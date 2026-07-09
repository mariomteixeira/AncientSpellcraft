package com.windanesz.ancientspellcraft.ritual;

import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.content.item.CrystalItem;
import com.koomplo.wizardry.core.AllyDesignation;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rituais (1.12.2 Rituals) em versao funcional: as RUNAS exigidas sao jogadas no chao ao redor do
 * conjurador e consumidas ao concluir a canalizacao do ritual book. Desvio documentado: sem o
 * desenho/colocacao de runas em blocos (BlockPlacedRune/TileRune ficam TODO); o padrao vira
 * contagem de runas. Restantes (arcane_barrier, condensing, forest) TODO.
 */
public final class ASRituals {

    public static final List<String> RITUALS = List.of("bonfire", "rejuvenation", "warlock_attunement", "elemental_attunement");
    public static final Map<String, Map<String, Integer>> RUNES_REQUIRED = new HashMap<>();

    static {
        RUNES_REQUIRED.put("bonfire", java.util.Map.of("rune_ansuz", 1, "rune_naudiz", 1, "rune_peorth", 2, "rune_sowilo", 1, "rune_wynn", 1));
        RUNES_REQUIRED.put("rejuvenation", java.util.Map.of("rune_mannaz", 4, "rune_odal", 2));
        RUNES_REQUIRED.put("warlock_attunement", java.util.Map.of("rune_ansuz", 1, "rune_ehwaz", 1, "rune_gyfu", 1, "rune_kaunan", 1, "rune_laguz", 1, "rune_odal", 2, "rune_peorth", 1, "rune_raido", 2, "rune_wynn", 3));
        RUNES_REQUIRED.put("elemental_attunement", java.util.Map.of("rune_ehwaz", 2, "rune_laguz", 1, "rune_mannaz", 2, "rune_naudiz", 1, "rune_tiwaz", 1));
    }

    /** Runas (item entities) num raio de 4; retorna as que casam com a exigencia, ou null se faltar. */
    public static List<ItemEntity> matchRunes(Player player, String ritual) {
        Map<String, Integer> required = new HashMap<>(RUNES_REQUIRED.getOrDefault(ritual, Map.of()));
        List<ItemEntity> matched = new java.util.ArrayList<>();
        for (ItemEntity item : player.level().getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(4))) {
            String name = BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).getPath();
            Integer needed = required.get(name);
            if (needed != null && needed > 0) {
                int take = Math.min(needed, item.getItem().getCount());
                required.put(name, needed - take);
                matched.add(item);
            }
        }
        return required.values().stream().allMatch(v -> v == 0) ? matched : null;
    }

    public static void consumeRunes(Player player, String ritual, List<ItemEntity> matched) {
        Map<String, Integer> required = new HashMap<>(RUNES_REQUIRED.getOrDefault(ritual, Map.of()));
        for (ItemEntity item : matched) {
            String name = BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).getPath();
            int needed = required.getOrDefault(name, 0);
            if (needed <= 0) continue;
            int take = Math.min(needed, item.getItem().getCount());
            item.getItem().shrink(take);
            required.put(name, needed - take);
            if (item.getItem().isEmpty()) item.discard();
        }
    }

    public static boolean finish(Player player, String ritual) {
        var level = player.level();
        switch (ritual) {
            case "bonfire" -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.setRespawnPosition(level.dimension(), player.blockPosition(), player.getYRot(), true, false);
                    player.sendSystemMessage(Component.translatable("ritual.ancientspellcraft.bonfire.new_spawnpoint",
                            player.getBlockX(), player.getBlockY(), player.getBlockZ()));
                }
                return true;
            }
            case "rejuvenation" -> {
                for (var target : EntityUtil.getLivingWithinRadius(6, player.getX(), player.getY(), player.getZ(), level)) {
                    if (target == player || AllyDesignation.isAllied(player, target)) {
                        target.heal(10);
                        target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
                    }
                }
                return true;
            }
            case "warlock_attunement" -> {
                var tag = player.getData(ASAttachments.WARLOCK_DATA);
                boolean attuned = !tag.getBoolean("WarlockAttuned");
                tag.putBoolean("WarlockAttuned", attuned);
                player.setData(ASAttachments.WARLOCK_DATA, tag);
                player.sendSystemMessage(Component.translatable(attuned
                        ? "ritual.ancientspellcraft.warlock_attunement.attuned"
                        : "ritual.ancientspellcraft.warlock_attunement.unattuned"));
                return true;
            }
            case "elemental_attunement" -> {
                for (ItemEntity item : player.level().getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(4))) {
                    if (item.getItem().getItem() instanceof CrystalItem) {
                        String name = BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).getPath();
                        String element = name.equals("magic_crystal") ? "magic" : name.replace("magic_crystal_", "");
                        var tag = player.getData(ASAttachments.WARLOCK_DATA);
                        if (element.equals("magic")) {
                            tag.remove("ElementalAttunement");
                            player.sendSystemMessage(Component.translatable("ritual.ancientspellcraft.elemental_attunement.cleared"));
                        } else {
                            tag.putString("ElementalAttunement", element);
                            player.sendSystemMessage(Component.translatable("ritual.ancientspellcraft.elemental_attunement.attuned", element));
                        }
                        player.setData(ASAttachments.WARLOCK_DATA, tag);
                        item.getItem().shrink(1);
                        if (item.getItem().isEmpty()) item.discard();
                        return true;
                    }
                }
                player.sendSystemMessage(Component.translatable("ritual.ancientspellcraft.elemental_attunement.no_crystal"));
                return false;
            }
        }
        return false;
    }

    private ASRituals() {
    }
}
