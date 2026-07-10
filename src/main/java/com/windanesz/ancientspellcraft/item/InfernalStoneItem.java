package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.item.ArtifactItem;
import com.koomplo.wizardry.core.IArtifactEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Infernal Stone (1.12.2 ItemInfernalStone): acumula calor (0-100) absorvendo blocos de FOGO num
 * raio de 3 enquanto segurado (+5 por bloco, 1 a cada 3t); perde 1 de calor a cada 3s equipado;
 * quente, spells de fogo custam -25% de mana e consomem 10 de calor (hook no ASSpellEvents);
 * com calor >= 50 serve de combustível de fornalha (8000t) sem se consumir.
 */
public class InfernalStoneItem extends ArtifactItem {

    private static final String HEAT_TAG = "heatProgress";
    public static final int MAX_HEAT = 100;

    public InfernalStoneItem(Rarity rarity) {
        super(rarity, new IArtifactEffect() {
            @Override
            public void onTick(Player player, Level level, ItemStack artifact) {
                // perda de calor: -1 a cada 60t
                if (!level.isClientSide && player.tickCount % 60 == 0) {
                    removeHeat(artifact, 1);
                }
            }
        });
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        // absorção de fogo: segurando em qualquer mão, 1 bloco a cada 3t no raio de 3
        if (level.isClientSide || !(entity instanceof Player player) || player.tickCount % 3 != 0) return;
        if (player.getMainHandItem() != stack && player.getOffhandItem() != stack) return;
        BlockPos center = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-3, -3, -3), center.offset(3, 3, 3))) {
            if (level.getBlockState(pos).is(Blocks.FIRE)) {
                level.removeBlock(pos, false);
                addHeat(stack, 5);
                return;
            }
        }
    }

    public static int getHeat(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? 0 : data.copyTag().getInt(HEAT_TAG);
    }

    public static void addHeat(ItemStack stack, int amount) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                tag -> tag.putInt(HEAT_TAG, Math.min(MAX_HEAT, tag.getInt(HEAT_TAG) + amount)));
    }

    public static void removeHeat(ItemStack stack, int amount) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                tag -> tag.putInt(HEAT_TAG, Math.max(0, tag.getInt(HEAT_TAG) - amount)));
    }

    @Override
    public int getBurnTime(@NotNull ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return getHeat(stack) >= 50 ? 8000 : 0;
    }

    @Override
    public boolean hasCraftingRemainingItem(@NotNull ItemStack stack) {
        return getHeat(stack) >= 1;
    }

    @Override
    public @NotNull ItemStack getCraftingRemainingItem(@NotNull ItemStack stack) {
        return getHeat(stack) >= 1 ? stack.copy() : ItemStack.EMPTY;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        int heat = getHeat(stack);
        ChatFormatting color = heat >= 75 ? ChatFormatting.RED : heat >= 50 ? ChatFormatting.GOLD
                : heat >= 25 ? ChatFormatting.YELLOW : ChatFormatting.GRAY;
        tooltip.add(Component.literal("Heat: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(heat + "%").withStyle(color)));
    }
}
