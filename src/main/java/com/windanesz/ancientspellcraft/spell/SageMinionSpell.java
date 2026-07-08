package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.content.spell.abstr.MinionSpell;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.function.Function;

/** MinionSpell de classe SAGE vinculada ao mystic book. */
public class SageMinionSpell<T extends Mob> extends MinionSpell<T> implements ClassSpell {

    public SageMinionSpell(Function<Level, T> factory) {
        super(factory);
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.SAGE;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.MYSTIC_SPELL_BOOK.get();
    }
}
