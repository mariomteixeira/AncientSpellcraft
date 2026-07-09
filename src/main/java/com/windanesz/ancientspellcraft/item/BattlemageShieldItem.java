package com.windanesz.ancientspellcraft.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/** Escudo do battlemage (1.12.2 ItemBattlemageShield). TODO: bloqueio de spells/reflexao (433 linhas). */
public class BattlemageShieldItem extends ShieldItem {

    public BattlemageShieldItem() {
        super(new Properties().stacksTo(1).durability(672));
    }
}
