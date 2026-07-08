package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.content.spell.abstr.MinionSpell;
import com.koomplo.wizardry.setup.registries.EBAttachments;
import com.windanesz.ancientspellcraft.entity.living.SkeletonMageEntity;
import com.windanesz.ancientspellcraft.entity.living.SkeletonMageMinion;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;

import java.util.List;

/** A alianca profana: um esqueleto mago de CADA elemento em circulo (1.12.2 UnholyAlliance). */
public class UnholyAlliance extends MinionSpell<SkeletonMageMinion> {

    public UnholyAlliance() {
        super(level -> new SkeletonMageMinion(ASEntities.SKELETON_MAGE_MINION.get(), level));
    }

    @Override
    protected boolean spawnMinions(CastContext ctx) {
        if (ctx.world().isClientSide) return true;
        BlockPos base = ctx.caster().blockPosition();
        List<BlockPos> locations = List.of(
                base.relative(Direction.NORTH, 2), base.relative(Direction.SOUTH, 2),
                base.relative(Direction.WEST, 2), base.relative(Direction.EAST, 2),
                base.relative(Direction.NORTH, 2).relative(Direction.EAST, 2),
                base.relative(Direction.NORTH, 2).relative(Direction.WEST, 2),
                base.relative(Direction.SOUTH, 2).relative(Direction.WEST, 2));

        int range = property(DefaultProperties.SUMMON_RADIUS);
        for (int i = 0; i < SkeletonMageEntity.MAGE_ELEMENTS.length; i++) {
            BlockPos pos;
            if (i < locations.size() && ctx.world().isEmptyBlock(locations.get(i)) && ctx.world().isEmptyBlock(locations.get(i).above())) {
                pos = locations.get(i);
            } else {
                pos = BlockUtil.findNearbyFloorSpace(ctx.world(), base, range, range * 2, false, null);
            }
            if (pos == null) return i > 0;

            SkeletonMageMinion minion = new SkeletonMageMinion(ASEntities.SKELETON_MAGE_MINION.get(), ctx.world());
            minion.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            minion.setRare(ctx.world().random.nextFloat() < 0.4f);
            minion.setMageElement(i);
            var data = minion.getData(EBAttachments.MINION_DATA);
            data.setSummoned(true);
            if (ctx.caster() != null) data.setOwner(ctx.caster());
            data.setLifetime((int) (property(DefaultProperties.MINION_LIFETIME) * ctx.modifiers().get(SpellModifiers.DURATION)));
            data.markGoalRestart(true);
            ctx.world().addFreshEntity(minion);
        }
        return true;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
