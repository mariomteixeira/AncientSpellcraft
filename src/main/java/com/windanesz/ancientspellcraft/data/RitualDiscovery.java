package com.windanesz.ancientspellcraft.data;

import com.windanesz.ancientspellcraft.network.KnownRitualsS2C;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/** Rituais descobertos por jogador (1.12.2 RitualDiscoveryData sobre WizardData). */
public final class RitualDiscovery {

    private static final String RITUALS = "Rituals";

    public static List<String> getKnownRituals(Player player) {
        CompoundTag data = player.getData(ASAttachments.KNOWN_RITUALS);
        List<String> rituals = new ArrayList<>();
        ListTag list = data.getList(RITUALS, Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            rituals.add(list.getString(i));
        }
        return rituals;
    }

    public static boolean hasRitualBeenDiscovered(Player player, String ritual) {
        if (player.isCreative()) return true;
        return getKnownRituals(player).contains(ritual);
    }

    public static void addKnownRitual(ServerPlayer player, String ritual) {
        List<String> rituals = getKnownRituals(player);
        if (rituals.contains(ritual)) return;
        rituals.add(ritual);
        CompoundTag data = player.getData(ASAttachments.KNOWN_RITUALS);
        ListTag list = new ListTag();
        rituals.forEach(r -> list.add(StringTag.valueOf(r)));
        data.put(RITUALS, list);
        sync(player);
    }

    public static void sync(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new KnownRitualsS2C(getKnownRituals(player)));
    }

    private RitualDiscovery() {}
}
