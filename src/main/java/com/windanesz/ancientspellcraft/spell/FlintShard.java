package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.content.spell.abstr.ArrowSpell;
import com.windanesz.ancientspellcraft.entity.projectile.FlintEntity;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;

/** Dispara um estilhaco de silex; em cima de cascalho custa 80% menos (1.12.2 FlintShard). */
public class FlintShard extends ArrowSpell<FlintEntity> {

    public FlintShard() {
        super(level -> new FlintEntity(ASEntities.FLINT_SHARD.get(), level));
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.caster().onGround() && ctx.world().getBlockState(ctx.caster().blockPosition().below()).is(Blocks.GRAVEL)) {
            float cost = ctx.modifiers().get(SpellModifiers.COST);
            ctx.modifiers().set(SpellModifiers.COST, Math.max(cost * 0.2f, 0.1f));
        }
        return super.cast(ctx);
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
