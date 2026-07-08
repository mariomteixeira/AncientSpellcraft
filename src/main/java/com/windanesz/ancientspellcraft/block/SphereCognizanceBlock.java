package com.windanesz.ancientspellcraft.block;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.mojang.serialization.MapCodec;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Esfera da Cognicao (1.12.2 BlockSphereCognizance). TODO BER animado (esfera girando). */
public class SphereCognizanceBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = net.minecraft.world.level.block.Block.box(5, 0, 5, 11, 8, 11);

    public SphereCognizanceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(SphereCognizanceBlock::new);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull net.minecraft.world.level.BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof SphereCognizanceBlockEntity be) {
            be.setLastPlayer(player);
            player.openMenu(be);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (random.nextBoolean()) {
            ParticleBuilder.create(EBParticles.SPARKLE)
                    .pos(pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble() / 5 + 0.5, pos.getZ() + random.nextDouble())
                    .velocity(0, 0.01, 0).time(20 + random.nextInt(10))
                    .color(0.5f + random.nextFloat() / 5, 0.5f + random.nextFloat() / 5, 0.5f + random.nextFloat() / 2)
                    .spawn(level);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new SphereCognizanceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ASBlocks.SPHERE_COGNIZANCE_BE.get(), SphereCognizanceBlockEntity::serverTick);
    }
}
