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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Artefact Pensive (1.12.2 BlockArtefactPensive): banco de XP — clique deposita todo o XP do
 * jogador (cap 1395 = 30 níveis), shift-clique saca tudo. Property EMPTY controla o visual.
 */
public class ArtefactPensiveBlock extends BaseEntityBlock {

    public static final MapCodec<ArtefactPensiveBlock> CODEC = simpleCodec(ArtefactPensiveBlock::new);
    public static final BooleanProperty EMPTY = BooleanProperty.create("empty");
    public static final int MAX_XP = 1395; // 30 níveis (valor 1.12.2)
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 12, 14);

    public ArtefactPensiveBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(EMPTY, true));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EMPTY);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArtefactPensiveBlockEntity(pos, state);
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
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof ArtefactPensiveBlockEntity pensive)) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            player.giveExperiencePoints(pensive.getStoredXp());
            pensive.setStoredXp(0);
        } else {
            int playerXp = totalXp(player);
            int toAdd = Math.min(MAX_XP - pensive.getStoredXp(), playerXp);
            if (toAdd > 0) {
                pensive.setStoredXp(pensive.getStoredXp() + toAdd);
                player.giveExperiencePoints(-toAdd);
            }
        }
        level.setBlock(pos, state.setValue(EMPTY, pensive.getStoredXp() == 0), 3);
        return InteractionResult.CONSUME;
    }

    /** XP total real do jogador (experienceTotal dessincroniza com /xp; recalcula por nível+progresso). */
    private static int totalXp(Player player) {
        int total = 0;
        for (int i = 0; i < player.experienceLevel; i++) {
            total += xpForLevel(i);
        }
        return total + Math.round(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    private static int xpForLevel(int level) {
        if (level >= 30) return 112 + (level - 30) * 9;
        return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }
}
