package com.windanesz.ancientspellcraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Lunar Flax (sage_flax do 1.12.2): planta que se recolhe durante o dia. No original eram DOIS
 * blocos (sage_flax/sage_flax_day) trocados por updateTick; aqui é um bloco com propriedade DAY.
 * Colheita só com tesoura à noite (loot table); de dia não dropa nada.
 */
public class SageFlaxBlock extends BushBlock {

    public static final MapCodec<SageFlaxBlock> CODEC = simpleCodec(SageFlaxBlock::new);
    public static final BooleanProperty DAY = BooleanProperty.create("day");

    // 1.12.2: 0.4 de largura centrada; 0.6 de altura à noite, 0.2 de dia
    private static final VoxelShape NIGHT_SHAPE = Block.box(4.8, 0.0, 4.8, 11.2, 9.6, 11.2);
    private static final VoxelShape DAY_SHAPE = Block.box(4.8, 0.0, 4.8, 11.2, 3.2, 11.2);

    public SageFlaxBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(DAY, false));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DAY);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(DAY) ? DAY_SHAPE : NIGHT_SHAPE;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean day = level.isDay();
        if (state.getValue(DAY) != day) {
            level.setBlockAndUpdate(pos, state.setValue(DAY, day));
        }
    }
}
