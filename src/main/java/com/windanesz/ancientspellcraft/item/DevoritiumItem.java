package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.material.IDevoritium;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/** Ingot/nugget de devoritium (1.12.2 ItemDevoritium): carregar aplica exaustão mágica. */
public class DevoritiumItem extends Item implements IDevoritium {

    public DevoritiumItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        onUpdateDelegate(stack, level, entity, slotId, isSelected);
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }
}
