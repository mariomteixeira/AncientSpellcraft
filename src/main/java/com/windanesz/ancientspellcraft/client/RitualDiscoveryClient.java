package com.windanesz.ancientspellcraft.client;

import net.minecraft.client.Minecraft;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Copia client da lista de rituais descobertos, alimentada pelo KnownRitualsS2C. */
public final class RitualDiscoveryClient {

    private static final Set<String> KNOWN = new HashSet<>();

    public static void setKnownRituals(List<String> rituals) {
        KNOWN.clear();
        KNOWN.addAll(rituals);
    }

    /** Gate de exibicao (1.12.2 ClientProxy.shouldDisplayDiscovered): criativo sempre ve. */
    public static boolean isDiscovered(String ritual) {
        var player = Minecraft.getInstance().player;
        if (player == null) return false;
        if (player.isCreative()) return true;
        return KNOWN.contains(ritual);
    }

    private RitualDiscoveryClient() {}
}
