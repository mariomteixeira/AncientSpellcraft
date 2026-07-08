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

/** Casca de magma conjurado em volta do caster (1.12.2 MagmaShell; colocada de uma vez, sem o EntityBuilder gradual). */
public class MagmaShell extends Spell {

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }

    /** Casca oca (esfera grande menos a de raio-1) em volta do caster (getShellBlocks do 1.12.2). */
    protected static List<BlockPos> shellBlocks(PlayerCastContext ctx, int baseRadius) {
        float blast = ctx.modifiers().get(SpellModifiers.BLAST);
        List<BlockPos> large = BlockUtil.getBlockSphere(ctx.caster().blockPosition().above(), baseRadius * blast);
        List<BlockPos> small = BlockUtil.getBlockSphere(ctx.caster().blockPosition().above(), (baseRadius - 1) * blast);
        List<BlockPos> hollow = new ArrayList<>(large);
        hollow.removeAll(small);
        hollow.removeIf(pos -> !ctx.world().getBlockState(pos).canBeReplaced());
        hollow.sort(Comparator.comparingInt(BlockPos::getY));
        return hollow;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!ctx.caster().onGround()) return false;
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        if (!ctx.world().isClientSide) {
            for (LivingEntity target : EntityUtil.getLivingWithinRadius(4, ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.world())) {
                if (AllyDesignation.isValidTarget(ctx.caster(), target)
                        && !MagicDamageSource.isEntityImmune(EBDamageSources.FIRE, target)) {
                    EntityUtil.applyStandardKnockback(ctx.caster(), target, 1f);
                }
            }
            int lifetime = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
            for (BlockPos pos : shellBlocks(ctx, property(DefaultProperties.BLAST_RADIUS).intValue())) {
                TemporaryBlockEntity.place(ctx.caster(), ctx.world(), ASBlocks.CONJURED_MAGMA.get(), pos, lifetime);
            }
        }
        return true;
    }
}
