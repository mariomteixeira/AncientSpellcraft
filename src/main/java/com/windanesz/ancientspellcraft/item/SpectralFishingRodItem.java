package com.windanesz.ancientspellcraft.item;

import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;

/** Vara de pescar espectral conjurada (1.12.2 ItemSpectralFishingRod). */
public class SpectralFishingRodItem extends FishingRodItem {

    public SpectralFishingRodItem() {
        super(new Properties().durability(600).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack stack, @NotNull ItemStack other) {
        return false;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }
}
