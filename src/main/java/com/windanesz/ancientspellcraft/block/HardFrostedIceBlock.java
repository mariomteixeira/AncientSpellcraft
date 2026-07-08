package com.windanesz.ancientspellcraft.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/** Gelo do cryostasis: derrete no scheduled tick agendado pela spell (1.12.2 BlockHardFrostedIce). */
public class HardFrostedIceBlock extends Block {

    public HardFrostedIceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        level.removeBlock(pos, false);
    }
}
