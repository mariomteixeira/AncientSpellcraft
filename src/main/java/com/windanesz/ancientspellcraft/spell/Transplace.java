package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Troca de lugar com o alvo (1.12.2 Transplace). */
public class Transplace extends SageRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target) || ctx.caster() == null) return false;
        if (!ctx.world().isClientSide) {
            Vec3 casterPos = ctx.caster().position();
            Vec3 targetPos = target.position();
            ctx.caster().teleportTo(targetPos.x, targetPos.y, targetPos.z);
            target.teleportTo(casterPos.x, casterPos.y, casterPos.z);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        com.koomplo.wizardry.api.client.ParticleBuilder.create(
                com.koomplo.wizardry.setup.registries.client.EBParticles.DARK_MAGIC).pos(x, y, z).color(0x6a3f9e).spawn(ctx.world());
    }
}
