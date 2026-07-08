package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Interrompe o cast do alvo (1.12.2 Counterspell). TODO cooldown forcado na wand do alvo. */
public class Counterspell extends SageRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return false;
        if (!ctx.world().isClientSide && target.isUsingItem()) {
            target.stopUsingItem();
            return true;
        }
        return !ctx.world().isClientSide ? false : true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        com.koomplo.wizardry.api.client.ParticleBuilder.create(
                com.koomplo.wizardry.setup.registries.client.EBParticles.FLASH).pos(x, y, z).color(0xd94444).spawn(ctx.world());
    }
}
