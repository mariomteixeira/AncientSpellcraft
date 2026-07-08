package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.content.spell.abstr.MinionSpell;
import com.koomplo.wizardry.setup.registries.EBAttachments;
import com.windanesz.ancientspellcraft.entity.living.SpiritBearEntity;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;

/** Invoca o urso espiritual, companheiro permanente (1.12.2 SummonSpiritBear). */
public class SummonSpiritBear extends MinionSpell<SpiritBearEntity> {

    public SummonSpiritBear() {
        super(level -> new SpiritBearEntity(ASEntities.SPIRIT_BEAR.get(), level));
    }

    @Override
    protected void addMinionExtras(SpiritBearEntity minion, CastContext ctx, int alreadySpawned) {
        super.addMinionExtras(minion, ctx, alreadySpawned);
        minion.getData(EBAttachments.MINION_DATA).setLifetime(-1); // companheiro permanente
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
