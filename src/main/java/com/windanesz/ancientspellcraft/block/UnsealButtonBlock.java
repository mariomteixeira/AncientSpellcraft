package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Botão de desselamento das vaults (1.12.2 BlockUnsealButton, bloco CHEIO inquebrável):
 * clicar inicia o desselamento das sealed_stone vizinhas e o próprio botão vira unsealed_stone.
 */
public class UnsealButtonBlock extends Block {

    public UnsealButtonBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.relative(direction);
                BlockState neighbor = level.getBlockState(neighborPos);
                if (neighbor.getBlock() instanceof SealedStoneBlock && neighbor.getValue(SealedStoneBlock.UNSEALING) == 0) {
                    level.setBlockAndUpdate(neighborPos, neighbor.setValue(SealedStoneBlock.UNSEALING, 1));
                }
            }
            level.setBlockAndUpdate(pos, ASBlocks.UNSEALED_STONE.get().defaultBlockState());
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
