package com.windanesz.ancientspellcraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.windanesz.ancientspellcraft.network.WarlockCastC2S;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Keybinds do AS (1.12.2 ClientProxy): H casta a spell absorvida do warlock. */
public final class ASKeyBindings {

    public static final KeyMapping WARLOCK_CAST = new KeyMapping("key.ancientspellcraft.key_warlock_cast",
            InputConstants.Type.KEYSYM, InputConstants.KEY_H, "key.ancientspellcraft.category");

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(WARLOCK_CAST);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().screen != null) return;
        while (WARLOCK_CAST.consumeClick()) {
            PacketDistributor.sendToServer(new WarlockCastC2S());
        }
    }

    private ASKeyBindings() {}
}
