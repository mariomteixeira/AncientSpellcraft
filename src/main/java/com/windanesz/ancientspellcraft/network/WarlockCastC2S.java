package com.windanesz.ancientspellcraft.network;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.handler.ASWarlockEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Keybind do warlock (1.12.2 PacketCastWarlockSpell): pede ao servidor o cast da spell absorvida. */
public record WarlockCastC2S() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<WarlockCastC2S> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, "warlock_cast"));

    public static final StreamCodec<FriendlyByteBuf, WarlockCastC2S> STREAM_CODEC =
            StreamCodec.unit(new WarlockCastC2S());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WarlockCastC2S packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ASWarlockEvents.castAbsorbedSpell(player);
            }
        });
    }
}
