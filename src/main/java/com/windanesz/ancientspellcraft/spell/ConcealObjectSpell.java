package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.windanesz.ancientspellcraft.block.ConcealedBlock;
import com.windanesz.ancientspellcraft.block.ConcealedBlockEntity;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Conceal Object (1.12.2 ConcealObject, class spell do SAGE): torna o bloco mirado invisível e
 * atravessável (estado + block entity guardados); re-cast no mesmo bloco reverte.
 */
public class ConcealObjectSpell extends SageRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (ctx.world().isClientSide) return true;
        BlockPos pos = blockHit.getBlockPos();
        BlockState state = ctx.world().getBlockState(pos);

        if (state.getBlock() instanceof ConcealedBlock) {
            if (ctx.world().getBlockEntity(pos) instanceof ConcealedBlockEntity concealed) {
                concealed.revert();
            }
            return true;
        }
        if (state.isAir() || state.getDestroySpeed(ctx.world(), pos) < 0) return false; // ar/inquebrável não esconde

        BlockEntity tile = ctx.world().getBlockEntity(pos);
        var tileData = tile == null ? null : tile.saveWithFullMetadata(ctx.world().registryAccess());
        if (tile != null) ctx.world().removeBlockEntity(pos);

        ctx.world().setBlockAndUpdate(pos, ASBlocks.CONCEALED_BLOCK.get().defaultBlockState());
        if (ctx.world().getBlockEntity(pos) instanceof ConcealedBlockEntity concealed) {
            concealed.store(state, tileData);
        }
        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }
}
