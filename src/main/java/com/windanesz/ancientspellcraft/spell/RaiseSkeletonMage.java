package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.content.spell.abstr.MinionSpell;
import com.windanesz.ancientspellcraft.entity.living.SkeletonMageEntity;
import com.windanesz.ancientspellcraft.entity.living.SkeletonMageMinion;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;

/**
 * Ergue um esqueleto mago de elemento aleatorio (1.12.2 RaiseSkeletonMage).
 * TODO cristal elemental no offhand forca o elemento (10% quebra) e amulet_elemental_offense - portam com os artefatos.
 */
public class RaiseSkeletonMage extends MinionSpell<SkeletonMageMinion> {

    public RaiseSkeletonMage() {
        super(level -> new SkeletonMageMinion(ASEntities.SKELETON_MAGE_MINION.get(), level));
    }

    @Override
    protected void addMinionExtras(SkeletonMageMinion minion, CastContext ctx, int alreadySpawned) {
        super.addMinionExtras(minion, ctx, alreadySpawned);
        minion.setRare(ctx.world().random.nextFloat() < 0.4f);
        minion.setMageElement(ctx.world().random.nextInt(SkeletonMageEntity.MAGE_ELEMENTS.length));
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
