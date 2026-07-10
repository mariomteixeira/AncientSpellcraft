package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.item.ArtifactItem;
import com.koomplo.wizardry.core.IArtifactEffect;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * Artefato com 1 encaixe (1.12.2 AbstractItemArtefactWithSlots): sneak-use com o item na offhand
 * ENCAIXA (consome 1); sneak-use com a offhand vazia devolve. Desvio: o 1.12.2 abria uma GUI de
 * slot — o port usa a offhand.
 */
public class SocketedArtifactItem extends ArtifactItem {

    private static final String SOCKET_TAG = "Socketed";
    private final Predicate<ItemStack> accepts;

    public SocketedArtifactItem(Rarity rarity, Predicate<ItemStack> accepts, @Nullable IArtifactEffect effect) {
        super(rarity, effect);
        this.accepts = accepts;
    }

    public static ItemStack getSocketed(ItemStack stack, net.minecraft.core.HolderLookup.Provider registries) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || !data.copyTag().contains(SOCKET_TAG)) return ItemStack.EMPTY;
        return ItemStack.parseOptional(registries, data.copyTag().getCompound(SOCKET_TAG));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) return InteractionResultHolder.pass(stack);
        ItemStack offhand = player.getOffhandItem();
        ItemStack socketed = getSocketed(stack, level.registryAccess());

        if (socketed.isEmpty() && !offhand.isEmpty() && accepts.test(offhand) && offhand != stack) {
            if (!level.isClientSide) {
                ItemStack toSocket = offhand.copyWithCount(1);
                offhand.shrink(1);
                CustomData.update(DataComponents.CUSTOM_DATA, stack,
                        tag -> tag.put(SOCKET_TAG, toSocket.save(level.registryAccess())));
            }
            return InteractionResultHolder.success(stack);
        }
        if (!socketed.isEmpty() && offhand.isEmpty()) {
            if (!level.isClientSide) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(SOCKET_TAG));
                player.getInventory().placeItemBackInInventory(socketed);
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null && data.copyTag().contains(SOCKET_TAG)) {
            var registries = context.registries();
            if (registries != null) {
                ItemStack socketed = getSocketed(stack, registries);
                if (!socketed.isEmpty()) {
                    tooltip.add(Component.translatable("item.ancientspellcraft.socketed_artifact.socketed",
                            socketed.getHoverName()).withStyle(net.minecraft.ChatFormatting.GREEN));
                    return;
                }
            }
        }
        tooltip.add(Component.translatable("item.ancientspellcraft.socketed_artifact.empty")
                .withStyle(net.minecraft.ChatFormatting.YELLOW));
    }
}
