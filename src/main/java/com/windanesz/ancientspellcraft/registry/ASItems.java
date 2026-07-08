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
                            output.accept(com.koomplo.wizardry.api.content.util.RegistryUtils.setSpell(
                                    new ItemStack(ANCIENT_SPELLCRAFT_SPELL_BOOK.get()), spell));
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
                        output.accept(DEVORITIUM_NUGGET.get());
                    })
                    .build());

    private ASItems() {
    }
}
