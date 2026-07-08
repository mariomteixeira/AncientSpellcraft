package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.content.spell.abstr.ConstructRangedSpell;
import com.windanesz.ancientspellcraft.entity.construct.ChaosFieldConstruct;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;

/** Campo do caos (1.12.2 ChaosField). */
public class ChaosFieldSpell extends ConstructRangedSpell<ChaosFieldConstruct> implements ClassSpell {

    public ChaosFieldSpell() {
        super(level -> new ChaosFieldConstruct(ASEntities.CHAOS_FIELD.get(), level), false);
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.WARLOCK;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.FORBIDDEN_TOME.get();
    }
}
