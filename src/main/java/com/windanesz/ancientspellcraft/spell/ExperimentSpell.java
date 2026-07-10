package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.RegistryUtils;
import com.koomplo.wizardry.content.ForfeitRegistry;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.core.platform.Services;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Experiment (1.12.2 Experiment, SAGE master): consome o "conhecimento não experimentado"
 * (spells descobertas ainda não usadas em experimentos) — chance de sucesso = base +
 * (elementos distintos)² por cento; sucesso dá 1 THEORY POINT (moeda do perfect_theory,
 * pendente); falha sorteia forfeit do Redux / buff / debuff / nada (pesos das properties).
 * Desvio documentado: amulet de +2% e artefato de tier ficam com artefatos.
 */
public class ExperimentSpell extends Spell implements ClassSpell {

    public static final String THEORY_POINTS_TAG = "TheoryPoints";
    public static final String EXPERIMENTED_TAG = "ExperimentedSpells";

    private static final SpellProperty<Float> BASE_SUCCESS_CHANCE = SpellProperty.floatProperty("base_success_chance", 0.05f);
    private static final SpellProperty<Float> FORFEIT_WEIGHT = SpellProperty.floatProperty("forfeit_weight", 40f);
    private static final SpellProperty<Float> BUFF_WEIGHT = SpellProperty.floatProperty("buff_weight", 15f);
    private static final SpellProperty<Float> DEBUFF_WEIGHT = SpellProperty.floatProperty("debuff_weight", 25f);
    private static final SpellProperty<Float> NO_EFFECT_WEIGHT = SpellProperty.floatProperty("no_effect_weight", 20f);

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (ctx.world().isClientSide) return true;
        CompoundTag tag = caster.getData(ASAttachments.PLAYER_DATA);

        List<String> experimented = new ArrayList<>();
        for (Tag t : tag.getList(EXPERIMENTED_TAG, Tag.TAG_STRING)) experimented.add(t.getAsString());

        var spellData = Services.OBJECT_DATA.getSpellManagerData(caster);
        List<Spell> usable = RegistryUtils.getSpells(s ->
                spellData.hasSpellBeenDiscovered(s) && !experimented.contains(s.getLocation().toString()));
        List<Element> distinctElements = usable.stream().map(Spell::getElement).distinct().toList();

        float chance = this.property(BASE_SUCCESS_CHANCE) + (float) (Math.pow(distinctElements.size(), 2) * 0.01);
        var random = ctx.world().random;

        if (random.nextFloat() <= chance) {
            ListTag list = tag.getList(EXPERIMENTED_TAG, Tag.TAG_STRING);
            for (Spell s : usable) list.add(StringTag.valueOf(s.getLocation().toString()));
            tag.put(EXPERIMENTED_TAG, list);
            tag.putInt(THEORY_POINTS_TAG, tag.getInt(THEORY_POINTS_TAG) + 1);
            caster.setData(ASAttachments.PLAYER_DATA, tag);
            caster.displayClientMessage(Component.translatable(
                    "spell.ancientspellcraft.experiment.received_point", tag.getInt(THEORY_POINTS_TAG)), false);
            this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
            return true;
        }

        float total = this.property(FORFEIT_WEIGHT) + this.property(BUFF_WEIGHT)
                + this.property(DEBUFF_WEIGHT) + this.property(NO_EFFECT_WEIGHT);
        float roll = random.nextFloat() * total;
        if ((roll -= this.property(FORFEIT_WEIGHT)) < 0) {
            // tier menor quanto mais elementos pesquisados (1.12.2)
            int tierIndex = random.nextInt(Math.max(1, Math.round(3 - distinctElements.size() / 3f)));
            var tiers = Services.REGISTRY_UTIL.getTiers().stream().toList();
            var forfeit = ForfeitRegistry.getRandomForfeit(new java.util.Random(random.nextLong()),
                    tiers.get(Math.min(tierIndex, tiers.size() - 1)), RegistryUtils.getRandomElement(random));
            if (forfeit != null) {
                forfeit.apply(ctx.world(), caster);
                recordExperiment(tag, "forfeit", forfeit.getName().toString());
            }
        } else if ((roll -= this.property(BUFF_WEIGHT)) < 0) {
            var effect = random.nextBoolean() ? MobEffects.REGENERATION : MobEffects.DAMAGE_RESISTANCE;
            caster.addEffect(new MobEffectInstance(effect, 600, 1));
            recordExperiment(tag, "mob_effect", effectString(effect));
        } else if (roll - this.property(DEBUFF_WEIGHT) < 0) {
            var effect = random.nextBoolean() ? MobEffects.WEAKNESS : MobEffects.MOVEMENT_SLOWDOWN;
            caster.addEffect(new MobEffectInstance(effect, 600, 1));
            recordExperiment(tag, "mob_effect", effectString(effect));
        } else {
            caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.experiment.no_effect"), true);
        }
        caster.setData(ASAttachments.PLAYER_DATA, tag);
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    /** Grava o resultado para o perfect_theory reproduzir depois. */
    private static void recordExperiment(CompoundTag tag, String type, String effect) {
        CompoundTag last = new CompoundTag();
        last.putString("Type", type);
        last.putString("Effect", effect);
        tag.put(PerfectTheoryCastSpell.LAST_EXPERIMENT_TAG, last);
    }

    private static String effectString(net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect) {
        return net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getKey(effect.value()) + ",600,1";
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.SAGE;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.MYSTIC_SPELL_BOOK.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
