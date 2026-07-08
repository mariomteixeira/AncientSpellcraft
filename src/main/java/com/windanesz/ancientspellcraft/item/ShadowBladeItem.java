package com.windanesz.ancientspellcraft.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/** Lamina sombria conjurada (1.12.2 ItemShadowBlade). TODO dash/charge sombrio (IVariable ticker). */
public class ShadowBladeItem extends SwordItem {

    public ShadowBladeItem() {
        super(Tiers.IRON, new Properties().durability(1200).rarity(Rarity.UNCOMMON)
                .attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4F)));
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
