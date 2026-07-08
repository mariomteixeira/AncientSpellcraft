package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.setup.registries.EBMobEffects;
import net.minecraft.world.effect.MobEffects;

/** Revela: remove invisibilidade/muffle de todos num raio (1.12.2 Unveil; TODO mirage). */
public class Unveil extends SageSpell {

    @Override
    public boolean cast(PlayerCastContext ctx) {
        double radius = property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST);
        if (!ctx.world().isClientSide) {
            for (var entity : EntityUtil.getLivingWithinRadius(radius, ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.world())) {
                if (entity == ctx.caster()) continue;
                entity.removeEffect(MobEffects.INVISIBILITY);
                entity.removeEffect(EBMobEffects.holder(EBMobEffects.MUFFLE));
            }
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }
}
