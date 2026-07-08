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

    private ASSpellEvents() {
    }
}
