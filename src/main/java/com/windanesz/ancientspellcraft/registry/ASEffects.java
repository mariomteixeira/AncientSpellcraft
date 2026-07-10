package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.potion.ASCurseMobEffect;
import com.windanesz.ancientspellcraft.potion.ASMobEffect;
import com.windanesz.ancientspellcraft.potion.LightSourceEffect;
import com.windanesz.ancientspellcraft.potion.ManaRegenerationEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.windanesz.ancientspellcraft.AncientSpellcraft.MODID;

/** Os 48 efeitos do 1.12.2 (registry/ASPotions.java). Shells marcados com TODO ganham logica no lote do sistema deles. */
public final class ASEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, MODID);

    public static final DeferredHolder<MobEffect, MobEffect> AQUATIC_AGILITY = EFFECTS.register("aquatic_agility",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0x0EB6B1)); // TODO logica no handler do sistema correspondente (registrado como efeito puro por enquanto)

    public static final DeferredHolder<MobEffect, MobEffect> ARCANE_AEGIS = EFFECTS.register("arcane_aegis",
            () -> new ASMobEffect(MobEffectCategory.HARMFUL, 0xf0fafa)); // TODO logica no handler do sistema correspondente (registrado como efeito puro por enquanto)

    public static final DeferredHolder<MobEffect, MobEffect> ARCANE_AUGMENTATION = EFFECTS.register("arcane_augmentation",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xf0fafa)); // TODO metamagic: hook de leitura nos modifiers de cast (sistema em lote futuro)

    public static final DeferredHolder<MobEffect, MobEffect> ASTRAL_PROJECTION = EFFECTS.register("astral_projection",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xffc800)); // logica portada em ASPotionEvents (AS-2b)

    public static final DeferredHolder<MobEffect, MobEffect> BUBBLE_HEAD = EFFECTS.register("bubble_head",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xE6E6FF)); // logica portada em ASPotionEvents (AS-2b)

    public static final DeferredHolder<MobEffect, MobEffect> BULWARK = EFFECTS.register("bulwark",
            () -> new ASMobEffect(MobEffectCategory.HARMFUL, 0xf0fafa)); // TODO logica no handler do sistema correspondente (registrado como efeito puro por enquanto)

    public static final DeferredHolder<MobEffect, MobEffect> CHAOS = EFFECTS.register("chaos",
            () -> new ASMobEffect(MobEffectCategory.HARMFUL, 0xfc0303)); // TODO logica no handler do sistema correspondente (registrado como efeito puro por enquanto)

    public static final DeferredHolder<MobEffect, MobEffect> CONTINUITY_CHARM = EFFECTS.register("continuity_charm",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xf0fafa)); // TODO metamagic: hook de leitura nos modifiers de cast (sistema em lote futuro)

    public static final DeferredHolder<MobEffect, MobEffect> CURSE_OF_DEATH = EFFECTS.register("curse_of_death",
            () -> new ASCurseMobEffect(MobEffectCategory.HARMFUL, 0x1c0b20)); // logica portada em ASPotionEvents (AS-2b)

    public static final DeferredHolder<MobEffect, MobEffect> CURSE_OF_ENDER = EFFECTS.register("curse_of_ender",
            () -> new ASCurseMobEffect(MobEffectCategory.HARMFUL, 0x571e65)); // TODO logica da curse (AS-2b/handler)

    public static final DeferredHolder<MobEffect, MobEffect> CURSE_OF_ETERNAL_COMBUSTION = EFFECTS.register("curse_of_eternal_combustion",
            () -> new ASCurseMobEffect(MobEffectCategory.HARMFUL, 0xFF4500)); // TODO logica da curse (AS-2b/handler)

    public static final DeferredHolder<MobEffect, MobEffect> CURSE_OF_ETERNAL_FROST = EFFECTS.register("curse_of_eternal_frost",
            () -> new ASCurseMobEffect(MobEffectCategory.HARMFUL, 0xFF4500)); // TODO logica da curse (AS-2b/handler)

    public static final DeferredHolder<MobEffect, MobEffect> CURSE_OF_ETERNAL_TEMPEST = EFFECTS.register("curse_of_eternal_tempest",
            () -> new ASCurseMobEffect(MobEffectCategory.HARMFUL, 0xADD8E6)); // TODO logica da curse (AS-2b/handler)

    public static final DeferredHolder<MobEffect, MobEffect> CURSE_OF_GILLS = EFFECTS.register("curse_of_gills",
            () -> new ASCurseMobEffect(MobEffectCategory.HARMFUL, 0x4287f5)); // TODO logica da curse (AS-2b/handler)

    public static final DeferredHolder<MobEffect, MobEffect> CURSE_OF_INSOMNIA = EFFECTS.register("curse_of_insomnia",
            () -> new ASCurseMobEffect(MobEffectCategory.HARMFUL, 0x483D8B)); // TODO logica da curse (AS-2b/handler)

    public static final DeferredHolder<MobEffect, MobEffect> CURSE_OF_UMBRA = EFFECTS.register("curse_of_umbra",
            () -> new ASCurseMobEffect(MobEffectCategory.HARMFUL, 0x000000)); // TODO logica da curse (AS-2b/handler)

    public static final DeferredHolder<MobEffect, MobEffect> CURSE_TEMPORAL_CASUALTY = EFFECTS.register("curse_temporal_casualty",
            () -> new ASCurseMobEffect(MobEffectCategory.HARMFUL, 0xD81A0B)); // TODO logica da curse (AS-2b/handler)

    public static final DeferredHolder<MobEffect, MobEffect> CURSE_WARD = EFFECTS.register("curse_ward",
            () -> new ASCurseMobEffect(MobEffectCategory.BENEFICIAL, 0xfcfde4)); // logica portada em ASPotionEvents (AS-2b)

    public static final DeferredHolder<MobEffect, MobEffect> DIMENSIONAL_ANCHOR = EFFECTS.register("dimensional_anchor",
            () -> new ASMobEffect(MobEffectCategory.HARMFUL, 0xc558d6)); // TODO logica no handler do sistema correspondente (registrado como efeito puro por enquanto)

    public static final DeferredHolder<MobEffect, MobEffect> EAGLE_EYE = EFFECTS.register("eagle_eye",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xffc800)); // logica portada em ASPotionEvents (AS-2b)

    public static final DeferredHolder<MobEffect, MobEffect> FORTIFIED_ARCHERY = EFFECTS.register("fortified_archery",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0x166630)); // TODO logica no handler do sistema correspondente (registrado como efeito puro por enquanto)

    public static final DeferredHolder<MobEffect, MobEffect> INTENSIFYING_FOCUS = EFFECTS.register("intensifying_focus",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xf0fafa)); // TODO metamagic: hook de leitura nos modifiers de cast (sistema em lote futuro)

    public static final DeferredHolder<MobEffect, MobEffect> LAVA_VISION = EFFECTS.register("lava_vision",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xF04900)); // TODO logica no handler do sistema correspondente (registrado como efeito puro por enquanto)

    public static final DeferredHolder<MobEffect, MobEffect> MAGICAL_EXHAUSTION = EFFECTS.register("magical_exhaustion",
            () -> new ASMobEffect(MobEffectCategory.HARMFUL, 0x635a63)); // TODO logica no handler do sistema correspondente (registrado como efeito puro por enquanto)

    public static final DeferredHolder<MobEffect, MobEffect> MAGMA_STRIDER = EFFECTS.register("magma_strider",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xD81A0B)); // TODO logica no handler do sistema correspondente (registrado como efeito puro por enquanto)

    public static final DeferredHolder<MobEffect, MobEffect> MARTYR = EFFECTS.register("martyr",
            () -> new ASMobEffect(MobEffectCategory.HARMFUL, 0x0f000f)); // TODO logica no handler do sistema correspondente (registrado como efeito puro por enquanto)

    public static final DeferredHolder<MobEffect, MobEffect> MARTYR_BENEFICIAL = EFFECTS.register("martyr_beneficial",
            () -> new ASMobEffect(MobEffectCategory.HARMFUL, 0x0f000f)); // TODO logica no handler do sistema correspondente (registrado como efeito puro por enquanto)

    public static final DeferredHolder<MobEffect, MobEffect> PROJECTILE_WARD = EFFECTS.register("projectile_ward",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xf0fafa)); // TODO logica no handler do sistema correspondente (registrado como efeito puro por enquanto)

    public static final DeferredHolder<MobEffect, MobEffect> SOUL_SCORCH = EFFECTS.register("soul_scorch",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xba3500)); // logica portada em ASPotionEvents (AS-2b)

    public static final DeferredHolder<MobEffect, MobEffect> SPELL_BLAST = EFFECTS.register("spell_blast",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xc558d6)); // TODO metamagic: hook de leitura nos modifiers de cast (sistema em lote futuro)

    public static final DeferredHolder<MobEffect, MobEffect> SPELL_COOLDOWN = EFFECTS.register("spell_cooldown",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xc558d6)); // TODO metamagic: hook de leitura nos modifiers de cast (sistema em lote futuro)

    public static final DeferredHolder<MobEffect, MobEffect> SPELL_DURATION = EFFECTS.register("spell_duration",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xc558d6)); // TODO metamagic: hook de leitura nos modifiers de cast (sistema em lote futuro)

    public static final DeferredHolder<MobEffect, MobEffect> SPELL_RANGE = EFFECTS.register("spell_range",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xc558d6)); // TODO metamagic: hook de leitura nos modifiers de cast (sistema em lote futuro)

    public static final DeferredHolder<MobEffect, MobEffect> SPELL_SIPHON = EFFECTS.register("spell_siphon",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xc558d6)); // TODO metamagic: hook de leitura nos modifiers de cast (sistema em lote futuro)

    public static final DeferredHolder<MobEffect, MobEffect> TENACITY = EFFECTS.register("tenacity",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xE6E6FF)); // TODO logica no handler do sistema correspondente (registrado como efeito puro por enquanto)

    public static final DeferredHolder<MobEffect, MobEffect> UNLIMITED_POWER = EFFECTS.register("unlimited_power",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xe65aff)); // logica portada em ASPotionEvents (AS-2b)

    public static final DeferredHolder<MobEffect, MobEffect> MANA_REGENERATION = EFFECTS.register("mana_regeneration",
            ManaRegenerationEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> MAGELIGHT = EFFECTS.register("magelight",
            () -> new LightSourceEffect(0xFAFCCC, () -> ASBlocks.MAGELIGHT.get()));

    public static final DeferredHolder<MobEffect, MobEffect> CANDLELIGHT = EFFECTS.register("candlelight",
            () -> new LightSourceEffect(0xFAFCCC, () -> ASBlocks.CANDLELIGHT.get()));

    /** 1.12.2 PotionWaterWalking: anda por cima d'agua. */
    public static final DeferredHolder<MobEffect, MobEffect> WATER_WALKING = EFFECTS.register("water_walking",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xE6E6FF, (entity, amplifier) -> {
                if (entity.isInWater()) {
                    var motion = entity.getDeltaMovement();
                    entity.setDeltaMovement(motion.x * (1.1f + 0.025 * 1.2),
                            motion.y < 0 ? 0.001f : motion.y, motion.z * (1.1f + 0.025 * 1.2));
                }
            }));

    /** 1.12.2 PotionFeatherFall: queda amortecida. */
    public static final DeferredHolder<MobEffect, MobEffect> FEATHER_FALL = EFFECTS.register("feather_fall",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xE6E6FF, (entity, amplifier) -> {
                entity.fallDistance = 0;
                var motion = entity.getDeltaMovement();
                if (motion.y < -0.3f) entity.setDeltaMovement(motion.x, motion.y + 0.1f, motion.z);
            }));

    /** 1.12.2 PotionTimeKnot: so o visual aqui; o rewind fica na spell time_knot (AS-2d). */
    public static final DeferredHolder<MobEffect, MobEffect> TIME_KNOT = EFFECTS.register("time_knot",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xD81A0B));

    /** 1.12.2 PotionBurrow: bolsao de ar ao redor (a escavacao real e da spell burrow, lote dos rays). */
    public static final DeferredHolder<MobEffect, MobEffect> BURROW = EFFECTS.register("burrow",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xe65aff)); // TODO makeAirPocket (depende da spell burrow)

    public static final DeferredHolder<MobEffect, MobEffect> IMPROVED_ARMOR = EFFECTS.register("improved_armor",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0x000000).addAttributeModifier(Attributes.ARMOR,
                    ResourceLocation.fromNamespaceAndPath(MODID, "improved_armor"), 2D, AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredHolder<MobEffect, MobEffect> DEGRADED_ARMOR = EFFECTS.register("degraded_armor",
            () -> new ASMobEffect(MobEffectCategory.HARMFUL, 0x000000).addAttributeModifier(Attributes.ARMOR,
                    ResourceLocation.fromNamespaceAndPath(MODID, "degraded_armor"), -2D, AttributeModifier.Operation.ADD_VALUE));

    /** 1.12.2 PotionGrowth; escala via Attributes.SCALE substitui o ArtemisLib. */
    public static final DeferredHolder<MobEffect, MobEffect> GROWTH = EFFECTS.register("growth",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0x8B4513)
                    .addAttributeModifier(Attributes.SCALE, rl("growth_scale"), 0.5D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_SPEED, rl("growth_atk_speed"), -0.2D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, rl("growth_kb"), 0.5D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, rl("growth_dmg"), 0.25D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.MAX_HEALTH, rl("growth_hp"), 0.3D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.ARMOR, rl("growth_armor"), 2D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ARMOR_TOUGHNESS, rl("growth_toughness"), 1D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    /** 1.12.2 PotionShrinkage; escala via Attributes.SCALE substitui o ArtemisLib. */
    public static final DeferredHolder<MobEffect, MobEffect> SHRINKAGE = EFFECTS.register("shrinkage",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0x9370DB)
                    .addAttributeModifier(Attributes.SCALE, rl("shrink_scale"), -0.5D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_SPEED, rl("shrink_atk_speed"), 0.25D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, rl("shrink_speed"), 0.1D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, rl("shrink_kb"), -0.5D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, rl("shrink_dmg"), -0.25D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.MAX_HEALTH, rl("shrink_hp"), -0.25D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.ARMOR, rl("shrink_armor"), -2D, AttributeModifier.Operation.ADD_VALUE));

    /** 1.12.2 PotionWizardShield: kb resist total + absorption em cascata (renova amplifier-2 a cada
     * expiracao; com head_shield o decaimento cai para -1 e cada renovacao dura 70t). */
    public static final DeferredHolder<MobEffect, MobEffect> WIZARD_SHIELD = EFFECTS.register("wizard_shield",
            () -> new ASMobEffect(MobEffectCategory.BENEFICIAL, 0xc558d6, (entity, amplifier) -> {
                var instance = entity.getEffect(WIZARD_SHIELD_HOLDER());
                if (instance != null && instance.getDuration() == 40 && !entity.level().isClientSide) {
                    boolean headShield = entity instanceof net.minecraft.world.entity.player.Player player
                            && com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                            com.windanesz.ancientspellcraft.registry.ASItems.HEAD_SHIELD.get());
                    int newAmplifier = amplifier - (headShield ? 1 : 2);
                    if (newAmplifier >= 0) {
                        entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                WIZARD_SHIELD_HOLDER(), headShield ? 70 : 60, newAmplifier));
                    }
                }
            }).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE,
                    rl("wizard_shield_kb"), 1.0D, AttributeModifier.Operation.ADD_VALUE));

    private static net.minecraft.core.Holder<MobEffect> WIZARD_SHIELD_HOLDER() {
        return WIZARD_SHIELD;
    }

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private ASEffects() {
    }
}
