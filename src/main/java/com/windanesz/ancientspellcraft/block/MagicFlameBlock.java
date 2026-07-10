package com.windanesz.ancientspellcraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Base das chamas mágicas do AS-14 (1.12.2 AbstractMagicFlame): bloco tipo fogo, sem colisão,
 * BE com lifetime/dono; arcane queima quem entra, wildfire persegue, teleportation teleporta.
 */
public class MagicFlameBlock extends BaseEntityBlock {

    public static final MapCodec<MagicFlameBlock> CODEC = simpleCodec(p -> new MagicFlameBlock(p, null, false));
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 14, 15);

    private final Supplier<BlockEntityType<? extends MagicFlameBlockEntity>> blockEntityType;
    private final boolean burnsEntities;

    public MagicFlameBlock(Properties properties,
                           Supplier<BlockEntityType<? extends MagicFlameBlockEntity>> blockEntityType,
                           boolean burnsEntities) {
        super(properties);
        this.blockEntityType = blockEntityType;
        this.burnsEntities = burnsEntities;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType == null ? null : blockEntityType.get().create(pos, state);
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
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) return;
        if (level.getBlockEntity(pos) instanceof MagicFlameBlockEntity flame && entity instanceof LivingEntity living) {
            flame.onEntityInside(living);
        }
        if (burnsEntities) {
            entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks(), 40));
        }
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (blockEntityType == null || type != blockEntityType.get() || level.isClientSide) return null;
        return (l, pos, s, be) -> ((MagicFlameBlockEntity) be).serverTick();
    }

}
