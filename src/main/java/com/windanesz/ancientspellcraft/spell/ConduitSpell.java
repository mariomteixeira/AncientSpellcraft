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
 * secundária (quantidade/tick * eficiência das properties). Desvios documentados:
 * ring_mana_transfer e charm_progression_orb (progressão) ficam com o lote de artefatos.
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
        if (!(source.getItem() instanceof IManaItem sourceMana) || !(target.getItem() instanceof IManaItem targetMana)) {
            return false;
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

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
