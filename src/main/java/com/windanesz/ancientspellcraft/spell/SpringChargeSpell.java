package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring Charge (1.12.2 SpringCharge): carrega agachado no chão (até 40t) e ao SOLTAR salta —
 * altura = vertical_speed * potência * ticks; impulso horizontal na direção do olhar. O Redux não
 * tem hook de "soltou o cast": o lançamento sai do tracker em ASSpellEvents (parou de castar com
 * carga acumulada). Desvio documentado: ring_cloudwalker (carga no ar) fica com artefatos.
 */
public class SpringChargeSpell extends Spell {

    public static final SpellProperty<Float> HORIZONTAL_SPEED = SpellProperty.floatProperty("horizontal_speed", 0.02f);
    public static final SpellProperty<Float> VERTICAL_SPEED = SpellProperty.floatProperty("vertical_speed", 0.02f);

    /** ticks carregados + potência capturada, por player (transiente, server). */
    public static final Map<UUID, float[]> CHARGING = new ConcurrentHashMap<>();

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (ctx.castingTicks() > 3 && !caster.onGround()) {
            caster.releaseUsingItem();
        }
        if (ctx.castingTicks() > 40) {
            caster.releaseUsingItem();
        }
        if (ctx.world().isClientSide) {
            ParticleBuilder.create(EBParticles.SPARK)
                    .pos(caster.getX() + (ctx.world().random.nextDouble() - 0.5) * ctx.castingTicks() / 25.0,
                            caster.getY() + 0.3,
                            caster.getZ() + (ctx.world().random.nextDouble() - 0.5) * ctx.castingTicks() / 25.0)
                    .time(4).spawn(ctx.world());
        } else {
            CHARGING.put(caster.getUUID(), new float[]{ctx.castingTicks(), ctx.modifiers().get(SpellModifiers.POTENCY)});
        }
        return caster.onGround();
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
