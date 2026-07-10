package com.windanesz.ancientspellcraft.network;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

/** Sync da lista de rituais descobertos (1.12.2 RitualDiscoveryData era um IStoredVariable synced). */
public record KnownRitualsS2C(List<String> rituals) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<KnownRitualsS2C> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, "known_rituals"));

    public static final StreamCodec<FriendlyByteBuf, KnownRitualsS2C> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeVarInt(packet.rituals.size());
                packet.rituals.forEach(buf::writeUtf);
            },
            buf -> {
                int size = buf.readVarInt();
                List<String> rituals = new ArrayList<>(size);
                for (int i = 0; i < size; i++) rituals.add(buf.readUtf());
                return new KnownRitualsS2C(rituals);
            });

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(KnownRitualsS2C packet, IPayloadContext context) {
        context.enqueueWork(() ->
                com.windanesz.ancientspellcraft.client.RitualDiscoveryClient.setKnownRituals(packet.rituals));
    }
}
