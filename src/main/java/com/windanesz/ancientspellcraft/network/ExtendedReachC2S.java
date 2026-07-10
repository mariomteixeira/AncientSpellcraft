package com.windanesz.ancientspellcraft.network;

import com.koomplo.wizardry.core.platform.Services;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.BattlemageSwordItem;
import com.windanesz.ancientspellcraft.spell.RunewordSpell;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Golpe estendido do runeword_reach/pull/push (1.12.2 PacketExtendedSwordReach): o client detecta
 * o clique no vazio com a runeword ativa e manda o alvo; o servidor valida distância/arma e aplica
 * o ataque + puxão/empurrão.
 */
public record ExtendedReachC2S(int entityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ExtendedReachC2S> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, "extended_reach"));

    public static final StreamCodec<FriendlyByteBuf, ExtendedReachC2S> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> buf.writeInt(packet.entityId),
            buf -> new ExtendedReachC2S(buf.readInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ExtendedReachC2S packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player.getMainHandItem().getItem() instanceof BattlemageSwordItem)) return;
            ItemCheck:
            {
                String active = RunewordSpell.getActive(player.getMainHandItem());
                if (active.isEmpty()) return;
                var spell = Services.REGISTRY_UTIL.getSpell(ResourceLocation.tryParse(active));
                if (spell == null) return;
                String path = spell.getLocation().getPath();
                if (!path.equals("runeword_reach") && !path.equals("runeword_pull") && !path.equals("runeword_push")) return;

                if (!(player.level().getEntity(packet.entityId) instanceof LivingEntity target)) return;
                if (player.distanceTo(target) > 9.5F || !player.hasLineOfSight(target)) return;

                player.attack(target); // dispara também os hooks de runeword (gasta a carga)
                if (path.equals("runeword_pull")) {
                    var pull = player.position().subtract(target.position()).normalize().scale(1.5);
                    target.push(pull.x, 0.4, pull.z);
                } else if (path.equals("runeword_push")) {
                    var push = target.position().subtract(player.position()).normalize().scale(1.8);
                    target.push(push.x, 0.4, push.z);
                }
            }
        });
    }
}
