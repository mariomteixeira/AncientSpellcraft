package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ASSpellBookItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Comparator;
import java.util.function.Supplier;

public final class ASItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AncientSpellcraft.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AncientSpellcraft.MODID);

    public static final Supplier<Item> ANCIENT_SPELLCRAFT_SPELL_BOOK =
            ITEMS.register("ancient_spellcraft_spell_book", ASSpellBookItem::new);

    public static final Supplier<Item> ANCIENT_SPELLCRAFT_SCROLL = ITEMS.register("ancient_spellcraft_scroll",
            () -> new com.koomplo.wizardry.content.item.ScrollItem(new Item.Properties()));

    public static final Supplier<Item> DEVORITIUM_INGOT = ITEMS.register("devoritium_ingot",
            () -> new Item(new Item.Properties()));

    public static final Supplier<Item> DEVORITIUM_NUGGET = ITEMS.register("devoritium_nugget",
            () -> new Item(new Item.Properties()));

    /** Orbes do warlock: 4 tiers x 8 elementos (1.12.2 ItemWarlockOrb), wands completas da classe. */
    static {
        String[] tiers = {"novice", "apprentice", "advanced", "master"};
        for (int t = 0; t < tiers.length; t++) {
            final int tierIndex = t;
            for (String element : new String[]{"magic", "fire", "ice", "lightning", "necromancy", "earth", "sorcery", "healing"}) {
                ITEMS.register("warlock_orb_" + tiers[t] + "_" + element,
                        () -> new com.windanesz.ancientspellcraft.item.WarlockOrbItem(
                                switch (tierIndex) {
                                    case 1 -> com.koomplo.wizardry.setup.registries.SpellTiers.APPRENTICE;
                                    case 2 -> com.koomplo.wizardry.setup.registries.SpellTiers.ADVANCED;
                                    case 3 -> com.koomplo.wizardry.setup.registries.SpellTiers.MASTER;
                                    default -> com.koomplo.wizardry.setup.registries.SpellTiers.NOVICE;
                                },
                                element.equals("magic") ? com.koomplo.wizardry.setup.registries.Elements.MAGIC
                                        : com.koomplo.wizardry.core.platform.Services.REGISTRY_UTIL.getElement(
                                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ebwizardry", element))));
            }
        }
    }

    public static final Supplier<Item> STONE_TABLET_SMALL = ITEMS.register("stone_tablet_small",
            () -> new com.windanesz.ancientspellcraft.item.RelicItem(com.koomplo.wizardry.setup.registries.SpellTiers.NOVICE, net.minecraft.world.item.Rarity.COMMON));
    public static final Supplier<Item> STONE_TABLET = ITEMS.register("stone_tablet",
            () -> new com.windanesz.ancientspellcraft.item.RelicItem(com.koomplo.wizardry.setup.registries.SpellTiers.APPRENTICE, net.minecraft.world.item.Rarity.UNCOMMON));
    public static final Supplier<Item> STONE_TABLET_LARGE = ITEMS.register("stone_tablet_large",
            () -> new com.windanesz.ancientspellcraft.item.RelicItem(com.koomplo.wizardry.setup.registries.SpellTiers.ADVANCED, net.minecraft.world.item.Rarity.RARE));
    public static final Supplier<Item> STONE_TABLET_GRAND = ITEMS.register("stone_tablet_grand",
            () -> new com.windanesz.ancientspellcraft.item.RelicItem(com.koomplo.wizardry.setup.registries.SpellTiers.MASTER, net.minecraft.world.item.Rarity.EPIC));

    public static final Supplier<Item> RUNE_ALGIZ = ITEMS.register("rune_algiz", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_ANSUZ = ITEMS.register("rune_ansuz", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_BERKANAN = ITEMS.register("rune_berkanan", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_DAGAZ = ITEMS.register("rune_dagaz", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_EHWAZ = ITEMS.register("rune_ehwaz", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_FEOH = ITEMS.register("rune_feoh", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_GYFU = ITEMS.register("rune_gyfu", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_HAGLAZ = ITEMS.register("rune_haglaz", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_IHWAZ = ITEMS.register("rune_ihwaz", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_ISAZ = ITEMS.register("rune_isaz", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_JERA = ITEMS.register("rune_jera", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_KAUNAN = ITEMS.register("rune_kaunan", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_LAGUZ = ITEMS.register("rune_laguz", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_MANNAZ = ITEMS.register("rune_mannaz", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_NAUDIZ = ITEMS.register("rune_naudiz", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_ODAL = ITEMS.register("rune_odal", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_PEORTH = ITEMS.register("rune_peorth", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_RAIDO = ITEMS.register("rune_raido", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_SOWILO = ITEMS.register("rune_sowilo", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_THURISAZ = ITEMS.register("rune_thurisaz", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_TIWAZ = ITEMS.register("rune_tiwaz", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_URUZ = ITEMS.register("rune_uruz", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_WYNN = ITEMS.register("rune_wynn", com.windanesz.ancientspellcraft.item.RuneItem::new);
    public static final Supplier<Item> RUNE_YNGVI = ITEMS.register("rune_yngvi", com.windanesz.ancientspellcraft.item.RuneItem::new);

    public static final Supplier<Item> BATTLEMAGE_SWORD_NOVICE = ITEMS.register("battlemage_sword_novice",
            () -> new com.windanesz.ancientspellcraft.item.BattlemageSwordItem(com.koomplo.wizardry.setup.registries.SpellTiers.NOVICE, 3));
    public static final Supplier<Item> BATTLEMAGE_SWORD_APPRENTICE = ITEMS.register("battlemage_sword_apprentice",
            () -> new com.windanesz.ancientspellcraft.item.BattlemageSwordItem(com.koomplo.wizardry.setup.registries.SpellTiers.APPRENTICE, 5));
    public static final Supplier<Item> BATTLEMAGE_SWORD_ADVANCED = ITEMS.register("battlemage_sword_advanced",
            () -> new com.windanesz.ancientspellcraft.item.BattlemageSwordItem(com.koomplo.wizardry.setup.registries.SpellTiers.ADVANCED, 7));
    public static final Supplier<Item> BATTLEMAGE_SWORD_MASTER = ITEMS.register("battlemage_sword_master",
            () -> new com.windanesz.ancientspellcraft.item.BattlemageSwordItem(com.koomplo.wizardry.setup.registries.SpellTiers.MASTER, 9));
    public static final Supplier<Item> BATTLEMAGE_SWORD_HILT = ITEMS.register("battlemage_sword_hilt",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> BATTLEMAGE_SWORD_BLADE = ITEMS.register("battlemage_sword_blade",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> BATTLEMAGE_SHIELD = ITEMS.register("battlemage_shield",
            com.windanesz.ancientspellcraft.item.BattlemageShieldItem::new);
    public static final Supplier<Item> CRYSTAL_SILVER_INGOT = ITEMS.register("crystal_silver_ingot",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CRYSTAL_SILVER_NUGGET = ITEMS.register("crystal_silver_nugget",
            () -> new Item(new Item.Properties()));

    public static final Supplier<Item> RITUAL_BOOK = ITEMS.register("ritual_book",
            com.windanesz.ancientspellcraft.item.RitualBookItem::new);

    public static final Supplier<Item> MYSTIC_SPELL_BOOK = ITEMS.register("mystic_spell_book",
            com.windanesz.ancientspellcraft.item.MysticSpellBookItem::new);

    public static final Supplier<Item> FORBIDDEN_TOME = ITEMS.register("forbidden_tome",
            com.windanesz.ancientspellcraft.item.ForbiddenTomeItem::new);

    public static final Supplier<Item> SHADOW_BLADE = ITEMS.register("shadow_blade",
            com.windanesz.ancientspellcraft.item.ShadowBladeItem::new);
    public static final Supplier<Item> SPECTRAL_FISHING_ROD = ITEMS.register("spectral_fishing_rod",
            com.windanesz.ancientspellcraft.item.SpectralFishingRodItem::new);
    public static final Supplier<Item> ICE_CREAM = ITEMS.register("ice_cream",
            com.windanesz.ancientspellcraft.item.IceCreamItem::new);

    public static final Supplier<Item> STONE_FIST = ITEMS.register("stone_fist",
            () -> new com.windanesz.ancientspellcraft.item.StoneFistItem(net.minecraft.world.item.Tiers.STONE, 0));
    public static final Supplier<Item> ADVANCED_STONE_FIST = ITEMS.register("advanced_stone_fist",
            () -> new com.windanesz.ancientspellcraft.item.StoneFistItem(net.minecraft.world.item.Tiers.STONE, 3));

    static {
        for (var entry : ASBlocks.CRYSTAL_ORES.entrySet()) {
            ITEMS.register("crystal_ore_" + entry.getKey(),
                    () -> new net.minecraft.world.item.BlockItem(entry.getValue().get(), new Item.Properties()));
        }
        for (String name : new String[]{"devoritium_block", "devoritium_ore", "devoritium_gilded_stone", "devoritium_bars", "devoritium_door", "ice_crafting_table", "imbuement_altar_ruined", "sphere_cognizance",
                "scribing_desk", "arcane_anvil", "sealed_stone", "unsealed_stone", "sentinel_block", "sentinel_block_diamond", "sage_lectern", "unseal_button"}) {
            ITEMS.register(name, () -> new net.minecraft.world.item.BlockItem(
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, name)),
                    new Item.Properties()));
        }
    }

    /** Livros e scrolls de todas as spells do AS (preenche conforme os lotes de spells chegam). */
    public static final Supplier<CreativeModeTab> AS_TAB = CREATIVE_TABS.register("ancientspellcraft",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ANCIENT_SPELLCRAFT_SPELL_BOOK.get()))
                    .title(Component.translatable("itemGroup.ancientspellcraft"))
                    .displayItems((parameters, output) -> {
                        var spells = com.koomplo.wizardry.core.platform.Services.REGISTRY_UTIL.getSpells().stream()
                                .filter(spell -> spell.getLocation().getNamespace().equals(AncientSpellcraft.MODID))
                                .sorted(Comparator.comparingInt(s -> s.getTier().getLevel()))
                                .toList();
                        for (var spell : spells) {
                            Item book = spell.applicableForItem(FORBIDDEN_TOME.get()) ? FORBIDDEN_TOME.get()
                                    : spell.applicableForItem(MYSTIC_SPELL_BOOK.get())
                                    ? MYSTIC_SPELL_BOOK.get() : ANCIENT_SPELLCRAFT_SPELL_BOOK.get();
                            output.accept(com.koomplo.wizardry.api.content.util.RegistryUtils.setSpell(
                                    new ItemStack(book), spell));
                        }
                        for (var spell : spells) {
                            output.accept(com.koomplo.wizardry.api.content.util.RegistryUtils.setSpell(
                                    new ItemStack(ANCIENT_SPELLCRAFT_SCROLL.get()), spell));
                        }
                    })
                    .build());

    /** Equipamentos, materiais e artefatos. */
    public static final Supplier<CreativeModeTab> GEAR_TAB = CREATIVE_TABS.register("ancientspellcraft_gear",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(DEVORITIUM_INGOT.get()))
                    .title(Component.translatable("itemGroup.ancientspellcraftgear"))
                    .displayItems((parameters, output) -> {
                        output.accept(DEVORITIUM_INGOT.get());
                        output.accept(BATTLEMAGE_SWORD_NOVICE.get());
                        output.accept(BATTLEMAGE_SWORD_APPRENTICE.get());
                        output.accept(BATTLEMAGE_SWORD_ADVANCED.get());
                        output.accept(BATTLEMAGE_SWORD_MASTER.get());
                        output.accept(BATTLEMAGE_SWORD_HILT.get());
                        output.accept(BATTLEMAGE_SWORD_BLADE.get());
                        output.accept(BATTLEMAGE_SHIELD.get());
                        output.accept(CRYSTAL_SILVER_INGOT.get());
                        output.accept(CRYSTAL_SILVER_NUGGET.get());
                        for (String rune : new String[]{"algiz", "ansuz", "berkanan", "dagaz", "ehwaz", "feoh", "gyfu", "haglaz", "ihwaz", "isaz", "jera", "kaunan", "laguz", "mannaz", "naudiz", "odal", "peorth", "raido", "sowilo", "thurisaz", "tiwaz", "uruz", "wynn", "yngvi"}) {
                            output.accept(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                            com.windanesz.ancientspellcraft.AncientSpellcraft.MODID, "rune_" + rune)));
                        }
                        for (String ritual : com.windanesz.ancientspellcraft.ritual.ASRituals.RITUALS) {
                            output.accept(com.windanesz.ancientspellcraft.item.RitualBookItem.withRitual(ritual));
                        }
                        for (String name : new String[]{"scribing_desk", "arcane_anvil", "sealed_stone", "unsealed_stone",
                                "sentinel_block", "sentinel_block_diamond", "sage_lectern", "unseal_button"}) {
                            output.accept(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                            com.windanesz.ancientspellcraft.AncientSpellcraft.MODID, name)));
                        }
                        output.accept(STONE_TABLET_SMALL.get());
                        output.accept(STONE_TABLET.get());
                        output.accept(STONE_TABLET_LARGE.get());
                        output.accept(STONE_TABLET_GRAND.get());
                        for (String t : new String[]{"novice", "apprentice", "advanced", "master"}) {
                            for (String e : new String[]{"magic", "fire", "ice", "lightning", "necromancy", "earth", "sorcery", "healing"}) {
                                output.accept(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                                com.windanesz.ancientspellcraft.AncientSpellcraft.MODID, "warlock_orb_" + t + "_" + e)));
                            }
                        }
                        output.accept(DEVORITIUM_NUGGET.get());
                        for (String name : new String[]{"devoritium_block", "devoritium_ore", "devoritium_gilded_stone",
                                "devoritium_bars", "devoritium_door", "crystal_ore_fire", "crystal_ore_earth",
                                "crystal_ore_healing", "crystal_ore_ice", "crystal_ore_lightning",
                                "crystal_ore_necromancy", "crystal_ore_sorcery", "ice_crafting_table", "imbuement_altar_ruined", "sphere_cognizance"}) {
                            output.accept(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(com.windanesz.ancientspellcraft.AncientSpellcraft.MODID, name)));
                        }
                    })
                    .build());

    private ASItems() {
    }
}
