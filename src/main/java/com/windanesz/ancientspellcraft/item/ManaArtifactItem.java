package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.item.ArtifactItem;
import com.koomplo.wizardry.api.content.item.IManaItem;
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

/**
 * Artefato com reserva de mana (1.12.2 ItemManaArtefact): começa cheio; sneak-use com um
 * magic_crystal na offhand recarrega (desvio: o 1.12.2 recarregava no arcane workbench).
 */
public class ManaArtifactItem extends ArtifactItem implements IManaItem {

    private static final String MANA_TAG = "Mana";
    private final int capacity;

    public ManaArtifactItem(Rarity rarity, int capacity, @Nullable IArtifactEffect effect) {
        super(rarity, effect);
        this.capacity = capacity;
    }

    @Override
    public int getMana(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || !data.copyTag().contains(MANA_TAG)) return capacity;
        return data.copyTag().getInt(MANA_TAG);
    }

    @Override
    public void setMana(ItemStack stack, int mana) {
        int clamped = Math.max(0, Math.min(mana, capacity));
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(MANA_TAG, clamped));
    }

    @Override
    public int getManaCapacity(ItemStack stack) {
        return capacity;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()
                && player.getOffhandItem().is(com.koomplo.wizardry.setup.registries.EBItems.MAGIC_CRYSTAL.get())
                && !isManaFull(stack)) {
            if (!level.isClientSide) {
                player.getOffhandItem().shrink(1);
                rechargeMana(stack, com.koomplo.wizardry.core.config.EBServerConfig.MANA_PER_CRYSTAL.get());
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getMana(stack) < capacity;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getMana(stack) / capacity);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x56e8e3;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.ancientspellcraft.mana_artifact.mana",
                getMana(stack), capacity).withStyle(net.minecraft.ChatFormatting.BLUE));
    }
}
