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
            () -> new com.windanesz.ancientspellcraft.item.DevoritiumItem(new Item.Properties()));

    public static final Supplier<Item> DEVORITIUM_NUGGET = ITEMS.register("devoritium_nugget",
            () -> new com.windanesz.ancientspellcraft.item.DevoritiumItem(new Item.Properties()));

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
    public static final Supplier<Item> BATTLEMAGE_CONTRACT = ITEMS.register("battlemage_contract",
            com.windanesz.ancientspellcraft.item.BattlemageContractItem::new);

    // AS-15: tomos do sage (wand da classe SAGE; progressão via Sage Lectern)
    public static final Supplier<Item> SAGE_TOME_NOVICE = ITEMS.register("sage_tome_novice",
            () -> new com.windanesz.ancientspellcraft.item.SageTomeItem(com.koomplo.wizardry.setup.registries.SpellTiers.NOVICE));
    public static final Supplier<Item> SAGE_TOME_APPRENTICE = ITEMS.register("sage_tome_apprentice",
            () -> new com.windanesz.ancientspellcraft.item.SageTomeItem(com.koomplo.wizardry.setup.registries.SpellTiers.APPRENTICE));
    public static final Supplier<Item> SAGE_TOME_ADVANCED = ITEMS.register("sage_tome_advanced",
            () -> new com.windanesz.ancientspellcraft.item.SageTomeItem(com.koomplo.wizardry.setup.registries.SpellTiers.ADVANCED));
    public static final Supplier<Item> SAGE_TOME_MASTER = ITEMS.register("sage_tome_master",
            () -> new com.windanesz.ancientspellcraft.item.SageTomeItem(com.koomplo.wizardry.setup.registries.SpellTiers.MASTER));
    public static final Supplier<Item> ENCHANTED_PAGE = ITEMS.register("enchanted_page",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> TOME_CONTROLLER = ITEMS.register("tome_controller",
            com.windanesz.ancientspellcraft.item.TomeControllerItem::new);
    public static final Supplier<Item> UNSEALING_SCROLL = ITEMS.register("unsealing_scroll",
            com.windanesz.ancientspellcraft.item.UnsealingScrollItem::new);

    // AS-18: artefatos — item do Redux (Curios quando presente, senão hotbar/inventário)
    static Supplier<Item> artifact(String name, net.minecraft.world.item.Rarity rarity,
                                   @org.jetbrains.annotations.Nullable com.koomplo.wizardry.core.IArtifactEffect effect) {
        return ITEMS.register(name, () -> com.koomplo.wizardry.core.integrations.ArtifactChannel.createArtifact(rarity, effect));
    }

    // ganchos externos (efeito checado no ponto de uso via ArtifactChannel.isEquipped)
    public static final Supplier<Item> CHARM_SEED_BAG = artifact("charm_seed_bag", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> RING_LILY_FLOWER = artifact("ring_lily_flower", net.minecraft.world.item.Rarity.UNCOMMON, null);
    public static final Supplier<Item> CHARM_HOARDERS_ORB = artifact("charm_hoarders_orb", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> CHARM_PRISMATIC_SPRAY = artifact("charm_prismatic_spray", net.minecraft.world.item.Rarity.EPIC, null);
    public static final Supplier<Item> RING_CHAOS_BLAST_MULTITARGET = artifact("ring_chaos_blast_multitarget", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> RING_ETERNAL_CONTINGENCY = artifact("ring_eternal_contingency", net.minecraft.world.item.Rarity.EPIC, null);
    public static final Supplier<Item> RING_PERMANENT_SHRINKAGE = artifact("ring_permanent_shrinkage", net.minecraft.world.item.Rarity.EPIC, null);
    public static final Supplier<Item> RING_PERMANENT_GROWTH = artifact("ring_permanent_growth", net.minecraft.world.item.Rarity.EPIC, null);
    public static final Supplier<Item> AMULET_ELEMENTAL_OFFENSE = ITEMS.register("amulet_elemental_offense",
            () -> new com.windanesz.ancientspellcraft.item.SocketedArtifactItem(net.minecraft.world.item.Rarity.UNCOMMON,
                    stack -> stack.getItem() instanceof com.koomplo.wizardry.content.item.CrystalItem,
                    com.windanesz.ancientspellcraft.item.ASArtifactEffects.socketOffense()));
    public static final Supplier<Item> RING_ABSORB_CRYSTAL = artifact("ring_absorb_crystal", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> CHARM_POTION_KIT = artifact("charm_potion_kit", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> CHARM_SHADOW_BLADE = artifact("charm_shadow_blade", net.minecraft.world.item.Rarity.EPIC, null);
    public static final Supplier<Item> CHARM_WARDROBE = artifact("charm_wardrobe", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> CHARM_METEORITE_STONE = artifact("charm_meteorite_stone", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> RING_MANA_TRANSFER = artifact("ring_mana_transfer", net.minecraft.world.item.Rarity.UNCOMMON, null);
    public static final Supplier<Item> CHARM_PROGRESSION_ORB = artifact("charm_progression_orb", net.minecraft.world.item.Rarity.EPIC, null);
    public static final Supplier<Item> RING_DISENCHANTER = artifact("ring_disenchanter", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> BELT_HORSE = artifact("belt_horse", net.minecraft.world.item.Rarity.UNCOMMON, null);
    public static final Supplier<Item> CHARM_SENTINEL_EYE = artifact("charm_sentinel_eye", net.minecraft.world.item.Rarity.UNCOMMON, null);
    public static final Supplier<Item> AMULET_DOMUS = artifact("amulet_domus", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> RING_KINETIC = artifact("ring_kinetic", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> CHARM_GLYPH_ILLUMINATION = artifact("charm_glyph_illumination", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> CHARM_MAGIC_LIGHT = artifact("charm_magic_light", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> HEAD_SHIELD = artifact("head_shield", net.minecraft.world.item.Rarity.EPIC, null);

    // modificadores de cast (1.12.2 ASEventHandler; valores fiéis, orb bonus default = 30)
    public static final Supplier<Item> CHARM_MANA_ORB = artifact("charm_mana_orb", net.minecraft.world.item.Rarity.UNCOMMON,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.costMultiplier(0.85f));
    public static final Supplier<Item> AMULET_MANA = artifact("amulet_mana", net.minecraft.world.item.Rarity.UNCOMMON,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.costMultiplier(0.90f));
    public static final Supplier<Item> RING_MANA_COST = artifact("ring_mana_cost", net.minecraft.world.item.Rarity.UNCOMMON,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.costMultiplier(0.95f));
    public static final Supplier<Item> RING_BLAST = artifact("ring_blast", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.tradeoff(com.koomplo.wizardry.api.content.spell.internal.SpellModifiers.BLAST));
    public static final Supplier<Item> RING_RANGE = artifact("ring_range", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.tradeoff(com.koomplo.wizardry.api.content.spell.internal.SpellModifiers.RANGE));
    public static final Supplier<Item> RING_DURATION = artifact("ring_duration", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.tradeoff(com.koomplo.wizardry.api.content.spell.internal.SpellModifiers.DURATION));
    public static final Supplier<Item> CHARM_ELEMENTAL_GRIMOIRE = artifact("charm_elemental_grimoire", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.grimoire(
                    () -> com.koomplo.wizardry.setup.registries.Elements.FIRE,
                    () -> com.koomplo.wizardry.setup.registries.Elements.ICE,
                    () -> com.koomplo.wizardry.setup.registries.Elements.LIGHTNING));
    public static final Supplier<Item> CHARM_FIRE_ORB = artifact("charm_fire_orb", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.elementOrb(() -> com.koomplo.wizardry.setup.registries.Elements.FIRE));
    public static final Supplier<Item> CHARM_ICE_ORB = artifact("charm_ice_orb", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.elementOrb(() -> com.koomplo.wizardry.setup.registries.Elements.ICE));
    public static final Supplier<Item> CHARM_LIGHTNING_ORB = artifact("charm_lightning_orb", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.elementOrb(() -> com.koomplo.wizardry.setup.registries.Elements.LIGHTNING));
    public static final Supplier<Item> CHARM_EARTH_ORB = artifact("charm_earth_orb", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.elementOrb(() -> com.koomplo.wizardry.setup.registries.Elements.EARTH));
    public static final Supplier<Item> CHARM_HEALING_ORB = artifact("charm_healing_orb", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.elementOrb(() -> com.koomplo.wizardry.setup.registries.Elements.HEALING));
    public static final Supplier<Item> CHARM_NECROMANCY_ORB = artifact("charm_necromancy_orb", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.elementOrb(() -> com.koomplo.wizardry.setup.registries.Elements.NECROMANCY));
    public static final Supplier<Item> CHARM_SORCERY_ORB = artifact("charm_sorcery_orb", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.elementOrb(() -> com.koomplo.wizardry.setup.registries.Elements.SORCERY));
    public static final Supplier<Item> RING_POWER = artifact("ring_power", net.minecraft.world.item.Rarity.UNCOMMON,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.powerJewel(0.05f));
    public static final Supplier<Item> AMULET_POWER = artifact("amulet_power", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.powerJewel(0.10f));
    public static final Supplier<Item> CHARM_POWER_ORB = artifact("charm_power_orb", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.powerJewel(0.20f));
    public static final Supplier<Item> RING_METAMAGIC_PRESERVE = artifact("ring_metamagic_preserve", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> CHARM_METAMAGIC_AMPLIFIER = artifact("charm_metamagic_amplifier", net.minecraft.world.item.Rarity.RARE, null);

    // onda 2a: defesas de dano recebido (1.12.2 onLivingHurtEvent)
    public static final Supplier<Item> CHARM_CRYOSTASIS = artifact("charm_cryostasis", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.cryostasis());
    public static final Supplier<Item> RING_PROTECTOR = artifact("ring_protector", net.minecraft.world.item.Rarity.UNCOMMON,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.protector());
    public static final Supplier<Item> RING_BERSERKER = artifact("ring_berserker", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.lowHealthBuff(
                    () -> net.minecraft.world.effect.MobEffects.DAMAGE_BOOST, 300, 0));
    public static final Supplier<Item> AMULET_RABBIT = artifact("amulet_rabbit", net.minecraft.world.item.Rarity.UNCOMMON,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.rabbit());
    public static final Supplier<Item> AMULET_SHIELD = artifact("amulet_shield", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.shieldAmulet());
    public static final Supplier<Item> BELT_SOUL_SCORCH = artifact("belt_soul_scorch", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.soulScorch());

    // onda 2b: morte e tempo
    public static final Supplier<Item> RING_UNDEATH = artifact("ring_undeath", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.undeath());
    public static final Supplier<Item> AMULET_TIME_KNOT = artifact("amulet_time_knot", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.timeKnot());
    public static final Supplier<Item> AMULET_TIME_SLOW = artifact("amulet_time_slow", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.timeSlow());
    public static final Supplier<Item> CHARM_REANIMATION = artifact("charm_reanimation", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.reanimation());
    public static final Supplier<Item> CHARM_PLUNDERERS_MARK = artifact("charm_plunderers_mark", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.plunderersMark());

    // onda 2c: poções/flechas/tick (efeitos de poção no ASPotionEvents.onEffectApplicable;
    // poison_arrow no ASSpellEvents.onProjectileImpact)
    public static final Supplier<Item> BELT_TEMPORAL_ANCHOR = artifact("belt_temporal_anchor", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> AMULET_POISON_RESISTANCE = artifact("amulet_poison_resistance", net.minecraft.world.item.Rarity.UNCOMMON, null);
    public static final Supplier<Item> AMULET_CURSE_WARD = artifact("amulet_curse_ward", net.minecraft.world.item.Rarity.EPIC, null);
    public static final Supplier<Item> AMULET_PERSISTENCE = artifact("amulet_persistence", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> AMULET_CURSED_MIRROR = artifact("amulet_cursed_mirror", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> AMULET_ABSORB_POTION = artifact("amulet_absorb_potion", net.minecraft.world.item.Rarity.RARE, null);
    public static final Supplier<Item> RING_POISON_ARROW = artifact("ring_poison_arrow", net.minecraft.world.item.Rarity.UNCOMMON, null);
    public static final Supplier<Item> HEAD_MASK_OF_SILENCE = artifact("head_mask_of_silence", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.maskOfSilence());
    public static final Supplier<Item> RING_PRISMARINE = artifact("ring_prismarine", net.minecraft.world.item.Rarity.UNCOMMON,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.prismarine());

    // onda 3a: buffs de tick
    public static final Supplier<Item> AMULET_CELERITY = artifact("amulet_celerity", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.celerity());
    public static final Supplier<Item> AMULET_OAKFLESH = artifact("amulet_oakflesh", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.oakflesh());
    public static final Supplier<Item> AMULET_HOLY_AURA = artifact("amulet_holy_aura", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.holyAura());
    public static final Supplier<Item> AMULET_HEALING_MUSHROOM = artifact("amulet_healing_mushroom", net.minecraft.world.item.Rarity.UNCOMMON,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.healingMushroom());
    public static final Supplier<Item> AMULET_CURSED_PENDANT = artifact("amulet_cursed_pendant", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.cursedPendant());

    // onda 4: baterias de mana, mana própria e sockets
    public static final Supplier<Item> RING_MANA_LESSER = ITEMS.register("ring_mana_lesser",
            () -> new com.windanesz.ancientspellcraft.item.ManaArtifactItem(net.minecraft.world.item.Rarity.UNCOMMON, 500, null));
    public static final Supplier<Item> RING_MANA_GREATER = ITEMS.register("ring_mana_greater",
            () -> new com.windanesz.ancientspellcraft.item.ManaArtifactItem(net.minecraft.world.item.Rarity.RARE, 1000, null));
    public static final Supplier<Item> CHARM_MAJESTIC_MANA = ITEMS.register("charm_majestic_mana",
            () -> new com.windanesz.ancientspellcraft.item.ManaArtifactItem(net.minecraft.world.item.Rarity.RARE, 2500, null));
    public static final Supplier<Item> CHARM_WIZARD_TANKARD = ITEMS.register("charm_wizard_tankard",
            () -> new com.windanesz.ancientspellcraft.item.WizardTankardItem(net.minecraft.world.item.Rarity.RARE));
    public static final Supplier<Item> CHARM_CUBE_PHASING = ITEMS.register("charm_cube_phasing",
            () -> new com.windanesz.ancientspellcraft.item.CubePhasingItem(net.minecraft.world.item.Rarity.EPIC));
    public static final Supplier<Item> AMULET_INVISIBILITY = ITEMS.register("amulet_invisibility",
            () -> new com.windanesz.ancientspellcraft.item.ManaArtifactItem(net.minecraft.world.item.Rarity.RARE, 1500,
                    com.windanesz.ancientspellcraft.item.ASArtifactEffects.invisibilityAmulet()));
    public static final Supplier<Item> AMULET_OF_RESISTANCE = ITEMS.register("amulet_of_resistance",
            () -> new com.windanesz.ancientspellcraft.item.ResistanceAmuletItem(net.minecraft.world.item.Rarity.RARE));
    public static final Supplier<Item> AMULET_ELEMENTAL_DEFENSE = ITEMS.register("amulet_elemental_defense",
            () -> new com.windanesz.ancientspellcraft.item.SocketedArtifactItem(net.minecraft.world.item.Rarity.RARE,
                    stack -> stack.getItem() instanceof com.koomplo.wizardry.content.item.CrystalItem,
                    com.windanesz.ancientspellcraft.item.ASArtifactEffects.socketDefense()));
    public static final Supplier<Item> AMULET_PENDANT_OF_ETERNITY = ITEMS.register("amulet_pendant_of_eternity",
            () -> new com.windanesz.ancientspellcraft.item.SocketedArtifactItem(net.minecraft.world.item.Rarity.EPIC,
                    stack -> stack.getItem() instanceof com.koomplo.wizardry.content.item.SpellBookItem
                            && com.koomplo.wizardry.api.content.util.RegistryUtils.getSpell(stack)
                            instanceof com.koomplo.wizardry.content.spell.abstr.BuffSpell, null));

    // onda 3d: tickables especiais
    public static final Supplier<Item> CHARM_SPECTERLIGHT_TORCH = artifact("charm_specterlight_torch", net.minecraft.world.item.Rarity.UNCOMMON,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.specterlightTorch());
    public static final Supplier<Item> BELT_ENCHANTED_WHETSTONE = artifact("belt_enchanted_whetstone", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.enchantedWhetstone());
    public static final Supplier<Item> AMULET_SEARING_SKIN = artifact("amulet_searing_skin", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.searingSkin());
    public static final Supplier<Item> HEAD_ARDOR_CROWN = artifact("head_ardor_crown", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.ardorCrown());
    public static final Supplier<Item> HEAD_MINION_CIRCLE = artifact("head_minion_circle", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.minionCircle());
    public static final Supplier<Item> CHARM_GUARDIAN_BLADE = artifact("charm_guardian_blade", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.guardianBlade());
    public static final Supplier<Item> AMULET_ELEMENTAL_AURA = artifact("amulet_elemental_aura", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.elementalAura());

    // onda 3b: glyphs do battlemage (charge/imbuement têm gates no RunewordSpell/registro do imbue)
    public static final Supplier<Item> CHARM_GLYPH_LEECHING = artifact("charm_glyph_leeching", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.glyphLeeching());
    public static final Supplier<Item> CHARM_GLYPH_ANTIGRAVITY = artifact("charm_glyph_antigravity", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.glyphAntigravity());
    public static final Supplier<Item> CHARM_GLYPH_MIGHT = artifact("charm_glyph_might", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.glyphMight());
    public static final Supplier<Item> CHARM_GLYPH_CHARGE = artifact("charm_glyph_charge", net.minecraft.world.item.Rarity.EPIC, null);
    public static final Supplier<Item> CHARM_GLYPH_IMBUEMENT = artifact("charm_glyph_imbuement", net.minecraft.world.item.Rarity.RARE, null);
    // AS-29: gate no BattlemageShieldItem.inventoryTick (remove o cooldown de quebra de guarda)
    public static final Supplier<Item> CHARM_GLYPH_SHIELD_DISABLE = artifact("charm_glyph_shield_disable", net.minecraft.world.item.Rarity.RARE, null);

    // onda 3c: auras do battlemage (tick 0.5s, raio 10, exigem o set completo)
    public static final Supplier<Item> CHARM_AURA_ALACRITY = artifact("charm_aura_alacrity", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.battlemageAura((player, nearby) -> {
                for (var e : nearby) {
                    if (com.windanesz.ancientspellcraft.item.ASArtifactEffects.isAuraAlly(player, e)) {
                        com.windanesz.ancientspellcraft.item.ASArtifactEffects.applyAuraEffect(player, e,
                                net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 60, 0, false);
                    }
                }
                com.windanesz.ancientspellcraft.item.ASArtifactEffects.applyAuraEffect(player, player,
                        net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 60, 0, false);
            }));
    public static final Supplier<Item> CHARM_AURA_HATRED = artifact("charm_aura_hatred", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.battlemageAura((player, nearby) -> {
                for (var e : nearby) {
                    if (com.windanesz.ancientspellcraft.item.ASArtifactEffects.isAuraAlly(player, e)
                            && e.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD)) {
                        com.windanesz.ancientspellcraft.item.ASArtifactEffects.applyAuraEffect(player, e,
                                net.minecraft.world.effect.MobEffects.DAMAGE_BOOST, 60, 0, false);
                    }
                }
            }));
    public static final Supplier<Item> CHARM_AURA_LIFE = artifact("charm_aura_life", net.minecraft.world.item.Rarity.EPIC,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.battlemageAura((player, nearby) -> {
                for (var e : nearby) {
                    if (com.windanesz.ancientspellcraft.item.ASArtifactEffects.isAuraAlly(player, e)) {
                        e.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                net.minecraft.world.effect.MobEffects.REGENERATION, 60, 0));
                    }
                }
                com.windanesz.ancientspellcraft.item.ASArtifactEffects.applyAuraEffect(player, player,
                        net.minecraft.world.effect.MobEffects.REGENERATION, 60, 0, false);
            }));
    public static final Supplier<Item> CHARM_AURA_PURITY = artifact("charm_aura_purity", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.battlemageAura((player, nearby) -> {
                for (var e : nearby) {
                    if (com.windanesz.ancientspellcraft.item.ASArtifactEffects.isAuraEnemy(player, e)
                            && e.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD)) {
                        com.windanesz.ancientspellcraft.item.ASArtifactEffects.applyAuraEffect(player, e,
                                net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false);
                        com.windanesz.ancientspellcraft.item.ASArtifactEffects.applyAuraEffect(player, e,
                                net.minecraft.world.effect.MobEffects.WEAKNESS, 60, 0, false);
                    }
                }
            }));
    public static final Supplier<Item> CHARM_AURA_WARDING = artifact("charm_aura_warding", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.battlemageAura((player, nearby) -> {
                var ward = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                        com.koomplo.wizardry.setup.registries.EBMobEffects.WARD.get());
                for (var e : nearby) {
                    if (com.windanesz.ancientspellcraft.item.ASArtifactEffects.isAuraAlly(player, e)) {
                        com.windanesz.ancientspellcraft.item.ASArtifactEffects.applyAuraEffect(player, e, ward, 60, 0, false);
                    }
                }
                com.windanesz.ancientspellcraft.item.ASArtifactEffects.applyAuraEffect(player, player, ward, 60, 1, false);
            }));
    public static final Supplier<Item> CHARM_AURA_WITHER = artifact("charm_aura_wither", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.battlemageAura((player, nearby) -> {
                for (var e : nearby) {
                    if (com.windanesz.ancientspellcraft.item.ASArtifactEffects.isAuraEnemy(player, e)) {
                        com.windanesz.ancientspellcraft.item.ASArtifactEffects.applyAuraEffect(player, e,
                                net.minecraft.world.effect.MobEffects.WITHER, 60, 0, true);
                    }
                }
            }));
    public static final Supplier<Item> CHARM_AURA_DEFENSE = artifact("charm_aura_defense", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.battlemageAura((player, nearby) ->
                    com.windanesz.ancientspellcraft.item.ASArtifactEffects.applyAuraEffect(player, player,
                            com.windanesz.ancientspellcraft.registry.ASEffects.IMPROVED_ARMOR, 60, 3, false)));
    public static final Supplier<Item> CHARM_AURA_VULNERABILITY = artifact("charm_aura_vulnerability", net.minecraft.world.item.Rarity.RARE,
            com.windanesz.ancientspellcraft.item.ASArtifactEffects.battlemageAura((player, nearby) -> {
                for (var e : nearby) {
                    if (com.windanesz.ancientspellcraft.item.ASArtifactEffects.isAuraEnemy(player, e)) {
                        com.windanesz.ancientspellcraft.item.ASArtifactEffects.applyAuraEffect(player, e,
                                com.windanesz.ancientspellcraft.registry.ASEffects.DEGRADED_ARMOR, 60, 4, false);
                    }
                }
            }));
    public static final Supplier<Item> HEAD_CHAOS_MAGIC = artifact("head_chaos_magic", net.minecraft.world.item.Rarity.EPIC,
            new com.koomplo.wizardry.core.IArtifactEffect() {
                @Override
                public void onSpellPreCast(com.koomplo.wizardry.api.content.event.SpellCastEvent.Pre event, net.minecraft.world.item.ItemStack artifact) {
                    // 1.12.2 ASEventHandler: +25% de potência em class spells de WARLOCK
                    if (event.getSpell() instanceof com.windanesz.ancientspellcraft.spell.ClassSpell classSpell
                            && classSpell.armourClass() == com.koomplo.wizardry.content.item.armor.WizardArmorType.WARLOCK) {
                        event.getModifiers().set(com.koomplo.wizardry.api.content.spell.internal.SpellModifiers.POTENCY,
                                1.25f * event.getModifiers().get(com.koomplo.wizardry.api.content.spell.internal.SpellModifiers.POTENCY));
                    }
                }
            });

    // AS-27: gate no SphereCognizanceBlockEntity (relíquia pesquisada sob lua cheia = tipo SPELL)
    public static final Supplier<Item> CHARM_STONE_TABLET = artifact("charm_stone_tablet",
            net.minecraft.world.item.Rarity.EPIC, null);

    // AS-28: gate no ASWarlockEvents.castAbsorbedSpell (metade da fome no cast absorvido)
    public static final Supplier<Item> AMULET_SPELLBINDING = artifact("amulet_spellbinding",
            net.minecraft.world.item.Rarity.RARE, null);

    // AS-32: gema consumida pelo absorb_artefact (+1% potência por gema absorvida, máx 30)
    public static final Supplier<Item> BODY_POWER_GEM = artifact("body_power_gem",
            net.minecraft.world.item.Rarity.EPIC, null);

    // AS-33: artefatos-sistema
    public static final Supplier<Item> CHARM_INFERNAL_STONE = ITEMS.register("charm_infernal_stone",
            () -> new com.windanesz.ancientspellcraft.item.InfernalStoneItem(net.minecraft.world.item.Rarity.EPIC));
    public static final Supplier<Item> CHARM_DIAMOND_GOOSE = ITEMS.register("charm_diamond_goose",
            () -> new com.windanesz.ancientspellcraft.item.DiamondGooseItem(net.minecraft.world.item.Rarity.RARE));
    public static final Supplier<Item> HEAD_MASK_OF_PERSEIGNI = artifact("head_mask_of_perseigni",
            net.minecraft.world.item.Rarity.RARE, com.windanesz.ancientspellcraft.item.ASArtifactEffects.maskOfPerseigni());
    public static final Supplier<Item> RING_ARCANE_FLAMES = artifact("ring_arcane_flames",
            net.minecraft.world.item.Rarity.EPIC, com.windanesz.ancientspellcraft.item.ASArtifactEffects.arcaneFlameRing());
    public static final Supplier<Item> CHARM_DEVORITIUM_MAGNET = ITEMS.register("charm_devoritium_magnet",
            () -> new com.windanesz.ancientspellcraft.item.DevoritiumMagnetItem(net.minecraft.world.item.Rarity.EPIC));
    public static final Supplier<Item> BELT_SCROLL_HOLDER = ITEMS.register("belt_scroll_holder",
            () -> new com.windanesz.ancientspellcraft.item.SocketedArtifactItem(net.minecraft.world.item.Rarity.RARE,
                    stack -> stack.getItem() instanceof com.koomplo.wizardry.content.item.WandUpgradeItem
                            && !stack.is(com.koomplo.wizardry.setup.registries.EBItems.STORAGE_UPGRADE.get())
                            && !stack.is(com.koomplo.wizardry.setup.registries.EBItems.SIPHON_UPGRADE.get())
                            && !stack.is(com.koomplo.wizardry.setup.registries.EBItems.ATTUNEMENT_UPGRADE.get())
                            && !stack.is(com.koomplo.wizardry.setup.registries.EBItems.MELEE_UPGRADE.get()),
                    com.windanesz.ancientspellcraft.item.ASArtifactEffects.scrollHolderCondenser()));
    public static final Supplier<Item> ALCHEMICAL_ESSENCE = ITEMS.register("alchemical_essence",
            com.windanesz.ancientspellcraft.item.AlchemicalEssenceItem::new);

    // AS-33: artefatos diários (1x por dia de Minecraft)
    public static final Supplier<Item> CORNUCOPIA = ITEMS.register("cornucopia",
            () -> new com.windanesz.ancientspellcraft.item.DailyArtifactItem(net.minecraft.world.item.Rarity.RARE, (player, stack) -> {
                var random = player.getRandom();
                float f = random.nextFloat();
                net.minecraft.world.item.ItemStack food;
                if (f <= 0.2f) food = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BREAD, 1 + random.nextInt(3));
                else if (f <= 0.4f) food = new net.minecraft.world.item.ItemStack(random.nextBoolean()
                        ? net.minecraft.world.item.Items.COOKED_MUTTON : net.minecraft.world.item.Items.BAKED_POTATO, 1 + random.nextInt(2));
                else if (f <= 0.5f) food = new net.minecraft.world.item.ItemStack(random.nextBoolean()
                        ? net.minecraft.world.item.Items.PUMPKIN_PIE : net.minecraft.world.item.Items.COOKIE, 1 + random.nextInt(3));
                else if (f <= 0.6f) food = random.nextBoolean()
                        ? new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BEETROOT, 3 + random.nextInt(3))
                        : new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BEETROOT_SOUP);
                else if (f <= 0.7f) food = random.nextBoolean()
                        ? new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.MUSHROOM_STEW)
                        : new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.APPLE, 1 + random.nextInt(3));
                else if (f <= 0.8f) food = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COOKED_CHICKEN, 1 + random.nextInt(2));
                else if (f <= 0.9f) food = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COOKED_COD, 1 + random.nextInt(2));
                else if (f <= 0.95f) food = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COOKED_PORKCHOP, 1 + random.nextInt(2));
                else if (f < 0.97f) food = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.GOLDEN_APPLE);
                else food = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COOKED_BEEF);
                com.windanesz.ancientspellcraft.item.DailyArtifactItem.give(player, food);
            }));
    public static final Supplier<Item> CHARM_BUCKET_COAL = ITEMS.register("charm_bucket_coal",
            () -> new com.windanesz.ancientspellcraft.item.DailyArtifactItem(net.minecraft.world.item.Rarity.RARE, (player, stack) ->
                    com.windanesz.ancientspellcraft.item.DailyArtifactItem.give(player,
                            new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COAL, 5 + player.getRandom().nextInt(6)))));
    public static final Supplier<Item> CHARM_GOLD_BAG = ITEMS.register("charm_gold_bag",
            () -> new com.windanesz.ancientspellcraft.item.DailyArtifactItem(net.minecraft.world.item.Rarity.RARE, (player, stack) ->
                    com.windanesz.ancientspellcraft.item.DailyArtifactItem.give(player,
                            new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.GOLD_NUGGET, 2 + player.getRandom().nextInt(7)))));
    public static final Supplier<Item> CHARM_EVERGROWING_CRYSTAL = ITEMS.register("charm_evergrowing_crystal",
            () -> new com.windanesz.ancientspellcraft.item.DailyArtifactItem(net.minecraft.world.item.Rarity.EPIC, (player, stack) -> {
                float f = player.getRandom().nextFloat();
                net.minecraft.world.item.ItemStack reward;
                if (f <= 0.1f) reward = new net.minecraft.world.item.ItemStack(ASItems.ASTRAL_DIAMOND_SHARD.get(), 1 + player.getRandom().nextInt(3));
                else if (f <= 0.15f) reward = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND);
                else reward = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.EMERALD, 1 + player.getRandom().nextInt(2));
                com.windanesz.ancientspellcraft.item.DailyArtifactItem.give(player, reward);
            }));
    public static final Supplier<Item> CHARM_PHILOSOPHERS_STONE = ITEMS.register("charm_philosophers_stone",
            () -> new com.windanesz.ancientspellcraft.item.DailyArtifactItem(net.minecraft.world.item.Rarity.EPIC, (player, stack) ->
                    com.windanesz.ancientspellcraft.item.DailyArtifactItem.give(player,
                            new net.minecraft.world.item.ItemStack(ASItems.ALCHEMICAL_ESSENCE.get()))));
    public static final Supplier<Item> CHARM_REMNANT_CAGE = ITEMS.register("charm_remnant_cage",
            () -> new com.windanesz.ancientspellcraft.item.RemnantCageItem(net.minecraft.world.item.Rarity.EPIC));

    // AS-26: residuais do scribing — tome de transcrição, everfull flask e a relíquia ancient_mana_flask
    public static final Supplier<Item> CHARM_TRANSCRIBING_TOME = ITEMS.register("charm_transcribing_tome",
            () -> new com.windanesz.ancientspellcraft.item.TranscribingTomeItem(net.minecraft.world.item.Rarity.UNCOMMON));
    public static final Supplier<Item> CHARM_MANA_FLASK = ITEMS.register("charm_mana_flask",
            () -> new com.windanesz.ancientspellcraft.item.EverfullManaFlaskItem(net.minecraft.world.item.Rarity.UNCOMMON));
    public static final Supplier<Item> ANCIENT_MANA_FLASK = ITEMS.register("ancient_mana_flask",
            () -> new com.windanesz.ancientspellcraft.item.RelicItem(
                    com.koomplo.wizardry.setup.registries.SpellTiers.APPRENTICE, net.minecraft.world.item.Rarity.UNCOMMON));

    public static final Supplier<Item> CRYSTAL_SILVER_INGOT = ITEMS.register("crystal_silver_ingot",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CRYSTAL_SILVER_NUGGET = ITEMS.register("crystal_silver_nugget",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> ASTRAL_DIAMOND_SHARD = ITEMS.register("astral_diamond_shard",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> MASTER_BOLT = ITEMS.register("master_bolt",
            () -> new Item(new Item.Properties().stacksTo(1)));

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
        // Blocos de devoritium usam item próprio (anti-magia ao carregar — 1.12.2 ItemBlockDevoritiumMaterial)
        for (String name : new String[]{"devoritium_block", "devoritium_ore", "devoritium_gilded_stone", "devoritium_bars", "devoritium_door"}) {
            ITEMS.register(name, () -> new com.windanesz.ancientspellcraft.item.DevoritiumBlockItem(
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, name)),
                    new Item.Properties()));
        }
        for (String name : new String[]{"ice_crafting_table", "imbuement_altar_ruined", "sphere_cognizance",
                "snow_slab",
                "scribing_desk", "arcane_anvil", "sealed_stone", "unsealed_stone", "sentinel_block", "sentinel_block_diamond", "sage_lectern", "unseal_button",
                "log_crystal_tree", "leaves_crystal_tree", "astral_diamond_ore", "crystal_silver_ore", "sage_flax",
                "dimension_boundary", "dimension_boundary_magic", "dimension_boundary_fire", "dimension_boundary_ice",
                "dimension_boundary_lightning", "dimension_boundary_necromancy", "dimension_boundary_earth",
                "dimension_boundary_sorcery", "dimension_boundary_healing", "dimension_focus", "dimension_focus_gold",
                "skull_watch", "artefact_pensive"}) {
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
                        output.accept(BATTLEMAGE_CONTRACT.get());
                        output.accept(CRYSTAL_SILVER_INGOT.get());
                        output.accept(CRYSTAL_SILVER_NUGGET.get());
                        output.accept(ASTRAL_DIAMOND_SHARD.get());
                        output.accept(MASTER_BOLT.get());
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
                                "crystal_ore_necromancy", "crystal_ore_sorcery", "ice_crafting_table", "imbuement_altar_ruined", "sphere_cognizance",
                                "log_crystal_tree", "leaves_crystal_tree", "astral_diamond_ore", "crystal_silver_ore", "sage_flax",
                                "dimension_boundary", "dimension_boundary_magic", "dimension_boundary_fire", "dimension_boundary_ice",
                                "dimension_boundary_lightning", "dimension_boundary_necromancy", "dimension_boundary_earth",
                                "dimension_boundary_sorcery", "dimension_boundary_healing", "dimension_focus", "dimension_focus_gold",
                                "skull_watch", "artefact_pensive"}) {
                            output.accept(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(com.windanesz.ancientspellcraft.AncientSpellcraft.MODID, name)));
                        }
                    })
                    .build());

    private ASItems() {
    }
}
