package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * Horse Whistle (1.12.2 HorseWhistle): assobia pelo teu cavalo — o último chamado (UUID no
 * attachment) ou o cavalo domado mais próximo num raio de 100; longe demais (>20) ele teleporta
 * para perto, senão vem galopando. Desvio documentado: buff de velocidade montado (belt_horse)
 * fica com o lote de artefatos.
 */
public class HorseWhistleSpell extends Spell {

    private static final String LAST_HORSE_TAG = "LastHorseUuid";

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!(ctx.world() instanceof ServerLevel serverLevel) || !(ctx.caster() instanceof Player caster)) {
            return ctx.world().isClientSide;
        }
        this.playSound(serverLevel, caster, ctx.castingTicks(), -1);

        // belt_horse (1.12.2): assobiar montado dá SPEED por 30s ao cavalo
        if (caster.getVehicle() instanceof AbstractHorse ridden
                && com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(caster, ASItems.BELT_HORSE.get())) {
            ridden.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 600, 0));
            return true;
        }

        var tag = caster.getData(ASAttachments.PLAYER_DATA);
        AbstractHorse horse = null;
        if (tag.hasUUID(LAST_HORSE_TAG)
                && serverLevel.getEntity(tag.getUUID(LAST_HORSE_TAG)) instanceof AbstractHorse last && last.isAlive()) {
            horse = last;
        } else {
            horse = serverLevel.getEntitiesOfClass(AbstractHorse.class, new AABB(caster.blockPosition()).inflate(100),
                            h -> !h.isVehicle() && caster.getUUID().equals(h.getOwnerUUID())).stream()
                    .min(Comparator.comparingDouble(h -> h.distanceToSqr(caster)))
                    .orElse(null);
        }
        if (horse == null || horse.isVehicle()) return false;

        if (horse.distanceTo(caster) > 20) {
            horse.teleportTo(caster.getX() + 2, caster.getY(), caster.getZ());
        }
        horse.getNavigation().stop();
        var followRange = horse.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRange != null) followRange.setBaseValue(100.0);
        horse.getNavigation().moveTo(caster.getX(), caster.getY(), caster.getZ(), 1.7);

        tag.putUUID(LAST_HORSE_TAG, horse.getUUID());
        caster.setData(ASAttachments.PLAYER_DATA, tag);
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
