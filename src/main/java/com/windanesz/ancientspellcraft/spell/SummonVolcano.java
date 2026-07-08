package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.content.spell.abstr.MinionSpell;
import com.windanesz.ancientspellcraft.entity.living.VolcanoEntity;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;

/** Invoca um vulcao que cospe formigas de fogo (1.12.2 SummonVolcano). */
public class SummonVolcano extends MinionSpell<VolcanoEntity> {

    public SummonVolcano() {
        super(level -> new VolcanoEntity(ASEntities.VOLCANO.get(), level));
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
