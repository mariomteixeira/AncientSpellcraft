package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.item.BattlemageSwordItem;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

/**
 * Runeword do battlemage (1.12.2 Runeword + 20 variantes): castar com a ESPADA de battlemage na
 * mão grava a palavra rúnica ativa com N cargas (component CustomData); cada golpe aplica o
 * efeito e gasta uma carga (leitura em ASRunewordEvents). Instants (restoration/endure/empower/
 * implode) resolvem no próprio cast. Desvios documentados: UMA runeword ativa por lâmina (o
 * original mantinha um mapa) e fury é armada por cast como as demais (no original era passiva
 * por glyph).
 */
public class RunewordSpell extends Spell implements ClassSpell {

    public static final String ACTIVE_TAG = "ActiveRuneword";
    public static final String CHARGES_TAG = "ActiveRunewordCharges";
    public static final String FURY_TAG = "FuryStacks";

    public static final SpellProperty<Integer> CHARGES = SpellProperty.intProperty("charges", 5);
    public static final SpellProperty<Integer> EFFECT_DURATION = SpellProperty.intProperty("effect_duration", 100);
    public static final SpellProperty<Float> DAMAGE_MULTIPLIER = SpellProperty.floatProperty("damage_multiplier", 1.5f);

    /** Efeito por golpe: (atacante, alvo, espada). */
    public interface HitEffect {
        void apply(RunewordSpell spell, Player attacker, LivingEntity target, ItemStack sword);
    }

    /** Modificador de dano por golpe. */
    public interface DamageModifier {
        float modify(RunewordSpell spell, float amount, Player attacker, LivingEntity target, ItemStack sword);
    }

    /** Cast instantâneo (não arma cargas). */
    public interface InstantEffect {
        boolean apply(RunewordSpell spell, PlayerCastContext ctx, ItemStack sword);
    }

    private HitEffect hitEffect;
    private DamageModifier damageModifier;
    private InstantEffect instantEffect;
    private boolean spendOnHit = true;
    private boolean continuous = false;

    public RunewordSpell continuous() {
        this.continuous = true;
        return this;
    }

    @Override
    public boolean isInstantCast() {
        return !continuous;
    }

    public RunewordSpell onHit(HitEffect effect) {
        this.hitEffect = effect;
        return this;
    }

    public RunewordSpell damage(DamageModifier modifier) {
        this.damageModifier = modifier;
        return this;
    }

    public RunewordSpell instant(InstantEffect effect) {
        this.instantEffect = effect;
        return this;
    }

    public RunewordSpell noSpend() {
        this.spendOnHit = false;
        return this;
    }

    public boolean spendsOnHit() {
        return spendOnHit;
    }

    public HitEffect hitEffect() {
        return hitEffect;
    }

    public DamageModifier damageModifier() {
        return damageModifier;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        ItemStack sword = ctx.caster().getMainHandItem();
        if (!(sword.getItem() instanceof BattlemageSwordItem)) return false;

        if (instantEffect != null) {
            boolean success = instantEffect.apply(this, ctx, sword);
            if (success) this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
            return success;
        }
        if (!ctx.world().isClientSide) {
            setActive(sword, this);
            // charm_glyph_charge (1.12.2): dobra as cargas ao armar
            if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(ctx.caster(),
                    com.windanesz.ancientspellcraft.registry.ASItems.CHARM_GLYPH_CHARGE.get())) {
                multiplyCharges(sword, 2);
            }
        } else {
            ParticleBuilder.create(EBParticles.SPARKLE, ctx.caster()).scale(0.8f).color(0xffffff)
                    .pos(0, 1.2, 0).time(15).spawn(ctx.world());
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    // ---- estado no component CustomData da espada ----

    public static void setActive(ItemStack sword, RunewordSpell runeword) {
        CustomData.update(DataComponents.CUSTOM_DATA, sword, tag -> {
            tag.putString(ACTIVE_TAG, runeword.getLocation().toString());
            tag.putInt(CHARGES_TAG, runeword.property(CHARGES));
            tag.remove(FURY_TAG);
        });
    }

    public static String getActive(ItemStack sword) {
        CompoundTag tag = sword.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getString(ACTIVE_TAG);
    }

    public static int getCharges(ItemStack sword) {
        return sword.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(CHARGES_TAG);
    }

    public static void multiplyCharges(ItemStack sword, int factor) {
        CustomData.update(DataComponents.CUSTOM_DATA, sword, tag ->
                tag.putInt(CHARGES_TAG, tag.getInt(CHARGES_TAG) * factor));
    }

    public static void spendCharge(ItemStack sword) {
        CustomData.update(DataComponents.CUSTOM_DATA, sword, tag -> {
            int charges = tag.getInt(CHARGES_TAG) - 1;
            if (charges <= 0) {
                tag.remove(ACTIVE_TAG);
                tag.remove(CHARGES_TAG);
                tag.remove(FURY_TAG);
            } else {
                tag.putInt(CHARGES_TAG, charges);
            }
        });
    }

    public static int getFuryStacks(ItemStack sword) {
        return sword.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(FURY_TAG);
    }

    public static void setFuryStacks(ItemStack sword, int stacks) {
        CustomData.update(DataComponents.CUSTOM_DATA, sword, tag -> tag.putInt(FURY_TAG, Math.max(0, stacks)));
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.BATTLEMAGE;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
