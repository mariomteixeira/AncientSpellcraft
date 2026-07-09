package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.core.AllyDesignation;
import com.koomplo.wizardry.setup.registries.EBAttachments;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/** Colhe plantacoes maduras em area (1.12.2 Harvest). TODO charm_seed_bag replanta (artefatos). */
public class Harvest extends Spell {

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
        boolean harvested = false;
        float radius = property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST);
        if (!ctx.world().isClientSide) {
            for (var pos : com.koomplo.wizardry.api.content.util.BlockUtil.getBlockSphere(ctx.caster().blockPosition(), radius)) {
                var state = ctx.world().getBlockState(pos);
                if (state.getBlock() instanceof net.minecraft.world.level.block.CropBlock crop && crop.isMaxAge(state)) {
                    ctx.world().destroyBlock(pos, true, ctx.caster());
                    harvested = true;
                } else if (state.is(net.minecraft.world.level.block.Blocks.PUMPKIN) || state.is(net.minecraft.world.level.block.Blocks.MELON)
                        || state.is(net.minecraft.world.level.block.Blocks.SUGAR_CANE) || state.is(net.minecraft.world.level.block.Blocks.CACTUS)
                        || state.getBlock() instanceof net.minecraft.world.level.block.NetherWartBlock
                        && state.getValue(net.minecraft.world.level.block.NetherWartBlock.AGE) >= 3) {
                    ctx.world().destroyBlock(pos, true, ctx.caster());
                    harvested = true;
                } else if (state.getBlock() instanceof net.minecraft.world.level.block.BonemealableBlock growable
                        && !(state.getBlock() instanceof net.minecraft.world.level.block.GrassBlock)
                        && !state.is(net.minecraft.tags.BlockTags.SAPLINGS)
                        && !growable.isValidBonemealTarget(ctx.world(), pos, state)) {
                    // 1.12.2: IGrowable que nao pode mais crescer = maduro (cacau, sweet berries etc)
                    ctx.world().destroyBlock(pos, true, ctx.caster());
                    harvested = true;
                }
            }
        }
        if (harvested) this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return harvested || ctx.world().isClientSide;
    }

    @Override
    public boolean requiresPacket() {
        return false;
    }
}
