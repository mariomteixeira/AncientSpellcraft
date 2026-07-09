package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.block.TemporaryBlockEntity;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Muro arcano (1.12.2 ArcaneWall): parede de blocos arcanos temporarios perpendicular ao olhar;
 * agachado mirando num bloco arcano, dissipa-o. TODO TileArcaneWall.isGenerated (muros de worldgen).
 */
public class ArcaneWall extends SageRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (ctx.caster().isShiftKeyDown() && ctx.world().getBlockState(blockHit.getBlockPos()).is(ASBlocks.ARCANE_WALL.get())) {
            if (!ctx.world().isClientSide) {
                ctx.world().removeBlock(blockHit.getBlockPos(), false);
            } else {
                ParticleBuilder.create(EBParticles.FLASH).pos(blockHit.getBlockPos().getX() + 0.5,
                                blockHit.getBlockPos().getY() + 0.5, blockHit.getBlockPos().getZ() + 0.5)
                        .scale(3).color(0x9d2cf3).spawn(ctx.world());
            }
            return true;
        }
        if (ctx.world().isClientSide) return true;

        BlockPos pos = blockHit.getBlockPos().above();
        Direction direction = ctx.caster().getDirection().getClockWise();
        int half = 2 + (int) ((ctx.modifiers().get(SpellModifiers.BLAST) - 1) / 0.25f + 0.5f);
        int lifetime = (int) (600 * ctx.modifiers().get(SpellModifiers.DURATION));
        boolean placed = false;
        for (int i = -half + 1; i < half; i++) {
            for (int j = 0; j < 2; j++) {
                BlockPos wallPos = pos.relative(direction, i).above(j);
                if (BlockUtil.canBlockBeReplaced(ctx.world(), wallPos)
                        && BlockUtil.canPlaceBlock(ctx.caster(), ctx.world(), wallPos)) {
                    TemporaryBlockEntity.place(ctx.caster(), ctx.world(), ASBlocks.ARCANE_WALL.get(), wallPos, lifetime);
                    placed = true;
                }
            }
        }
        if (placed) this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return placed;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DUST).pos(x, y, z).color(0x9d2cf3).spawn(ctx.world());
    }
}
