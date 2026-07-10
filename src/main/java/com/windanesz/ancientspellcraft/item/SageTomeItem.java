package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.spell.SpellTier;
import com.koomplo.wizardry.content.item.WandItem;
import com.koomplo.wizardry.setup.registries.Elements;

/**
 * Tomo do sage (1.12.2 ItemSageTome): a "wand" da classe SAGE — guarda spells, mana e upgrades
 * como qualquer cast item do Redux. Progride de tier no Sage Lectern com enchanted pages.
 * Desvio (igual às espadas battlemage): 4 tiers sem variante elemental (o 1.12.2 tinha 4x9).
 */
public class SageTomeItem extends WandItem {

    public SageTomeItem(SpellTier tier) {
        super(tier, Elements.MAGIC);
    }
}
