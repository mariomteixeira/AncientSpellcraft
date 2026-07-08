package com.windanesz.ancientspellcraft.data;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.SpellTier;
import com.koomplo.wizardry.core.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/** Componentes de pesquisa do scribing desk (1.12.2 SpellComponentList); entradas com spell/item
 * ausente sao ignoradas em runtime. */
public final class SpellComponents {

    public record Entry(String spellId, List<String> itemIds) {
    }

    private static final List<Entry> TABLE = List.of(
            entry("antimagic_field", "minecraft:diamond", "ebwizardry:magic_crystal_grand"),
            entry("arcane_aegis", "ebwizardry:astral_diamond", "ebwizardry:magic_crystal_grand", "minecraft:golden_apple"),
            entry("arcane_augmentation", "ebwizardry:magic_crystal_grand", "ebwizardry:blast_upgrade"),
            entry("arcane_beam", "ebwizardry:magic_crystal_grand", "minecraft:quartz"),
            entry("arcane_magnetism", "minecraft:ender_eye", "minecraft:redstone_torch"),
            entry("aspect_hunter", "minecraft:bow", "minecraft:leather_helmet", "minecraft:splash_potion"),
            entry("might_and_magic", "ebwizardry:mana_flask_medium", "minecraft:splash_potion"),
            entry("blockweaving", "ebwizardry:crystal_block", "minecraft:golden_pickaxe"),
            entry("bubble_head", "minecraft:glass", "minecraft:water_bucket", "minecraft:glowstone_dust"),
            entry("bulwark", "ebwizardry:magic_crystal_earth", "minecraft:shield", "ebwizardry:magic_crystal_grand"),
            entry("channel_power", "ebwizardry:astral_diamond", "ebwizardry:astral_diamond", "ebwizardry:astral_diamond"),
            entry("channel_effect", "ebwizardry:magic_crystal_grand"),
            entry("continuity_charm", "ebwizardry:magic_crystal_grand", "ebwizardry:duration_upgrade"),
            entry("crystal_mine", "ebwizardry:crystal_block", "minecraft:tnt"),
            entry("conduit", "ebwizardry:mana_flask_large", "ebwizardry:receptacle", "ebwizardry:magic_crystal_grand"),
            entry("covenant", "ebwizardry:wand_master", "minecraft:diamond_block", "ebwizardry:astral_diamond"),
            entry("dispel_lesser_magic", "minecraft:glass_bottle", "ebwizardry:magic_crystal_grand", "ebwizardry:siphon_upgrade"),
            entry("dispel_greater_magic", "minecraft:glass_bottle", "ebwizardry:astral_diamond", "ebwizardry:siphon_upgrade"),
            entry("eagle_eye", "minecraft:ender_eye", "ebwizardry:magic_crystal", "minecraft:feather"),
            entry("farsight", "minecraft:ender_eye", "ebwizardry:magic_crystal", "minecraft:glass_pane"),
            entry("forcefend", "ebwizardry:astral_diamond", "minecraft:golden_apple", "minecraft:shield"),
            entry("intensifying_focus", "ebwizardry:magic_crystal_grand", "ebwizardry:attunement_upgrade"),
            entry("magelight", "minecraft:glowstone", "minecraft:torch", "minecraft:fire_charge"),
            entry("mana_flare", "ebwizardry:wand_novice", "ebwizardry:magic_crystal_grand"),
            entry("mana_vortex", "ebwizardry:receptacle", "ebwizardry:magic_crystal_grand", "minecraft:blaze_powder"),
            entry("projectile_ward", "minecraft:leather_chestplate", "minecraft:shield", "minecraft:arrow"),
            entry("prismatic_spray", "minecraft:glass_pane", "ebwizardry:magic_crystal_grand", "ebwizardry:astral_diamond"),
            entry("silencing_sigil", "minecraft:note_block", "minecraft:redstone_block"),
            entry("skull_sentinel", "minecraft:skeleton_skull", "ebwizardry:magic_crystal_grand"),
            entry("water_walking", "minecraft:water_bucket", "minecraft:diamond_boots", "ebwizardry:magic_crystal_grand"),
            entry("essence_extraction", "ebwizardry:mana_flask_large", "ebwizardry:receptacle", "ebwizardry:magic_crystal_grand"),
            entry("contingency_fire", "ebwizardry:blank_scroll", "minecraft:splash_potion", "ebwizardry:crystal_block"),
            entry("contingency_fire", "ebwizardry:blank_scroll", "minecraft:splash_potion", "ebwizardry:crystal_block"),
            entry("contingency_damage", "ebwizardry:blank_scroll", "minecraft:splash_potion", "ebwizardry:crystal_block"),
            entry("contingency_critical_health", "ebwizardry:blank_scroll", "minecraft:splash_potion", "ebwizardry:crystal_block"),
            entry("contingency_death", "ebwizardry:blank_scroll", "minecraft:splash_potion", "ebwizardry:crystal_block"),
            entry("contingency_drowning", "ebwizardry:blank_scroll", "minecraft:splash_potion", "ebwizardry:crystal_block"),
            entry("contingency_hostile_spellcast", "ebwizardry:blank_scroll", "ebwizardry:wizard_hat", "ebwizardry:crystal_block"),
            entry("contingency_immobility", "ebwizardry:blank_scroll", "minecraft:splash_potion", "ebwizardry:crystal_block"),
            entry("metamagic_projectile", "ebwizardry:wand_novice", "ebwizardry:crystal_block"),
            entry("wizard_shield", "ebwizardry:wand_novice", "ebwizardry:crystal_block"),
            entry("shrink_self", "minecraft:speckled_melon", "minecraft:golden_apple"),
            entry("grow_self", "minecraft:golden_carrot", "minecraft:golden_apple"),
            entry("mass_shrink", "minecraft:speckled_melon", "minecraft:golden_apple", "ebwizardry:magic_crystal_grand"),
            entry("mass_growth", "minecraft:golden_carrot", "minecraft:golden_apple", "ebwizardry:magic_crystal_grand"),
            entry("permashrink", "minecraft:speckled_melon", "minecraft:golden_apple", "ebwizardry:astral_diamond"),
            entry("permagrowth", "minecraft:golden_carrot", "minecraft:golden_apple", "ebwizardry:astral_diamond"),
            entry("words_of_unbinding", "minecraft:enchanting_table", "ebwizardry:magic_crystal_grand"),
            entry("astral_projection", "minecraft:ghast_tear", "ebwizardry:magic_crystal_grand", "minecraft:golden_apple"),
            entry("dimensional_anchor", "minecraft:splash_potion"),
            entry("conjure_lesser_sentry", "minecraft:gold_block", "ebwizardry:magic_crystal"),
            entry("conjure_greater_sentry", "minecraft:diamond_block", "ebwizardry:magic_crystal_grand"));

    private static Entry entry(String spell, String... items) {
        return new Entry(spell, List.of(items));
    }

    public static Spell spellOf(Entry entry) {
        return Services.REGISTRY_UTIL.getSpell(ResourceLocation.fromNamespaceAndPath("ancientspellcraft", entry.spellId()));
    }

    /** Itens resolvidos da entrada, ou null se algum estiver ausente. */
    public static List<Item> itemsOf(Entry entry) {
        List<Item> items = new ArrayList<>();
        for (String id : entry.itemIds()) {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
            if (item == Items.AIR) return null;
            items.add(item);
        }
        return items;
    }

    public static Entry entryFor(Spell spell) {
        for (Entry entry : TABLE) {
            Spell s = spellOf(entry);
            if (s == spell && itemsOf(entry) != null) return entry;
        }
        return null;
    }

    public static List<Spell> spellsByTier(SpellTier tier) {
        List<Spell> spells = new ArrayList<>();
        for (Entry entry : TABLE) {
            Spell spell = spellOf(entry);
            if (spell != null && spell.getTier() == tier && itemsOf(entry) != null) spells.add(spell);
        }
        return spells;
    }

    private SpellComponents() {
    }
}
