package com.windanesz.ancientspellcraft.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Essência alquímica (1.12.2 ItemAlchemicalEssence): use com ferro na offhand transmuta 2-5 em ouro. */
public class AlchemicalEssenceItem extends Item {

    public AlchemicalEssenceItem() {
        super(new Properties().stacksTo(16).rarity(Rarity.RARE));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack essence = player.getItemInHand(hand);
        ItemStack offhand = player.getOffhandItem();
        if (!level.isClientSide && offhand.is(Items.IRON_INGOT)) {
            int count = 2 + level.random.nextInt(Math.max(1, Math.min(5, offhand.getCount()) - 1));
            essence.shrink(1);
            offhand.shrink(count);
            ItemStack gold = new ItemStack(Items.GOLD_INGOT, count);
            if (!player.getInventory().add(gold)) player.drop(gold, false);
        }
        return InteractionResultHolder.pass(essence);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.ancientspellcraft.alchemical_essence.desc")
                .withStyle(ChatFormatting.GRAY));
    }
}
