package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.item.IManaItem;
import com.koomplo.wizardry.core.config.EBServerConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Everfull Flask of Mana (1.12.2 ItemEverfullManaFlask, artefato charm): reserva de 1400 que se
 * reenche sozinha (+3 por intervalo do condenser); use na offhand transfere 10% da capacidade para
 * o item de mana da mainhand. Desvio: textura única com barra (o 1.12.2 tinha 11 modelos por nível).
 */
public class EverfullManaFlaskItem extends ManaArtifactItem {

    private static final int CAPACITY = 1400;
    private static final int REGEN = 3;

    public EverfullManaFlaskItem(Rarity rarity) {
        super(rarity, CAPACITY, null);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && !isManaFull(stack)
                && level.getGameTime() % EBServerConfig.CONDENSER_TICK_INTERVAL.get() == 0) {
            rechargeMana(stack, REGEN);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack flask = player.getItemInHand(hand);
        if (hand == InteractionHand.OFF_HAND && player.getMainHandItem().getItem() instanceof IManaItem manaItem) {
            ItemStack mainhand = player.getMainHandItem();
            if (!level.isClientSide) {
                int amount = Math.min(CAPACITY / 10, getMana(flask));
                amount = Math.min(amount, manaItem.getManaCapacity(mainhand) - manaItem.getMana(mainhand));
                manaItem.rechargeMana(mainhand, amount);
                consumeMana(flask, amount, player);
            }
            return InteractionResultHolder.success(flask);
        }
        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable(
                    "item.ancientspellcraft.charm_mana_flask.invalid_use"), true);
        }
        return InteractionResultHolder.fail(flask);
    }
}
