package com.windanesz.ancientspellcraft.block;

import com.koomplo.wizardry.content.item.SpellBookItem;
import com.mojang.serialization.MapCodec;
import com.windanesz.ancientspellcraft.item.SageTomeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Containers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Sage Lectern (1.12.2 BlockSageLectern): estação da classe SAGE. Segura um livro (tomo, spell
 * book ou mystic book); clique com tomo/livro coloca, sneak-clique de mão vazia devolve, clique
 * normal com tomo dentro abre a GUI de progressão. Desvios: sem render de livro animado
 * (modelo estático do AS-8c) e sem lectern "natural" com spell aleatória nas estruturas.
 */
public class SageLecternBlock extends BaseEntityBlock {

    public static final MapCodec<SageLecternBlock> CODEC = simpleCodec(p -> new SageLecternBlock());

    public SageLecternBlock() {
        super(Properties.of().mapColor(net.minecraft.world.level.material.MapColor.WOOD)
                .strength(2.0F, 5.0F).noOcclusion()
                .sound(net.minecraft.world.level.block.SoundType.STONE));
        registerDefaultState(stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, net.minecraft.core.Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new SageLecternBlockEntity(pos, state);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level,
                                                       @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof SageLecternBlockEntity lectern)) return ItemInteractionResult.FAIL;

        // colocar livro (tomo, spell book do Redux/AS, mystic book)
        if (!lectern.hasBook() && (stack.getItem() instanceof SageTomeItem || stack.getItem() instanceof SpellBookItem)) {
            if (!level.isClientSide) {
                lectern.setItem(SageLecternBlockEntity.BOOK, stack.split(1));
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof SageLecternBlockEntity lectern)) return InteractionResult.FAIL;

        // sneak devolve o livro
        if (player.isShiftKeyDown()) {
            if (lectern.hasBook() && !level.isClientSide) {
                player.getInventory().placeItemBackInInventory(lectern.getBook().copy());
                lectern.setItem(SageLecternBlockEntity.BOOK, ItemStack.EMPTY);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // GUI de progressão (com ou sem tomo dentro)
        if (!level.isClientSide) {
            player.openMenu(lectern);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof SageLecternBlockEntity lectern) {
            // não dropa o slot de resultado (é derivado dos inputs)
            for (int i = 0; i < SageLecternBlockEntity.RESULT; i++) {
                ItemStack stack = lectern.getItem(i);
                if (!stack.isEmpty()) Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
