package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MageLightBlockEntity extends BlockEntity {

    public MageLightBlockEntity(BlockPos pos, BlockState state) {
        super(ASBlocks.MAGE_LIGHT_BE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MageLightBlockEntity be) {
        if (!(state.getBlock() instanceof MageLightBlock block)) return;
        Player player = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4.0, false);
        // TODO 1.12.2 tambem mantinha a luz com os charms glyph_illumination/magic_light (portam com os artefatos)
        if (player == null || !player.hasEffect(block.keepAliveEffect())) {
            level.removeBlock(pos, false);
        }
    }
}
