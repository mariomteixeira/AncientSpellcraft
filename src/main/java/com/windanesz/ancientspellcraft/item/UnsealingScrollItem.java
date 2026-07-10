package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.block.TemporaryBlockEntity;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/**
 * Unsealing Scroll (1.12.2 ItemUnsealingScroll): pergaminho raro de loot — right-click num muro
 * arcano GERADO (até 5 blocos) dissolve o selo; consome o pergaminho (menos em criativo).
 */
public class UnsealingScrollItem extends Item {

    public UnsealingScrollItem() {
        super(new Properties().stacksTo(16).rarity(net.minecraft.world.item.Rarity.RARE));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.pass(stack);

        var origin = player.getEyePosition();
        var end = origin.add(player.getLookAngle().scale(5));
        var hit = level.clip(new ClipContext(origin, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() != HitResult.Type.BLOCK
                || !level.getBlockState(hit.getBlockPos()).is(ASBlocks.ARCANE_WALL.get())) {
            return InteractionResultHolder.pass(stack);
        }
        int removed = TemporaryBlockEntity.dissolveConnectedPermanent(level, ASBlocks.ARCANE_WALL.get(), hit.getBlockPos(), 64);
        if (removed == 0) return InteractionResultHolder.pass(stack);

        level.playSound(null, hit.getBlockPos(), net.minecraft.sounds.SoundEvents.GLASS_BREAK,
                net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 0.8F);
        if (!player.isCreative()) stack.shrink(1);
        return InteractionResultHolder.success(stack);
    }
}
