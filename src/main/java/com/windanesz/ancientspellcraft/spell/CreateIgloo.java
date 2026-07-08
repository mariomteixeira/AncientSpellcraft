package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.core.AllyDesignation;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.block.TemporaryBlockEntity;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Constroi um iglu de neve conjurada em volta do caster (1.12.2 CreateIgloo; formato aproximado por hemisferio oco - lista manual do original nao replicada 1:1). */
public class CreateIgloo extends Spell {

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!ctx.caster().onGround()) return false;
        BlockPos base = ctx.caster().blockPosition();
        if (!ctx.world().isClientSide) {
            List<BlockPos> shell = new ArrayList<>(BlockUtil.getBlockSphere(base.above(), 3));
            shell.removeAll(BlockUtil.getBlockSphere(base.above(), 2));
            var door = ctx.caster().getDirection();
            for (BlockPos pos : shell) {
                if (pos.getY() < base.getY()) continue;
                // entrada na direcao do olhar
                if (pos.getY() <= base.getY() + 1 && pos.subtract(base).getX() * door.getStepX()
                        + pos.subtract(base).getZ() * door.getStepZ() > 1) continue;
                TemporaryBlockEntity.place(ctx.caster(), ctx.world(), ASBlocks.CONJURED_SNOW.get(), pos, 1200);
            }
        } else {
            for (int i = 0; i < 20; i++) {
                ParticleBuilder.create(EBParticles.SNOW, ctx.caster()).spawn(ctx.world());
            }
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }
}
