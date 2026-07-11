package com.windanesz.ancientspellcraft;

import com.koomplo.wizardry.core.config.ConfigProvider;
import com.koomplo.wizardry.core.config.option.ConfigOption;
import com.koomplo.wizardry.core.config.option.ListConfigOption;
import com.koomplo.wizardry.core.config.option.NumberConfigOption;
import com.koomplo.wizardry.core.config.util.ConfigType;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Config de servidor do AS via infra do Redux (defaults do Settings 1.12.2).
 * empowerment_upgrade_potency_gain fica de fora até os upgrades da espada do battlemage existirem.
 */
public class ASServerConfig implements ConfigProvider {
    private static final ArrayList<ConfigOption<?>> OPTIONS = new ArrayList<>();
    public static final ASServerConfig INSTANCE = new ASServerConfig();

    public static final ConfigOption<Double> SPHERE_SPELL_IDENTIFICATION_CHANCE =
            addOption(NumberConfigOption.doublePrecision("sphere_spell_identification_chance", 0.05D, 0.0D, 1.0D));
    public static final ConfigOption<Integer> RUNIC_SHIELD_DURABILITY =
            addOption(NumberConfigOption.integer("runic_shield_durability", 1000, 1, Integer.MAX_VALUE));
    public static final ConfigOption<Integer> DIAMOND_GOOSE_DETECTION_RANGE =
            addOption(NumberConfigOption.integer("diamond_goose_detection_range", 8, 1, 64));
    public static final ConfigOption<Integer> GEM_OF_POWER_MAX_ABSORB_AMOUNT =
            addOption(NumberConfigOption.integer("gem_of_power_max_absorb_amount", 30, 0, 1000));
    public static final ConfigOption<Integer> METAMAGIC_WAND_COOLDOWN =
            addOption(NumberConfigOption.integer("metamagic_wand_cooldown", 1200, 0, Integer.MAX_VALUE));
    public static final ConfigOption<Integer> SPELLBLADE_CHARGE_GAIN_PER_SPELLCAST =
            addOption(NumberConfigOption.integer("spellblade_charge_gain_per_spellcast", 20, 0, 100));
    public static final ConfigOption<Integer> SPELLBLADE_CHARGE_GAIN_PER_HIT =
            addOption(NumberConfigOption.integer("spellblade_charge_gain_per_hit", 5, 0, 100));

    public static final ConfigOption<List<ResourceLocation>> ABSORB_ARTEFACT_BLACKLIST =
            addOption(ListConfigOption.resourceLocation("absorb_artefact_blacklist", List.of(
                    ResourceLocation.parse("ancientspellcraft:charm_philosophers_stone"),
                    ResourceLocation.parse("ancientspellcraft:cornucopia"),
                    ResourceLocation.parse("ancientspellcraft:charm_bucket_coal"),
                    ResourceLocation.parse("ancientspellcraft:charm_evergrowing_crystal"),
                    ResourceLocation.parse("ancientspellcraft:charm_gold_bag"))));

    public static final ConfigOption<List<ResourceLocation>> IMMOBILITY_CONTINGENCY_EFFECTS =
            addOption(ListConfigOption.resourceLocation("immobility_contingency_effects", List.of(
                    ResourceLocation.parse("ebwizardry:paralysis"),
                    ResourceLocation.parse("ebwizardry:containment"),
                    ResourceLocation.parse("ebwizardry:slow_time"),
                    ResourceLocation.parse("ebwizardry:frost"),
                    ResourceLocation.parse("minecraft:slowness"))));

    public static final ConfigOption<List<ResourceLocation>> METAMAGIC_PROJECTILE_INCOMPATIBLE_SPELLS =
            addOption(ListConfigOption.resourceLocation("metamagic_projectile_incompatible_spells", List.of(
                    ResourceLocation.parse("ancientspellcraft:animate_item"),
                    ResourceLocation.parse("ancientspellcraft:animate_weapon"),
                    ResourceLocation.parse("ancientspellcraft:aquatic_agility"),
                    ResourceLocation.parse("ancientspellcraft:arcane_aegis"),
                    ResourceLocation.parse("ancientspellcraft:bubble_head"),
                    ResourceLocation.parse("ancientspellcraft:conjure_ink"),
                    ResourceLocation.parse("ancientspellcraft:eagle_eye"),
                    ResourceLocation.parse("ancientspellcraft:experiment"),
                    ResourceLocation.parse("ancientspellcraft:farsight"),
                    ResourceLocation.parse("ancientspellcraft:ice_tower"),
                    ResourceLocation.parse("ancientspellcraft:locating"),
                    ResourceLocation.parse("ancientspellcraft:magic_sparks"),
                    ResourceLocation.parse("ancientspellcraft:projectile_ward"),
                    ResourceLocation.parse("ancientspellcraft:resist_fire"))));

    /** Pares "efeito|contraparte" — o alter_potion troca cada efeito da poção pelo mapeado (bidirecional). */
    public static final ConfigOption<List<String>> ALTER_POTION_MAPPING =
            addOption(ListConfigOption.of("alter_potion_mapping", List.of(
                    "minecraft:speed|minecraft:slowness",
                    "minecraft:regeneration|minecraft:poison",
                    "minecraft:strength|minecraft:weakness",
                    "minecraft:haste|minecraft:mining_fatigue",
                    "minecraft:instant_health|minecraft:instant_damage",
                    "minecraft:luck|minecraft:unluck",
                    "minecraft:invisibility|minecraft:glowing",
                    "minecraft:jump_boost|minecraft:levitation",
                    "minecraft:night_vision|minecraft:blindness",
                    "ebwizardry:empowerment|ancientspellcraft:magical_exhaustion"), Codec.STRING));

    private static <T> ConfigOption<T> addOption(ConfigOption<T> option) {
        OPTIONS.add(option);
        return option;
    }

    @Override
    public String getModid() {
        return AncientSpellcraft.MODID;
    }

    @Override
    public ConfigType getType() {
        return ConfigType.SERVER;
    }

    @Override
    public Collection<ConfigOption<?>> build() {
        return OPTIONS;
    }
}
