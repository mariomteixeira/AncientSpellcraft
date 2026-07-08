package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.content.item.armor.WizardArmorType;

/** Spell de classe (1.12.2 IClassSpell): so casta com o set completo da armadura correspondente. */
public interface ClassSpell {

    WizardArmorType armourClass();

    /** Elemento do set completo da classe, ou MAGIC (1.12.2 getElementOrMagicElement). TODO head_chaos_magic. */
    default com.koomplo.wizardry.api.content.spell.Element elementOrMagic(net.minecraft.world.entity.LivingEntity caster) {
        if (caster.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).getItem()
                instanceof com.koomplo.wizardry.content.item.armor.WizardArmorItem armor
                && armor.getWizardArmorType() == armourClass()
                && caster instanceof net.minecraft.world.entity.player.Player player
                && com.windanesz.ancientspellcraft.handler.ASSpellEvents.isWearingFullSet(player, armourClass())
                && armor.getElement() != null) {
            return armor.getElement();
        }
        return com.koomplo.wizardry.setup.registries.Elements.MAGIC;
    }
}
