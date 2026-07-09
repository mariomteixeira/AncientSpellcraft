package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.item.WarlockOrbItem;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.world.PocketDimension;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * Orb Space (1.12.2 OrbSpace): canaliza 3s segurando um WARLOCK ORB e entra no espaco de bolso
 * (dimensao propria com a biblioteca de bolso por jogador); castar la dentro volta ao ponto de
 * partida. Desvio documentado: plot por jogador em grade por UUID (sem o sistema de orbe fisico
 * 1:1 do original).
 */
public class OrbSpace extends Spell implements ClassSpell {

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        boolean holdingOrb = ctx.caster().getMainHandItem().getItem() instanceof WarlockOrbItem
                || ctx.caster().getOffhandItem().getItem() instanceof WarlockOrbItem;
        boolean inside = PocketDimension.isInside(ctx.caster());
        if (!holdingOrb && !inside) {
            if (!ctx.world().isClientSide && ctx.castingTicks() == 0) {
                ctx.caster().displayClientMessage(Component.translatable("spell.ancientspellcraft.orb_space.no_orb"), true);
            }
            return false;
        }
        if (ctx.world().isClientSide) {
            ParticleBuilder.create(EBParticles.FLASH, ctx.caster()).scale(0.8f).color(0x9d2cf3)
                    .pos(0, 0.2 + ctx.world().random.nextDouble() * 1.5, 0).time(20).spawn(ctx.world());
            return true;
        }
        if (ctx.castingTicks() != 60 || !(ctx.caster() instanceof ServerPlayer player)) {
            return true;
        }
        if (inside) {
            PocketDimension.teleportBack(player);
        } else {
            PocketDimension.teleportIn(player);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
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
