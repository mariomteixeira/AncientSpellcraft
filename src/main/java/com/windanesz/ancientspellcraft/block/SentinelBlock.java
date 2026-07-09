package com.windanesz.ancientspellcraft.block;

import com.mojang.serialization.MapCodec;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Base física da torreta sentinela (1.12.2 BlockSentinel): hitbox pequena, BE com a lógica. */
public class SentinelBlock extends BaseEntityBlock {

    public static final MapCodec<SentinelBlock> CODEC = simpleCodec(SentinelBlock::new);
    private static final VoxelShape SHAPE = Block.box(5, 0, 5, 11, 8, 11);

    public SentinelBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SentinelBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != ASBlocks.SENTINEL_BLOCK_ENTITY.get()) return null;
        return level.isClientSide
                ? (l, pos, s, be) -> ((SentinelBlockEntity) be).clientTick()
                : (l, pos, s, be) -> ((SentinelBlockEntity) be).serverTick();
    }
}
