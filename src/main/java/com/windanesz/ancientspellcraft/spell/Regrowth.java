package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Regen em si mesmo; agachado vira um raio que aplica o regen num aliado a distancia (1.12.2 Regrowth). */
public class Regrowth extends ASBuffSpell {

    @SuppressWarnings("unchecked")
    public Regrowth() {
        super(201 / 255f, 90 / 255f, 168 / 255f, () -> net.minecraft.core.Holder.direct(MobEffects.REGENERATION.value()));
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.caster().isShiftKeyDown()) {
            double range = property(DefaultProperties.RANGE) * ctx.modifiers().get(SpellModifiers.RANGE);
            LivingEntity hit = rayTarget(ctx.caster(), range);
            if (hit == null) return false;
            boolean result = applyEffects(ctx, hit);
            if (result) this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
            return result;
        }
        return super.cast(ctx);
    }

    private static LivingEntity rayTarget(LivingEntity caster, double range) {
        Vec3 origin = caster.getEyePosition();
        Vec3 look = caster.getLookAngle();
        for (double d = 1; d <= range; d += 0.5) {
            Vec3 point = origin.add(look.scale(d));
            for (LivingEntity entity : caster.level().getEntitiesOfClass(LivingEntity.class,
                    new AABB(point.subtract(0.5, 0.5, 0.5), point.add(0.5, 0.5, 0.5)))) {
                if (entity != caster) return entity;
            }
        }
        return null;
    }
}
