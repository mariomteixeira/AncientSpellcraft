package com.windanesz.ancientspellcraft.client;

import net.minecraft.client.Minecraft;

/** Pontes client chamadas de código comum (só executam depois de um guard de isClientSide). */
public final class ASClientHooks {

    public static void openRitualBook(String ritual) {
        Minecraft.getInstance().setScreen(new RitualBookScreen(ritual));
    }

    /**
     * Astral projection usa o shader do sixth sense (1.12.2 PotionAstralProjection.SHADER apontava
     * para uma cópia idêntica do JSON do EB — nunca chegou a ser carregado lá; aqui vale).
     */
    public static void registerPostEffects() {
        com.koomplo.wizardry.client.PostEffects.register(250,
                player -> player.hasEffect(com.windanesz.ancientspellcraft.registry.ASEffects.ASTRAL_PROJECTION),
                com.koomplo.wizardry.client.PostEffects.SIXTH_SENSE);
    }

    private ASClientHooks() {}
}
