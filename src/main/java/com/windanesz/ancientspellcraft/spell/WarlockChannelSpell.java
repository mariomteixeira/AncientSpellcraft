package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/** Base dos absorbs do warlock (1.12.2): canaliza 60t com o insumo no offhand e absorve. */
public abstract class WarlockChannelSpell extends Spell implements ClassSpell {

    protected abstract boolean isValidOffhand(net.minecraft.world.entity.player.Player caster, ItemStack stack);

    protected abstract String invalidMessage();

    /** Efetiva a absorcao no tick 60 (server). @return true se absorveu. */
    protected abstract boolean absorb(PlayerCastContext ctx, ItemStack offhand);

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        ItemStack offhand = ctx.caster().getOffhandItem();
        if (!isValidOffhand(ctx.caster(), offhand)) {
            if (!ctx.world().isClientSide && ctx.castingTicks() == 0) {
                ctx.caster().displayClientMessage(Component.translatable(invalidMessage()), true);
            }
            return false;
        }
        if (ctx.castingTicks() % 40 == 0) this.playSoundLoop(ctx.world(), ctx.caster(), ctx.castingTicks());

        Element element = elementOrMagic(ctx.caster());
        if (ctx.world().isClientSide) {
            int[] colours = WarlockSpellEffects.colours(element);
            var rand = ctx.world().random;
            ParticleBuilder.create(WarlockSpellEffects.particle(element), rand,
                            ctx.caster().getX() + rand.nextDouble() * 0.5 * (rand.nextBoolean() ? 1 : -1), ctx.caster().getY(),
                            ctx.caster().getZ() + rand.nextDouble() * 0.5 * (rand.nextBoolean() ? 1 : -1), 0.03, true)
                    .velocity(0, 0.3, 0).color(colours[rand.nextInt(3)]).time(20 + rand.nextInt(50)).spawn(ctx.world());
        } else if (ctx.castingTicks() == 60) {
            return absorb(ctx, offhand);
        }
        return true;
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.WARLOCK;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.FORBIDDEN_TOME.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
