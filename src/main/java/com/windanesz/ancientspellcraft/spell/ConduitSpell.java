package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.item.IManaItem;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Conduit (1.12.2 Conduit): cast contínuo transfere mana da wand da mão principal para a da
 * secundária (quantidade/tick * eficiência das properties). charm_progression_orb transfere
 * PROGRESSÃO (10/tick) em vez de mana; ring_mana_transfer transfere para a wand do JOGADOR
 * mirado (raio 10) quando a offhand não tem wand.
 */
public class ConduitSpell extends Spell {

    private static final SpellProperty<Float> TRANSFER_EFFICIENCY = SpellProperty.floatProperty("transfer_efficiency", 0.9f);
    private static final SpellProperty<Integer> TRANSFER_AMOUNT_PER_TICK = SpellProperty.intProperty("transfer_amount_per_tick", 2);

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player player = ctx.caster();
        ItemStack source = player.getMainHandItem();
        ItemStack target = player.getOffhandItem();
        if (!(source.getItem() instanceof IManaItem sourceMana)) return false;

        if (!(target.getItem() instanceof IManaItem)) {
            // ring_mana_transfer (1.12.2): sem wand na offhand, transfere para a wand do jogador mirado
            if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player, ASItems.RING_MANA_TRANSFER.get())) {
                return transferToLookTarget(ctx, source, sourceMana);
            }
            return false;
        }
        IManaItem targetMana = (IManaItem) target.getItem();

        // charm_progression_orb (1.12.2): transfere progressão (10/tick) em vez de mana
        if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player, ASItems.CHARM_PROGRESSION_ORB.get())) {
            if (!ctx.world().isClientSide) {
                int sourceProgression = com.koomplo.wizardry.api.content.util.CastItemDataHelper.getProgression(source);
                if (sourceProgression <= 0) return false;
                int amount = Math.min(10, sourceProgression);
                com.koomplo.wizardry.api.content.util.CastItemDataHelper.addProgression(source, -amount);
                com.koomplo.wizardry.api.content.util.CastItemDataHelper.addProgression(target, amount);
            } else {
                ParticleBuilder.create(EBParticles.FLASH, player).scale(0.7f).color(0xf5d442)
                        .pos(0, 1.2 + ctx.world().random.nextDouble() * 0.4, 0).time(12).spawn(ctx.world());
            }
            this.playSound(ctx.world(), player, ctx.castingTicks(), -1);
            return true;
        }

        if (sourceMana.getMana(source) <= 0 || targetMana.isManaFull(target)) return false;

        if (!ctx.world().isClientSide) {
            int amount = Math.min(this.property(TRANSFER_AMOUNT_PER_TICK), sourceMana.getMana(source));
            sourceMana.consumeMana(source, amount, player);
            targetMana.rechargeMana(target, Math.max(1, (int) (amount * this.property(TRANSFER_EFFICIENCY))));
        } else {
            ParticleBuilder.create(EBParticles.SPARKLE, player).scale(0.7f).color(0x56e8e3)
                    .pos(0, 1.2 + ctx.world().random.nextDouble() * 0.4, 0).time(12).spawn(ctx.world());
        }
        this.playSound(ctx.world(), player, ctx.castingTicks(), -1);
        return true;
    }

    /** Ray de 10 blocos: transfere mana para o cast item da mainhand do jogador atingido. */
    private boolean transferToLookTarget(PlayerCastContext ctx, ItemStack source, IManaItem sourceMana) {
        Player player = ctx.caster();
        if (sourceMana.getMana(source) <= 0) return false;
        var origin = player.getEyePosition();
        var look = player.getLookAngle();
        var end = origin.add(look.scale(10));
        var hit = net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(ctx.world(), player,
                origin, end, player.getBoundingBox().expandTowards(look.scale(10)).inflate(1.0),
                e -> e instanceof Player && e != player);
        if (hit == null || !(hit.getEntity() instanceof Player targetPlayer)
                || !(targetPlayer.getMainHandItem().getItem() instanceof IManaItem targetMana)) {
            return false;
        }
        ItemStack targetStack = targetPlayer.getMainHandItem();
        if (targetMana.isManaFull(targetStack)) return false;
        if (!ctx.world().isClientSide) {
            int amount = Math.min(this.property(TRANSFER_AMOUNT_PER_TICK), sourceMana.getMana(source));
            sourceMana.consumeMana(source, amount, player);
            targetMana.rechargeMana(targetStack, Math.max(1, (int) (amount * this.property(TRANSFER_EFFICIENCY))));
        } else {
            ParticleBuilder.create(EBParticles.SPARKLE, player).scale(0.7f).color(0x56e8e3)
                    .pos(0, 1.2 + ctx.world().random.nextDouble() * 0.4, 0).time(12).spawn(ctx.world());
        }
        this.playSound(ctx.world(), player, ctx.castingTicks(), -1);
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
