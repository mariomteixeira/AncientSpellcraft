package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Runa futhark (1.12.2 ItemRune): ingrediente dos rituais; clicar no chão desenha a runa
 * (PlacedRuneBlock, consome 1 — decorativo; os rituais do port rodam pelo Ritual Core).
 */
public class RuneItem extends Item {

    public RuneItem() {
        super(new Properties());
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        if (!context.getLevel().getBlockState(pos).canBeReplaced()) pos = pos.relative(context.getClickedFace());

        BlockState rune = ASBlocks.PLACED_RUNE.get().defaultBlockState();
        if (!context.getLevel().getBlockState(pos).canBeReplaced() || !rune.canSurvive(context.getLevel(), pos)) {
            return InteractionResult.FAIL;
        }

        context.getLevel().setBlockAndUpdate(pos, rune);
        context.getLevel().playSound(context.getPlayer(), pos, SoundType.STONE.getPlaceSound(), SoundSource.BLOCKS,
                (SoundType.STONE.getVolume() + 1.0F) / 2.0F, SoundType.STONE.getPitch() * 0.8F);
        context.getItemInHand().shrink(1);
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
}
