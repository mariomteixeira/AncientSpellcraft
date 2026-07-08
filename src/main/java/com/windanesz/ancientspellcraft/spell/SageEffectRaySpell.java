package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.function.Supplier;

/** EffectRaySpell de classe SAGE (mystic book). */
public class SageEffectRaySpell extends EffectRaySpell implements ClassSpell {

    public SageEffectRaySpell(boolean permanent, boolean useStrength,
                              List<Supplier<Holder<MobEffect>>> effects, List<ParticleSpec> particles) {
        super(permanent, useStrength, effects, particles);
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
