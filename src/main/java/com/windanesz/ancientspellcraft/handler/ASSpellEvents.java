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
    }

    /** Gate das class spells (1.12.2 IClassSpell.onSpellCastPreEvent): exige o set completo. TODO warlock attunement. */
    public static void onClassSpellCastPre(com.koomplo.wizardry.api.content.event.SpellCastEvent.Pre event) {
        if (!(event.getSpell() instanceof com.windanesz.ancientspellcraft.spell.ClassSpell classSpell)) return;
        if (!(event.getCaster() instanceof Player player)) return;
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
