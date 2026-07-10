package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class ASAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AncientSpellcraft.MODID);

    /** Snapshot de posicao/vida/dimensao do time_knot. */
    public static final Supplier<AttachmentType<CompoundTag>> TIME_KNOT = ATTACHMENTS.register("time_knot",
            () -> AttachmentType.builder(() -> new CompoundTag()).serialize(CompoundTag.CODEC).build());

    /** Absorcoes do warlock: Element (string), Spell (RL), Effect (RL) + EffectDuration. */
    public static final Supplier<AttachmentType<CompoundTag>> WARLOCK_DATA = ATTACHMENTS.register("warlock_data",
            () -> AttachmentType.builder(() -> new CompoundTag()).serialize(CompoundTag.CODEC).build());

    /** Flags gerais por jogador (1.12.2 WizardData variables): MetamagicProjectile (boolean). */
    public static final Supplier<AttachmentType<CompoundTag>> PLAYER_DATA = ATTACHMENTS.register("player_data",
            () -> AttachmentType.builder(() -> new CompoundTag()).serialize(CompoundTag.CODEC).build());

    /** Rituais descobertos (1.12.2 RitualDiscoveryData, Persistence.ALWAYS): lista "Rituals". */
    public static final Supplier<AttachmentType<CompoundTag>> KNOWN_RITUALS = ATTACHMENTS.register("known_rituals",
            () -> AttachmentType.builder(() -> new CompoundTag()).serialize(CompoundTag.CODEC).copyOnDeath().build());

    private ASAttachments() {
    }
}
