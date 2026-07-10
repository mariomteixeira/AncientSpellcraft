package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.item.ArtifactItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * Artefato diário (1.12.2 ItemDailyArtefact): right-click dá a recompensa e trava por um dia de
 * Minecraft (24000t). Desvio: sem a textura alternativa por item property "ready".
 */
public class DailyArtifactItem extends ArtifactItem {

    private static final String LAST_OPEN_TAG = "last_open_time";
    private static final long FULL_DAY = 24000;

    private final BiConsumer<Player, ItemStack> action;

    public DailyArtifactItem(Rarity rarity, BiConsumer<Player, ItemStack> action) {
        super(rarity, null);
        this.action = action;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        long now = level.getGameTime();
        long last = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getLong(LAST_OPEN_TAG);
        if (last != 0 && now - last < FULL_DAY) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable(
                        getDescriptionId() + ".empty"), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) {
            action.accept(player, stack);
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putLong(LAST_OPEN_TAG, now));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public static void give(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
