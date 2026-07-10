package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.windanesz.ancientspellcraft.misc.ASCameraState;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * Farsight (1.12.2 Farsight): contínua; enquanto canalizada aplica zoom de FOV no client
 * (ASCameraClientHandler.onComputeFovModifier), que desliga sozinho quando o cast para.
 */
public class FarsightSpell extends Spell {

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.world().isClientSide) {
            ASCameraState.farsightActive = true;
        }
        return true;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
