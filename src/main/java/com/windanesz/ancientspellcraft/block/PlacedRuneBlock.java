package com.windanesz.ancientspellcraft.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/** Runa desenhada no chao (1.12.2 BlockPlacedRune, decorativa). TODO padrao de ritual sobre runas colocadas. */
public class PlacedRuneBlock extends Block {

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 0.5, 15);

    public PlacedRuneBlock() {
        super(Properties.of().noCollission().instabreak().noLootTable()
                .lightLevel(state -> 3));
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                           @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }
}
