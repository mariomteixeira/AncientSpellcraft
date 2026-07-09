package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.world.PocketDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Saída física do espaço de bolso (1.12.2 BlockDimensionFocus): clicar dentro da pocket dimension
 * teleporta de volta ao ponto de partida. Desvio documentado: o original também devolvia o orb
 * guardado (LAST_ORB) — o port não tem o sistema de orbe físico 1:1.
 */
public class DimensionFocusBlock extends Block {

    public DimensionFocusBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!PocketDimension.isInside(player)) return InteractionResult.PASS;
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            PocketDimension.teleportBack(serverPlayer);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
