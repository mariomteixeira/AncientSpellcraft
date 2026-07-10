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

/** Colhe plantacoes maduras em area (1.12.2 Harvest). charm_seed_bag replanta consumindo 1 semente do inventario. */
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
        boolean seedBag = com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(ctx.caster(), ASItems.CHARM_SEED_BAG.get());
        var replant = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.world.level.block.state.BlockState>();
        var seeds = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.world.item.ItemStack>();
        if (!ctx.world().isClientSide) {
            for (var pos : com.koomplo.wizardry.api.content.util.BlockUtil.getBlockSphere(ctx.caster().blockPosition(), radius)) {
                var state = ctx.world().getBlockState(pos);
                boolean mature;
                if (state.getBlock() instanceof net.minecraft.world.level.block.CropBlock crop && crop.isMaxAge(state)) {
                    mature = true;
                } else if (state.is(net.minecraft.world.level.block.Blocks.PUMPKIN) || state.is(net.minecraft.world.level.block.Blocks.MELON)
                        || state.is(net.minecraft.world.level.block.Blocks.SUGAR_CANE) || state.is(net.minecraft.world.level.block.Blocks.CACTUS)
                        || state.getBlock() instanceof net.minecraft.world.level.block.NetherWartBlock
                        && state.getValue(net.minecraft.world.level.block.NetherWartBlock.AGE) >= 3) {
                    mature = true;
                } else if (state.getBlock() instanceof net.minecraft.world.level.block.BonemealableBlock growable
                        && !(state.getBlock() instanceof net.minecraft.world.level.block.GrassBlock)
                        && !state.is(net.minecraft.tags.BlockTags.SAPLINGS)
                        && !growable.isValidBonemealTarget(ctx.world(), pos, state)) {
                    // 1.12.2: IGrowable que nao pode mais crescer = maduro (cacau, sweet berries etc)
                    mature = true;
                } else {
                    mature = false;
                }
                if (mature) {
                    if (seedBag) {
                        seeds.put(pos, state.getBlock().getCloneItemStack(ctx.world(), pos, state));
                        replant.put(pos, state.getBlock().defaultBlockState());
                    }
                    ctx.world().destroyBlock(pos, true, ctx.caster());
                    harvested = true;
                }
            }
            // charm_seed_bag: replanta gastando 1 "semente" (o pick-block do bloco colhido) do inventário
            for (var entry : replant.entrySet()) {
                var seed = seeds.get(entry.getKey());
                if (seed == null || seed.isEmpty()) continue;
                var inventory = ctx.caster().getInventory();
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    if (net.minecraft.world.item.ItemStack.isSameItem(inventory.getItem(i), seed)) {
                        inventory.removeItem(i, 1);
                        ctx.world().setBlockAndUpdate(entry.getKey(), entry.getValue());
                        break;
                    }
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
