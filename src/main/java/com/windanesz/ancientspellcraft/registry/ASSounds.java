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
    public static final DeferredHolder<SoundEvent, SoundEvent> RELIC_USE_LOOP = sound("relic_use_loop");
    public static final DeferredHolder<SoundEvent, SoundEvent> RELIC_ACTIVATE = sound("relic_activate");
    public static final DeferredHolder<SoundEvent, SoundEvent> RELIC_ACTIVATE_2 = sound("relic_activate_2");
    public static final DeferredHolder<SoundEvent, SoundEvent> TRANSMUTATION = sound("transmutation");

    private static DeferredHolder<SoundEvent, SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, name)));
    }

    private ASSounds() {
    }
}
