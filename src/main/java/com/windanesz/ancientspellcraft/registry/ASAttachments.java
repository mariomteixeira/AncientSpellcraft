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

    private ASAttachments() {
    }
}
