package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.material.IDevoritium;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.jetbrains.annotations.NotNull;

/** Porta de devoritium (1.12.2 BlockDevoritiumDoor): anti-magia ao pisar/colidir. */
public class DevoritiumDoorBlock extends DoorBlock implements IDevoritium {

    public DevoritiumDoorBlock(BlockSetType type, Properties properties) {
        super(type, properties);
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
