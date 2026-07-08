package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;

/** Ray de classe SAGE (mystic book). */
public abstract class SageRaySpell extends ASRaySpell implements ClassSpell {

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.SAGE;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.MYSTIC_SPELL_BOOK.get();
    }
}
