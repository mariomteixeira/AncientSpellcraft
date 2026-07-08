package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.windanesz.ancientspellcraft.block.MagicMushroomBlock;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/** Anel de fadas: circulo de cogumelos magicos aleatorios em volta do caster (1.12.2 FairyRing). */
public class FairyRing extends Spell {

    public static final SpellProperty<Integer> BLOCK_LIFETIME = SpellProperty.intProperty("block_lifetime");

    @Override
    public boolean cast(PlayerCastContext ctx) {
        BlockPos origin = ctx.caster().blockPosition();
        if (!ctx.world().isClientSide) {
            boolean placed = false;
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    int dist = Math.max(Math.abs(dx), Math.abs(dz));
                    if (dist < 1 || dist > 2) continue;
                    boolean combat = ctx.world().random.nextBoolean();
                    placed |= MagicMushroomBlock.tryPlace(ctx.world(), origin.offset(dx, 0, dz), ctx.caster(),
                            MagicMushroomBlock.randomMushroom(ctx.world(), combat), property(BLOCK_LIFETIME),
                            ctx.modifiers().get(SpellModifiers.POTENCY));
                }
            }
            if (!placed) return false;
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
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
