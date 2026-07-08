package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.spell.SpellTier;
import com.koomplo.wizardry.content.item.WandItem;

/** Orbe do warlock (1.12.2 ItemWarlockOrb): a arma de cast da classe, uma wand em forma de orbe.
 * TODO: consumo do conteudo absorvido (spell/pocao/elemento) e restricao de uso a warlocks. */
public class WarlockOrbItem extends WandItem {

    public WarlockOrbItem(SpellTier tier, Element element) {
        super(tier, element);
    }
}
