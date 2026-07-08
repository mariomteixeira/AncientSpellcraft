package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.core.AllyDesignation;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * Obliteracao (1.12.2 Obliteration): carrega charging_time e libera uma onda elemental devastadora.
 * TODO EntityLevitatingBlock (blocos arremessados do original, codigo upstream incompleto).
 */
public class Obliteration extends Spell implements ClassSpell {

    public static final SpellProperty<Integer> CHARGING_TIME = SpellProperty.intProperty("charging_time");

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        int chargeup = property(CHARGING_TIME);
        Element element = elementOrMagic(ctx.caster());
        int[] colours = WarlockSpellEffects.colours(element);

        if (ctx.castingTicks() < chargeup) {
            if (ctx.world().isClientSide) {
                for (int i = 0; i < 10; i++) {
                    ParticleBuilder.create(EBParticles.FLASH, ctx.caster())
                            .scale(Math.min(1.0f, 2 / (Math.max(1.2f, ctx.castingTicks() * 0.2f) * 0.7f)))
                            .color(colours[ctx.world().random.nextInt(2)])
                            .pos(0, ctx.world().random.nextFloat() + 0.2f, 0).time(40).spawn(ctx.world());
                }
            }
            return true;
        }
        if (ctx.castingTicks() > chargeup + 1) return false;

        float radius = property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST);
        if (ctx.world().isClientSide) {
            var origin = ctx.caster().position();
            ParticleBuilder.create(EBParticles.SPHERE).time(10).pos(origin.add(0, 0.1, 0)).scale(radius * 0.8f).color(colours[0]).spawn(ctx.world());
            ParticleBuilder.create(EBParticles.SPHERE).time(20).pos(origin.add(0, 0.1, 0)).scale(radius * 0.8f).color(colours[0]).spawn(ctx.world());
            ParticleBuilder.create(EBParticles.FLASH).pos(origin.add(0, 0.1, 0)).scale(radius * 0.8f).color(colours[0]).time(60).spawn(ctx.world());
        } else {
            for (var target : EntityUtil.getLivingWithinRadius(radius,
                    ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.world())) {
                if (target == ctx.caster() || !AllyDesignation.isValidTarget(ctx.caster(), target)) continue;
                WarlockSpellEffects.affectEntity(target, element, ctx.caster(), true);
                EntityUtil.attackEntityWithoutKnockback(target,
                        com.koomplo.wizardry.api.content.util.MagicDamageSource.causeDirectMagicDamage(ctx.caster(),
                                com.koomplo.wizardry.setup.registries.EBDamageSources.MAGIC),
                        property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY));
            }
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.WARLOCK;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.FORBIDDEN_TOME.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
