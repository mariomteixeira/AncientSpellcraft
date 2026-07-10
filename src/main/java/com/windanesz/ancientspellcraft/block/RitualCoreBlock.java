package com.windanesz.ancientspellcraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Âncora dos rituais contínuos (AS-9c; substitui o TileRune do 1.12.2): fica no centro do ritual
 * e o BlockEntity mantém o efeito (arcane_barrier/condensing/forest). Quebrou -> ritual acaba.
 */
public class RitualCoreBlock extends BaseEntityBlock {

    public static final MapCodec<RitualCoreBlock> CODEC = simpleCodec(RitualCoreBlock::new);
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 0.5, 15);

    public RitualCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RitualCoreBlockEntity(pos, state);
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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof RitualCoreBlockEntity core) {
            core.popStoredItem();
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != com.windanesz.ancientspellcraft.registry.ASBlocks.RITUAL_CORE_BLOCK_ENTITY.get()) return null;
        return level.isClientSide
                ? (l, pos, s, be) -> ((RitualCoreBlockEntity) be).clientTick()
                : (l, pos, s, be) -> ((RitualCoreBlockEntity) be).serverTick();
    }
}
