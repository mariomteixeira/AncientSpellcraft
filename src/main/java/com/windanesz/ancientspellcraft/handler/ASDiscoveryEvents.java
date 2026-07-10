package com.windanesz.ancientspellcraft.handler;

import com.koomplo.wizardry.setup.registries.EBItems;
import com.windanesz.ancientspellcraft.data.RitualDiscovery;
import com.windanesz.ancientspellcraft.item.RelicItem;
import com.windanesz.ancientspellcraft.item.RitualBookItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Descobertas por Scroll of Identification na outra mao (1.12.2 ASEventHandler.onRightClickItem):
 * ritual book -> descobre o ritual (o gate upstream estava desativado; o port ativa o sistema como
 * projetado); reliquia -> pesquisa o conteudo (desvio: o 1.12.2 pesquisava na Sphere of Cognizance,
 * ainda nao portada).
 */
public final class ASDiscoveryEvents {

    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!event.getItemStack().is(EBItems.IDENTIFICATION_SCROLL.get())) return;
        Player player = event.getEntity();
        InteractionHand otherHand = event.getHand() == InteractionHand.MAIN_HAND
                ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack other = player.getItemInHand(otherHand);

        if (other.getItem() instanceof RitualBookItem) {
            String ritual = RitualBookItem.getRitual(other);
            if (ritual.isEmpty()) return;
            event.setCanceled(true);
            if (player instanceof ServerPlayer serverPlayer
                    && !RitualDiscovery.hasRitualBeenDiscovered(serverPlayer, ritual)) {
                RitualDiscovery.addKnownRitual(serverPlayer, ritual);
                if (!player.isCreative()) event.getItemStack().shrink(1);
                player.sendSystemMessage(Component.translatable("ritual.discover",
                        Component.translatable("ritual.ancientspellcraft." + ritual)));
                player.getCooldowns().addCooldown(event.getItemStack().getItem(), 60);
            }
        } else if (other.getItem() instanceof RelicItem && !RelicItem.isResearched(other)) {
            event.setCanceled(true);
            if (!player.level().isClientSide) {
                RelicItem.research(other, player);
                if (!player.isCreative()) event.getItemStack().shrink(1);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 1.25f, 1f);
                player.getCooldowns().addCooldown(event.getItemStack().getItem(), 60);
            }
        }
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) RitualDiscovery.sync(player);
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) RitualDiscovery.sync(player);
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) RitualDiscovery.sync(player);
    }

    private ASDiscoveryEvents() {}
}
