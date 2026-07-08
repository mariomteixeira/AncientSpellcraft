package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.block.TemporaryBlockEntity;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Muralha de fogo de 3 blocos perpendicular ao olhar (1.12.2 FireWall). */
public class FireWall extends ASRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (!ctx.world().isClientSide && ctx.caster() != null) {
            BlockPos pos = blockHit.getBlockPos().above();
            if (ctx.world().getBlockState(pos).canBeReplaced()) {
                Direction facing = ctx.caster().getDirection().getClockWise();
                for (BlockPos current : new BlockPos[]{pos, pos.relative(facing), pos.relative(facing.getOpposite())}) {
                    if (ctx.world().getBlockState(current).canBeReplaced()) {
                        ctx.world().setBlockAndUpdate(current, net.minecraft.world.level.block.Blocks.FIRE.defaultBlockState());
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).spawn(ctx.world());
    }
}
