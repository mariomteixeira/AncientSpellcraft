package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Phase Jump (1.12.2 PhaseJump, SAGE): canaliza e ao SOLTAR teleporta uma distância aleatória
 * entre min e max (x blast) + extra por segundo canalizado, via Banish do Redux. Mesmo padrão
 * de release do spring_charge: o lançamento sai do tracker em ASSpellEvents.
 */
public class PhaseJumpSpell extends Spell implements ClassSpell {

    public static final SpellProperty<Float> MINIMUM_TELEPORT_DISTANCE = SpellProperty.floatProperty("minimum_teleport_distance", 5f);
    public static final SpellProperty<Float> MAXIMUM_TELEPORT_DISTANCE = SpellProperty.floatProperty("maximum_teleport_distance", 12f);
    public static final SpellProperty<Float> EXTRA_DISTANCE_PER_SECOND = SpellProperty.floatProperty("extra_distance_per_second", 2f);

    /** ticks canalizados + modificador de blast, por player (transiente, server). */
    public static final Map<UUID, float[]> CHANNELING = new ConcurrentHashMap<>();

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (ctx.world().isClientSide) {
            ParticleBuilder.create(EBParticles.DUST)
                    .pos(caster.getX() + (ctx.world().random.nextDouble() - 0.5),
                            caster.getY() + ctx.world().random.nextDouble() * 2,
                            caster.getZ() + (ctx.world().random.nextDouble() - 0.5))
                    .time(10).spawn(ctx.world());
        } else {
            CHANNELING.put(caster.getUUID(),
                    new float[]{ctx.castingTicks(), ctx.modifiers().get(SpellModifiers.BLAST)});
        }
        return true;
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.SAGE;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.MYSTIC_SPELL_BOOK.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
