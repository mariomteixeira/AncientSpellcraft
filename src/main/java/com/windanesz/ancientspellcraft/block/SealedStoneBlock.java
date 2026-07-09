package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * Pedra selada das vaults (1.12.2 BlockSealedStone): inquebrável até ser "desselada" pelo
 * unseal button. UNSEALING > 0 contagia vizinhos seladas e progride via random tick até
 * virar unsealed_stone (quebrável).
 */
public class SealedStoneBlock extends Block {

    public static final IntegerProperty UNSEALING = IntegerProperty.create("unsealing", 0, 3);

    public SealedStoneBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(UNSEALING, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNSEALING);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int unsealing = state.getValue(UNSEALING);
        if (unsealing > 0 && unsealing < 3) {
            level.setBlockAndUpdate(pos, state.setValue(UNSEALING, unsealing + 1));
        } else if (unsealing == 3) {
            level.setBlockAndUpdate(pos, ASBlocks.UNSEALED_STONE.get().defaultBlockState());
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        BlockState neighbor = level.getBlockState(neighborPos);
        if (neighbor.getBlock() instanceof SealedStoneBlock && neighbor.getValue(UNSEALING) > 0
                && state.getValue(UNSEALING) == 0) {
            level.setBlockAndUpdate(pos, state.setValue(UNSEALING, 1));
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }
}
