package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.setup.registries.EBBlocks;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Muro espectral (1.12.2 SpectralWall): ergue uma parede de blocos espectrais perpendicular ao
 * olhar (600t); agachado mirando num bloco espectral, dissipa-o. Desvio documentado: colocacao
 * instantanea (o original construia gradualmente via EntityBuilder).
 */
public class SpectralWall extends SageRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        var pos = blockHit.getBlockPos().above();
        if (ctx.caster().isShiftKeyDown() && ctx.world().getBlockState(blockHit.getBlockPos()).is(EBBlocks.SPECTRAL_BLOCK.get())) {
            if (!ctx.world().isClientSide) {
                ctx.world().removeBlock(blockHit.getBlockPos(), false);
            } else {
                ParticleBuilder.create(EBParticles.FLASH).pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                        .scale(3).color(0.75f, 1, 0.85f).spawn(ctx.world());
            }
            return true;
        }
        if (ctx.world().isClientSide) return true;

        Direction direction = ctx.caster().getDirection().getClockWise();
        int half = 3 + (int) ((ctx.modifiers().get(SpellModifiers.BLAST) - 1) / 0.25f + 0.5f);
        int lifetime = 600;
        boolean placed = false;
        for (int i = -half + 1; i < half; i++) {
            for (int j = 0; j < 3; j++) {
                BlockPos wallPos = pos.relative(direction, i).above(j);
                if (BlockUtil.canBlockBeReplaced(ctx.world(), wallPos)
                        && BlockUtil.canPlaceBlock(ctx.caster(), ctx.world(), wallPos)) {
                    ctx.world().setBlockAndUpdate(wallPos, EBBlocks.SPECTRAL_BLOCK.get().defaultBlockState());
                    ctx.world().scheduleTick(wallPos.immutable(), EBBlocks.SPECTRAL_BLOCK.get(), lifetime);
                    placed = true;
                }
            }
        }
        if (placed) this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return placed;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DUST).pos(x, y, z).color(0.75f, 1f, 0.85f).spawn(ctx.world());
    }
}
