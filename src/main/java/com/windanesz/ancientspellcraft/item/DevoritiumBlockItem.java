package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.material.IDevoritium;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

/** Item dos blocos de devoritium (1.12.2 ItemBlockDevoritiumMaterial): carregar aplica exaustão mágica. */
public class DevoritiumBlockItem extends BlockItem implements IDevoritium {

    public DevoritiumBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        onUpdateDelegate(stack, level, entity, slotId, isSelected);
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }
}
