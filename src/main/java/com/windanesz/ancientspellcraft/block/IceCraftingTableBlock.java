package com.windanesz.ancientspellcraft.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

/** Mesa de trabalho de gelo (1.12.2 BlockIceWorkbench): crafting vanilla; conjurada, derrete no scheduled tick. */
public class IceCraftingTableBlock extends Block {

    public IceCraftingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult hit) {
        if (!level.isClientSide) {
            player.openMenu(new SimpleMenuProvider((id, inventory, p) ->
                    new CraftingMenu(id, inventory, ContainerLevelAccess.create(level, pos)) {
                        @Override
                        public boolean stillValid(@NotNull Player player) {
                            return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
                        }
                    }, Component.translatable("block.ancientspellcraft.ice_crafting_table")));
            player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        level.removeBlock(pos, false); // derrete quando conjurada com lifetime
    }
}
