package com.windanesz.ancientspellcraft.client;

import net.minecraft.client.Minecraft;

/** Pontes client chamadas de código comum (só executam depois de um guard de isClientSide). */
public final class ASClientHooks {

    public static void openRitualBook(String ritual) {
        Minecraft.getInstance().setScreen(new RitualBookScreen(ritual));
    }

    private ASClientHooks() {}
}
