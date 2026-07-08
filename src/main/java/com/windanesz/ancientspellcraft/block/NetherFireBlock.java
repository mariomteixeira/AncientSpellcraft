package com.windanesz.ancientspellcraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/** Fogo infernal (1.12.2 BlockHellFire): fogo vanilla-like sem espalhar. */
public class NetherFireBlock extends BaseFireBlock {

    public NetherFireBlock(Properties properties) {
        super(properties, 2.0F);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseFireBlock> codec() {
        return simpleCodec(NetherFireBlock::new);
    }

    @Override
    protected boolean canBurn(@NotNull BlockState state) {
        return true;
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader world, @NotNull BlockPos pos) {
        return world.getBlockState(pos.below()).isSolidRender(world, pos.below());
    }
}
