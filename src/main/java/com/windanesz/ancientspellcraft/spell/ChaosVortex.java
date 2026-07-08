package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.core.AllyDesignation;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/** Vortice do caos (1.12.2 ChaosVortex): canaliza um vortice elemental que fere tudo no raio a cada 10t. */
public class ChaosVortex extends Spell implements ClassSpell {

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Element element = elementOrMagic(ctx.caster());
        float radius = Math.min(5.0f, ctx.castingTicks() * 0.2f) * 2 * ctx.modifiers().get(SpellModifiers.BLAST);

        if (ctx.castingTicks() % 40 == 0) this.playSoundLoop(ctx.world(), ctx.caster(), ctx.castingTicks());

        if (ctx.world().isClientSide) {
            int[] colours = WarlockSpellEffects.colours(element);
            for (int i = 0; i < 10; i++) {
                ParticleBuilder.create(EBParticles.FLASH, ctx.caster()).scale(2)
                        .color(colours[ctx.world().random.nextInt(2)])
                        .pos(0, ctx.world().random.nextInt(10) + 0.5, 0).time(40).spawn(ctx.world());
                ParticleBuilder.create(WarlockSpellEffects.particle(element), ctx.caster())
                        .scale(ctx.world().random.nextFloat() * 2)
                        .color(colours[element.getName().equals("lightning") ? 3 : ctx.world().random.nextInt(2)])
                        .pos(0, ctx.world().random.nextInt(10) + 0.5, 0).time(40).spawn(ctx.world());
            }
        }
        if (ctx.caster().tickCount % 10 == 0) {
            float fullRadius = property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST);
            for (var target : EntityUtil.getLivingWithinRadius(fullRadius * 0.5f,
                    ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.world())) {
                if (target == ctx.caster() || !AllyDesignation.isValidTarget(ctx.caster(), target)) continue;
                WarlockSpellEffects.affectEntity(target, element, ctx.caster(), true);
            }
        }
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
