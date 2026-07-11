package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.spell.SpellTier;
import com.koomplo.wizardry.content.item.WandItem;

/** Orbe do warlock (1.12.2 ItemWarlockOrb): a arma de cast da classe, uma wand em forma de orbe.
 * Apurado no AS-28: o 1.12.2 não restringia o orb a warlocks e o consumo do absorvido era por
 * keybind (H), não pelo orb — ambos já cobertos pelo sistema do warlock. */
public class WarlockOrbItem extends WandItem {

    public WarlockOrbItem(SpellTier tier, Element element) {
        super(tier, element);
    }
}
