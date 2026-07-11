package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.material.IDevoritium;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/** Grades de devoritium (1.12.2 BlockDevoritiumBars): anti-magia ao pisar/colidir. */
public class DevoritiumBarsBlock extends IronBarsBlock implements IDevoritium {

    public DevoritiumBarsBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        onEntityWalkDelegate(level, pos, entity);
        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        onEntityCollisionDelegate(level, pos, state, entity);
        super.entityInside(state, level, pos, entity);
    }
}
