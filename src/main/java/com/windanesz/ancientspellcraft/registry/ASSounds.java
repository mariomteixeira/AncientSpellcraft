package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Sons próprios do pack (1.12.2 sounds.json) — preenchido conforme os itens que os usam chegam. */
public final class ASSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, AncientSpellcraft.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> GOOSE = sound("goose");
    public static final DeferredHolder<SoundEvent, SoundEvent> WAR_HORN = sound("war_horn");

    private static DeferredHolder<SoundEvent, SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, name)));
    }

    private ASSounds() {
    }
}
