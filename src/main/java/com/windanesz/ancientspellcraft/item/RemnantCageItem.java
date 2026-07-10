package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.content.entity.living.Remnant;
import com.koomplo.wizardry.core.platform.Services;
import com.koomplo.wizardry.setup.registries.EBItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Remnant Cage (1.12.2 ItemRemnantCage): captura um Remnant (guarda o elemento); com um remnant
 * dentro, o uso diário rende spectral dust do elemento; sneak-use solta o remnant como minion.
 * Desvio: o minion é o próprio Remnant do Redux com MinionData (o 1.12.2 tinha EntityRemnantMinion).
 */
public class RemnantCageItem extends DailyArtifactItem {

    private static final String HAS_REMNANT_TAG = "hasRemnant";
    private static final String STORED_ELEMENT_TAG = "storedElement";

    public RemnantCageItem(Rarity rarity) {
        super(rarity, RemnantCageItem::daily);
    }

    private static void daily(Player player, ItemStack stack) {
        if (!hasRemnant(stack)) return;
        String element = getStoredElement(stack);
        Item dust = switch (element) {
            case "earth" -> EBItems.SPECTRAL_DUST_EARTH.get();
            case "fire" -> EBItems.SPECTRAL_DUST_FIRE.get();
            case "healing" -> EBItems.SPECTRAL_DUST_HEALING.get();
            case "ice" -> EBItems.SPECTRAL_DUST_ICE.get();
            case "lightning" -> EBItems.SPECTRAL_DUST_LIGHTNING.get();
            case "necromancy" -> EBItems.SPECTRAL_DUST_NECROMANCY.get();
            case "sorcery" -> EBItems.SPECTRAL_DUST_SORCERY.get();
            default -> EBItems.SPECTRAL_DUST.get();
        };
        give(player, new ItemStack(dust));
        clearRemnant(stack);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player,
                                                           @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (!(target instanceof Remnant remnant) || hasRemnant(stack)) return InteractionResult.PASS;
        if (!player.level().isClientSide) {
            String element = remnant.getElement();
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
                tag.putBoolean(HAS_REMNANT_TAG, true);
                tag.putString(STORED_ELEMENT_TAG, element == null ? "" : element);
            });
            remnant.discard();
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!hasRemnant(stack)) return InteractionResultHolder.fail(stack);
            if (!level.isClientSide) {
                var minion = new com.windanesz.ancientspellcraft.entity.living.RemnantMinion(
                        com.windanesz.ancientspellcraft.registry.ASEntities.REMNANT_MINION.get(), level);
                minion.setPos(player.getX(), player.getY(), player.getZ());
                CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                String stored = data.copyTag().getString(STORED_ELEMENT_TAG);
                if (!stored.isEmpty()) minion.setElement(stored);
                var minionData = Services.OBJECT_DATA.getMinionData(minion);
                minionData.setSummoned(true);
                minionData.setOwnerUUID(player.getUUID());
                minionData.setLifetime(-1);
                minionData.updateGoals();
                level.addFreshEntity(minion);
                clearRemnant(stack);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return super.use(level, player, hand);
    }

    private static boolean hasRemnant(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.copyTag().getBoolean(HAS_REMNANT_TAG);
    }

    private static String getStoredElement(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return "";
        String stored = data.copyTag().getString(STORED_ELEMENT_TAG);
        var rl = net.minecraft.resources.ResourceLocation.tryParse(stored);
        return rl == null ? "" : rl.getPath();
    }

    private static void clearRemnant(ItemStack stack) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.remove(HAS_REMNANT_TAG);
            tag.remove(STORED_ELEMENT_TAG);
        });
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (hasRemnant(stack)) {
            tooltip.add(Component.translatable("item.ancientspellcraft.charm_remnant_cage.contains",
                    getStoredElement(stack)).withStyle(ChatFormatting.DARK_AQUA));
        }
    }
}
