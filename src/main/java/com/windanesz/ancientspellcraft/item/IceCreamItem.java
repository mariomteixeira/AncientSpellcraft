package com.windanesz.ancientspellcraft.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/** Sorvete conjurado (1.12.2 ItemIceCream): comida 1/0.6 sempre comivel que cura 1 coracao. */
public class IceCreamItem extends Item {

    public IceCreamItem() {
        super(new Properties().food(new FoodProperties.Builder()
                .nutrition(1).saturationModifier(0.6F).alwaysEdible().fast().build()));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            player.heal(2.0F);
        }
        return super.finishUsingItem(stack, level, entity);
    }
}
