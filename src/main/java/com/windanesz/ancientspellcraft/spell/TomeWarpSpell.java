package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * Tome Warp (1.12.2 TomeWarp, SAGE): spell "gravável" — não faz nada ao castar; existir nos slots
 * do tomo ANIMADO habilita o sneak-right-click do tome_controller a trocar de lugar com o tomo.
 */
public class TomeWarpSpell extends Spell implements ClassSpell {

    @Override
    public boolean cast(PlayerCastContext ctx) {
        return false;
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.SAGE;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.MYSTIC_SPELL_BOOK.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
