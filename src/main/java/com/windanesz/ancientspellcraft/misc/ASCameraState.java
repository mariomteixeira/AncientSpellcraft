package com.windanesz.ancientspellcraft.misc;

import net.minecraft.core.BlockPos;

/**
 * Flags do sistema de câmera client (1.12.2 ClientEventHandler): setados pelas spells
 * (farsight/scrying_orb) no lado client e lidos pelo handler client. Classe comum de
 * propósito para as spells não referenciarem classes client no servidor dedicado.
 */
public final class ASCameraState {

    /** Farsight sendo canalizada — zoom de FOV enquanto o item está em uso. */
    public static boolean farsightActive;

    /** Posição gravada pelo sneak-cast do scrying_orb (cópia client; o server guarda no attachment). */
    public static BlockPos scryingStoredPos;

    /** Scrying ativo — a câmera vai para {@link #scryingStoredPos} em vez da posição do caster. */
    public static boolean scryingActive;

    private ASCameraState() {}
}
