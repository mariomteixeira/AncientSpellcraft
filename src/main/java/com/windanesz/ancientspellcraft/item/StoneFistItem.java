package com.windanesz.ancientspellcraft.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import org.jetbrains.annotations.NotNull;

/** Punho de pedra conjurado (1.12.2 ItemStoneFist). TODO slowness enquanto segura. */
public class StoneFistItem extends SwordItem {

    public StoneFistItem(Tier tier, int bonusDamage) {
        super(tier, new Properties().durability(600).rarity(Rarity.UNCOMMON)
                .attributes(SwordItem.createAttributes(tier, 3 + bonusDamage, -2.4F)));
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
