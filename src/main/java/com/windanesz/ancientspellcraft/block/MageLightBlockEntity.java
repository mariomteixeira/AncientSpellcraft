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
        // 1.12.2: os charms glyph_illumination/magic_light também mantêm a luz acesa
        if (player == null || (!player.hasEffect(block.keepAliveEffect())
                && !com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                com.windanesz.ancientspellcraft.registry.ASItems.CHARM_GLYPH_ILLUMINATION.get())
                && !com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                com.windanesz.ancientspellcraft.registry.ASItems.CHARM_MAGIC_LIGHT.get()))) {
            level.removeBlock(pos, false);
        }
    }
}
