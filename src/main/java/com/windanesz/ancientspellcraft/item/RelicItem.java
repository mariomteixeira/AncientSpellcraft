package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.SpellTier;
import com.koomplo.wizardry.api.content.util.RegistryUtils;
import com.koomplo.wizardry.setup.registries.Spells;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.data.SpellComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Reliquia completa (1.12.2 ItemRelic): tabuleta sem conteudo ate ser pesquisada; a pesquisa sorteia
 * SPELL (40%) / INCANTATION (15%) / ENCHANTMENT (25%) / POWER (20%) com conteudo pelo tier. SPELL vai
 * ao scribing desk; os outros tres canalizam 120t no right-click e consomem a tabuleta. Desvios: a
 * pesquisa e por Scroll of Identification na outra mao (o 1.12.2 usava a Sphere of Cognizance, ainda
 * nao portada) e os sons sao vanilla (sounds.json do pack nao portado).
 */
public class RelicItem extends Item {

    // nomes de tag do 1.12.2
    private static final String RESEARCHED = "researched";
    private static final String RELIC_TYPE = "relicType";
    private static final String DURATION = "duration";

    /** Pools do initEffects() 1.12.2; resolvidos em runtime, ausentes ignorados. */
    private static final List<ResourceLocation> INCANTATION_POOL = List.of(
            mc("speed"), mc("slowness"), mc("haste"), mc("mining_fatigue"), mc("strength"),
            mc("jump_boost"), mc("nausea"), mc("regeneration"), mc("resistance"), mc("fire_resistance"),
            mc("water_breathing"), mc("invisibility"), mc("blindness"), mc("night_vision"), mc("hunger"),
            mc("weakness"), mc("poison"), mc("wither"), mc("health_boost"), mc("absorption"),
            mc("glowing"), mc("levitation"),
            as("candlelight"), as("curse_ward"), as("magelight"), as("arcane_aegis"), as("bubble_head"),
            as("bulwark"), as("water_walking"), as("fortified_archery"), as("lava_vision"),
            as("feather_fall"), as("projectile_ward"), as("aquatic_agility"), as("magma_strider"),
            eb("muffle"), eb("transience"), eb("sixth_sense"), eb("arcane_jammer"), eb("fireskin"),
            eb("frost"), eb("ice_shroud"), eb("containment"), eb("curse_of_enfeeblement"),
            eb("curse_of_undeath"), eb("ward"), eb("static_aura"), eb("frost_step"), eb("font_of_mana"));

    private static final List<ResourceLocation> POWER_POOL = List.of(
            eb("empowerment"), as("spell_blast"), as("spell_range"), as("spell_duration"),
            as("spell_cooldown"), as("spell_siphon"), as("mana_regeneration"));

    /** Encantamentos do proprio wizardry excluidos do sorteio (lista do 1.12.2). */
    private static final List<ResourceLocation> EXCLUDED_ENCHANTS = List.of(
            eb("magic_sword"), eb("flaming_weapon"), eb("freezing_weapon"), eb("shocking_weapon"), eb("magic_bow"));

    private final SpellTier tier;

    public RelicItem(SpellTier tier, Rarity rarity) {
        super(new Properties().stacksTo(16).rarity(rarity));
        this.tier = tier;
    }

    public SpellTier getTier() {
        return tier;
    }

    public enum RelicType {
        SPELL("spell", ChatFormatting.GOLD),
        INCANTATION("incantation", ChatFormatting.BLUE),
        ENCHANTMENT("enchantment", ChatFormatting.DARK_PURPLE),
        POWER("power", ChatFormatting.DARK_AQUA);

        public final String name;
        public final ChatFormatting color;

        RelicType(String name, ChatFormatting color) {
            this.name = name;
            this.color = color;
        }

        @Nullable
        static RelicType fromName(String name) {
            for (RelicType type : values()) {
                if (type.name.equals(name)) return type;
            }
            return null;
        }
    }

    /** Reliquias antigas com spell atribuida contam como pesquisadas (compat com mundos do 3.x). */
    public static boolean isResearched(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (data.copyTag().getBoolean(RESEARCHED)) return true;
        return RegistryUtils.getSpell(stack) != Spells.NONE;
    }

    @Nullable
    public static RelicType getRelicType(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        RelicType type = RelicType.fromName(data.copyTag().getString(RELIC_TYPE));
        if (type == null && RegistryUtils.getSpell(stack) != Spells.NONE) return RelicType.SPELL;
        return type;
    }

    /** Pesquisa a reliquia (1.12.2 setRandomContentType com type=null): sorteia tipo e conteudo. */
    public static void research(ItemStack stack, Player player) {
        research(stack, player, null);
    }

