package com.windanesz.ancientspellcraft.handler;

import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ASSpellEvents {

    /** Guarda do burrow: se o cast parou, o jogador nao pode continuar atravessando o chao. */
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.noPhysics && !player.isSpectator()
                && !EntityUtil.isCasting(player, ASSpells.BURROW.get())) {
            player.noPhysics = false;
        }
        if (!player.level().isClientSide) {
            tickMasterBoltPull(player);
        }
    }

    /**
     * Puxão do master_bolt (1.12.2 MasterBolt.update): countdown de 20t movendo o jogador em
     * passos até o bloco, deixando um rastro de lightning_block temporário; na chegada, explosão
     * proporcional à queda + esfera de raios + devolve o item. Desvio: sem o efeito static_aura.
     */
    private static void tickMasterBoltPull(Player player) {
        var tag = player.getData(com.windanesz.ancientspellcraft.registry.ASAttachments.PLAYER_DATA);
        int countdown = tag.getInt(com.windanesz.ancientspellcraft.spell.MasterBoltSpell.COUNTDOWN_TAG);
        if (countdown <= 0) return;
        var destOpt = net.minecraft.nbt.NbtUtils.readBlockPos(tag, com.windanesz.ancientspellcraft.spell.MasterBoltSpell.LOCATION_TAG);
        if (destOpt.isEmpty()) {
            tag.remove(com.windanesz.ancientspellcraft.spell.MasterBoltSpell.COUNTDOWN_TAG);
            player.setData(com.windanesz.ancientspellcraft.registry.ASAttachments.PLAYER_DATA, tag);
            return;
        }
        var dest = destOpt.get();
        var level = player.level();
        if (level.getBlockState(dest).is(com.windanesz.ancientspellcraft.registry.ASBlocks.MASTER_BOLT.get())) {
            level.removeBlock(dest, false);
        }

        boolean arrived = false;
        if (countdown > 1 && player.distanceToSqr(dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5) > 2.0) {
            double stepX = (dest.getX() + 0.5 - player.getX()) / countdown;
            double stepY = (dest.getY() - player.getY()) / countdown;
            double stepZ = (dest.getZ() + 0.5 - player.getZ()) / countdown;
            for (int i = 0; i < 10 && !arrived; i++) {
                player.teleportTo(player.getX() + stepX, player.getY() + stepY, player.getZ() + stepZ);
                var pos = player.blockPosition();
                if (level.getBlockState(pos).isAir()) {
                    com.windanesz.ancientspellcraft.block.TemporaryBlockEntity.place(player, level,
                            com.windanesz.ancientspellcraft.registry.ASBlocks.LIGHTNING_BLOCK.get(), pos, 60);
                }
                arrived = player.distanceToSqr(dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5) < 2.0;
            }
        } else {
            arrived = true;
        }

        if (arrived) {
            level.explode(player, player.getX(), player.getY(), player.getZ(),
                    Math.min(2.0F, player.fallDistance * 0.05F), net.minecraft.world.level.Level.ExplosionInteraction.MOB);
            float radius = Math.max(1.0F, Math.min(3.0F, player.fallDistance * 0.2F));
            var center = player.blockPosition();
            for (var pos : net.minecraft.core.BlockPos.betweenClosed(center.offset((int) -radius, (int) -radius, (int) -radius),
                    center.offset((int) radius, (int) radius, (int) radius))) {
                if (pos.distSqr(center) <= radius * radius && level.getBlockState(pos).isAir()) {
                    com.windanesz.ancientspellcraft.block.TemporaryBlockEntity.place(player, level,
                            com.windanesz.ancientspellcraft.registry.ASBlocks.LIGHTNING_BLOCK.get(), pos.immutable(), 90);
                }
            }
            player.getInventory().placeItemBackInInventory(
                    new net.minecraft.world.item.ItemStack(com.windanesz.ancientspellcraft.registry.ASItems.MASTER_BOLT.get()));
            player.fallDistance = 0;
            tag.remove(com.windanesz.ancientspellcraft.spell.MasterBoltSpell.LOCATION_TAG);
            tag.remove(com.windanesz.ancientspellcraft.spell.MasterBoltSpell.DIMENSION_TAG);
            tag.remove(com.windanesz.ancientspellcraft.spell.MasterBoltSpell.COUNTDOWN_TAG);
        } else {
            tag.putInt(com.windanesz.ancientspellcraft.spell.MasterBoltSpell.COUNTDOWN_TAG, countdown - 1);
        }
        player.setData(com.windanesz.ancientspellcraft.registry.ASAttachments.PLAYER_DATA, tag);
    }

    /** static_charge (1.12.2 ASEventHandler): espada carregada dá +2 de dano por nível até expirar. */
    public static void onIncomingDamage(net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getSource().getDirectEntity() instanceof net.minecraft.world.entity.LivingEntity attacker)) return;
        var stack = attacker.getMainHandItem();
        if (!(stack.getItem() instanceof net.minecraft.world.item.SwordItem)) return;
        var tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        int level = tag.getInt(com.windanesz.ancientspellcraft.spell.StaticChargeSpell.LEVEL_TAG);
        if (level <= 0) return;
        if (tag.getLong(com.windanesz.ancientspellcraft.spell.StaticChargeSpell.EXPIRY_TAG) < event.getEntity().level().getGameTime()) {
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, t -> {
                t.remove(com.windanesz.ancientspellcraft.spell.StaticChargeSpell.LEVEL_TAG);
                t.remove(com.windanesz.ancientspellcraft.spell.StaticChargeSpell.EXPIRY_TAG);
            });
            return;
        }
        event.setAmount(event.getAmount() + level * 2);
    }

    /** Gate das class spells (1.12.2 IClassSpell.onSpellCastPreEvent): exige o set completo. TODO warlock attunement. */
    public static void onClassSpellCastPre(com.koomplo.wizardry.api.content.event.SpellCastEvent.Pre event) {
        if (!(event.getSpell() instanceof com.windanesz.ancientspellcraft.spell.ClassSpell classSpell)) return;
        if (!(event.getCaster() instanceof Player player)) return;
        if (classSpell.armourClass() == com.koomplo.wizardry.content.item.armor.WizardArmorType.WARLOCK
                && !ASWarlockEvents.isWarlockAttuned(player)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    "message.ancientspellcraft.not_warlock_attuned"), true);
            event.setCanceled(true);
            return;
        }
        if (!isWearingFullSet(player, classSpell.armourClass())) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    "message.ancientspellcraft.must_have_full_matching_set",
                    net.minecraft.network.chat.Component.translatable(
                            "wizard_armour_class." + classSpell.armourClass().name().toLowerCase())), false);
            event.setCanceled(true);
        }
    }

    public static boolean isWearingFullSet(Player player, com.koomplo.wizardry.content.item.armor.WizardArmorType type) {
        for (var stack : player.getArmorSlots()) {
            if (!(stack.getItem() instanceof com.koomplo.wizardry.content.item.armor.WizardArmorItem armor)
                    || armor.getWizardArmorType() != type) {
                return false;
            }
        }
        return true;
    }

    private ASSpellEvents() {
    }
}