    /** Variante com tipo forcado (1.12.2 setRandomContentType com type != null — charm_stone_tablet). */
    public static void research(ItemStack stack, Player player, @Nullable RelicType forced) {
        if (!(stack.getItem() instanceof RelicItem relic) || isResearched(stack)) return;
        RelicType rolled = forced;
        if (rolled == null) {
            float f = player.getRandom().nextFloat();
            if (f <= 0.4f) rolled = RelicType.SPELL;
            else if (f <= 0.55f) rolled = RelicType.INCANTATION;
            else if (f <= 0.8f) rolled = RelicType.ENCHANTMENT;
            else rolled = RelicType.POWER;
        }
        final RelicType type = rolled;

        switch (type) {
            case SPELL -> {
                List<Spell> spells = SpellComponents.spellsByTier(relic.tier);
                if (!spells.isEmpty()) {
                    RegistryUtils.setSpell(stack, spells.get(player.getRandom().nextInt(spells.size())));
                }
            }
            case INCANTATION -> setRandomEffect(stack, player, RelicType.INCANTATION, INCANTATION_POOL,
                    switch (relic.tier.getLevel()) {
                        case 1 -> 16 * 60 * 20;
                        case 2 -> 30 * 60 * 20;
                        case 3 -> 60 * 60 * 20;
                        default -> 10 * 60 * 20;
                    });
            case POWER -> setRandomEffect(stack, player, RelicType.POWER, POWER_POOL,
                    (5 + 5 * relic.tier.getLevel()) * 60 * 20);
            case ENCHANTMENT -> setRandomEnchantments(stack, player, relic.tier);
        }
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString(RELIC_TYPE, type.name);
            tag.putBoolean(RESEARCHED, true);
        });
    }

    private static void setRandomEffect(ItemStack stack, Player player, RelicType type,
                                        List<ResourceLocation> pool, int duration) {
        List<ResourceLocation> available = pool.stream()
                .filter(rl -> BuiltInRegistries.MOB_EFFECT.getHolder(rl).isPresent()).toList();
        if (available.isEmpty()) return;
        ResourceLocation chosen = available.get(player.getRandom().nextInt(available.size()));
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString(type.name, chosen.toString());
            tag.putInt(DURATION, duration);
        });
    }

    private static void setRandomEnchantments(ItemStack stack, Player player, SpellTier tier) {
        var lookup = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        List<Holder<Enchantment>> all = lookup.listElements()
                .filter(h -> !h.is(EnchantmentTags.CURSE))
                .filter(h -> EXCLUDED_ENCHANTS.stream().noneMatch(h::is))
                .map(h -> (Holder<Enchantment>) h).toList();
        if (all.isEmpty()) return;

        // itens de referencia do filtro "weird ones" do 1.12.2
        List<ItemStack> probes = List.of(new ItemStack(Items.DIAMOND_HELMET), new ItemStack(Items.DIAMOND_CHESTPLATE),
                new ItemStack(Items.DIAMOND_LEGGINGS), new ItemStack(Items.DIAMOND_BOOTS),
                new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.BOW),
                new ItemStack(Items.DIAMOND_HOE), new ItemStack(Items.DIAMOND_PICKAXE));

        int count = tier.getLevel() + 1;
        ItemEnchantments.Mutable stored = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (int j = 0; j < count; j++) {
            Holder<Enchantment> enchantment = all.get(player.getRandom().nextInt(all.size()));
            for (int i = 0; i < 5; i++) {
                Holder<Enchantment> candidate = enchantment;
                if (probes.stream().noneMatch(probe -> candidate.value().canEnchant(probe))) {
                    enchantment = all.get(player.getRandom().nextInt(all.size()));
                } else {
                    break;
                }
            }
            int min = Math.min(enchantment.value().getMinLevel() + count / 2, enchantment.value().getMaxLevel());
            int level = Mth.nextInt(player.getRandom(), min, enchantment.value().getMaxLevel());
            stored.set(enchantment, level);
        }
        stack.set(DataComponents.STORED_ENCHANTMENTS, stored.toImmutable());
    }

    private static Optional<Holder<MobEffect>> getStoredEffect(ItemStack stack, RelicType type) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        String name = data.copyTag().getString(type.name);
        if (name.isEmpty()) return Optional.empty();
        return BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(name)).map(h -> h);
    }

    private static int getStoredDuration(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(DURATION);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 120;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        RelicType type = getRelicType(stack);
        if (type == RelicType.INCANTATION || type == RelicType.POWER || type == RelicType.ENCHANTMENT) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remaining) {
        int usingTicks = getUseDuration(stack, entity) - remaining;
        RelicType type = getRelicType(stack);
        if (type == null) return;

        if (!level.isClientSide && usingTicks % 25 == 0) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    com.windanesz.ancientspellcraft.registry.ASSounds.RELIC_USE_LOOP.get(), SoundSource.NEUTRAL, 1.1f, 1f);
        }
        if (level.isClientSide && usingTicks % 10 == 0) {
            int color = switch (type) {
                case INCANTATION -> getStoredEffect(stack, RelicType.INCANTATION)
                        .map(h -> h.value().getColor()).orElse(0x10c7c1);
                case ENCHANTMENT -> 0x5c1461;
                default -> 0x10c7c1;
            };
            if (type == RelicType.ENCHANTMENT) {
                for (int i = 0; i < 10; i++) {
                    double dx = (level.random.nextDouble() * 2 - 1) * 3;
                    double dy = (level.random.nextDouble() * 2 - 1) * 3;
                    double dz = (level.random.nextDouble() * 2 - 1) * 3;
                    level.addParticle(ParticleTypes.ENCHANT, entity.getX(), entity.getY() + 1.5, entity.getZ(), dx, dy, dz);
                }
            }
            ParticleBuilder.create(EBParticles.FLASH, entity).pos(0, 0.1, 0).color(color)
                    .scale(4.3f).time(30).spawn(level);
            ParticleBuilder.create(EBParticles.FLASH, entity).pos(0, 0.1, 0).color(0x303030)
                    .scale(2.3f).time(40).spawn(level);
        }
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (!(entity instanceof Player player)) return stack;
        player.getCooldowns().addCooldown(this, 60);
        RelicType type = getRelicType(stack);
        if (type == RelicType.INCANTATION || type == RelicType.POWER) {
            if (!level.isClientSide) {
                var effect = getStoredEffect(stack, type);
                if (effect.isEmpty()) return stack;
                entity.addEffect(new MobEffectInstance(effect.get(), getStoredDuration(stack), 0));
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        com.windanesz.ancientspellcraft.registry.ASSounds.RELIC_ACTIVATE_2.get(), SoundSource.NEUTRAL, 0.9f, 1f);
                stack.shrink(1);
            }
            return stack;
        } else if (type == RelicType.ENCHANTMENT) {
            ItemStack offhand = player.getOffhandItem();
            if (offhand.isEmpty() || !offhand.isEnchantable() || level.isClientSide) return stack;

            ItemEnchantments storedEnchantments = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
            List<Holder<Enchantment>> current = new ArrayList<>(offhand.getEnchantments().keySet());

            // remove os incompativeis com o item e entre si (semantica do 1.12.2)
            List<Holder<Enchantment>> toApply = new ArrayList<>();
            for (var entry : storedEnchantments.entrySet()) {
                Holder<Enchantment> enchantment = entry.getKey();
                if (!EnchantmentHelper.isEnchantmentCompatible(current, enchantment)) continue;
                if (!toApply.stream().allMatch(other -> Enchantment.areCompatible(other, enchantment))) continue;
                toApply.add(enchantment);
            }
            boolean enchantedSomething = false;
            for (Holder<Enchantment> enchantment : toApply) {
                if (enchantment.value().canEnchant(offhand)) {
                    offhand.enchant(enchantment, storedEnchantments.getLevel(enchantment));
                    enchantedSomething = true;
                }
            }
            if (enchantedSomething) {
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        com.windanesz.ancientspellcraft.registry.ASSounds.RELIC_ACTIVATE.get(), SoundSource.NEUTRAL, 0.9f, 1f);
                stack.shrink(1);
            }
            return stack;
        }
        return stack;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return stack.getRarity() == Rarity.EPIC;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (!isResearched(stack)) {
            tooltip.add(Component.translatable("item.ancientspellcraft.relic.unidentified").withStyle(ChatFormatting.GRAY));
            return;
        }
        RelicType type = getRelicType(stack);
        if (type == null) return;
        tooltip.add(Component.translatable("relic_type." + type.name).withStyle(type.color, ChatFormatting.ITALIC));

        switch (type) {
            case SPELL -> {
                Spell spell = RegistryUtils.getSpell(stack);
                if (spell == Spells.NONE) return;
                tooltip.add(Component.translatable(spell.getDescriptionId()).withStyle(ChatFormatting.GOLD));
                var entry = SpellComponents.entryFor(spell);
                if (entry != null) {
                    tooltip.add(Component.translatable("relic_type.spell.required_components").withStyle(ChatFormatting.GRAY));
                    for (var item : SpellComponents.itemsOf(entry)) {
                        tooltip.add(Component.literal("  ").append(item.getDescription()).withStyle(ChatFormatting.DARK_AQUA));
                    }
                }
                tooltip.add(Component.translatable("relic_type.spell.instruction").withStyle(ChatFormatting.GRAY));
            }
            case INCANTATION, POWER -> {
                getStoredEffect(stack, type).ifPresent(effect -> tooltip.add(Component.literal(" ")
                        .append(effect.value().getDisplayName())
                        .append(" (" + StringUtil.formatTickDuration(getStoredDuration(stack), context.tickRate()) + ")")));
                tooltip.add(Component.translatable("relic_type." + type.name + ".instruction").withStyle(ChatFormatting.GRAY));
            }
            // ENCHANTMENT: a lista vem do proprio componente STORED_ENCHANTMENTS no tooltip
            case ENCHANTMENT -> tooltip.add(Component.translatable("relic_type.enchantment.instruction").withStyle(ChatFormatting.GRAY));
        }
    }

    private static ResourceLocation mc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    private static ResourceLocation as(String path) {
        return ResourceLocation.fromNamespaceAndPath("ancientspellcraft", path);
    }

    private static ResourceLocation eb(String path) {
        return ResourceLocation.fromNamespaceAndPath("ebwizardry", path);
    }
}
