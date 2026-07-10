package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Living Comet (1.12.2 LivingComet, branch sem charm_meteorite_stone — artefato fica no lote de
 * artefatos): cast contínuo transforma o caster num cometa — sobe nos primeiros 20t, acelera na
 * direção do olhar, controle vertical após 100t; pousar no chão com 40t+ explode (1.8) e dá
 * fire resistance. Sem dano de queda enquanto casta.
 */
public class LivingCometSpell extends Spell {

    private static final double Y_NUDGE_ACCELERATION = 0.12;
    private static final com.koomplo.wizardry.api.content.spell.properties.SpellProperty<Float> SPEED =
            com.koomplo.wizardry.api.content.spell.properties.SpellProperty.floatProperty("speed", 1.0f);
    private static final com.koomplo.wizardry.api.content.spell.properties.SpellProperty<Float> ACCELERATION =
            com.koomplo.wizardry.api.content.spell.properties.SpellProperty.floatProperty("acceleration", 0.05f);

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected @org.jetbrains.annotations.NotNull com.koomplo.wizardry.api.content.spell.properties.SpellProperties properties() {
        return com.koomplo.wizardry.api.content.spell.properties.SpellProperties.empty();
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        Level world = ctx.world();
        int ticks = ctx.castingTicks();

        this.playSound(world, caster, ticks, -1);
        caster.fallDistance = 0.0F;

        if (ticks > 40 && caster.onGround()) {
            if (!world.isClientSide) {
                world.explode(caster, caster.getX(), caster.getY(), caster.getZ(), 1.8F, Level.ExplosionInteraction.MOB);
                caster.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0));
            }
            caster.releaseUsingItem();
            return true;
        }

        if (!caster.isFallFlying()) {
            float speed = 2.0F * this.property(SPEED) * ctx.modifiers().get(SpellModifiers.POTENCY);
            float acceleration = 2.0F * this.property(ACCELERATION) * ctx.modifiers().get(SpellModifiers.POTENCY);
            Vec3 look = caster.getLookAngle();
            Vec3 motion = caster.getDeltaMovement();

            if (ticks < 20) {
                caster.push(0, 0.3, 0);
            }
            if (ticks > 20 && (Math.abs(motion.x) < speed || motion.x / look.x < 0)
                    && (Math.abs(motion.z) < speed || motion.z / look.z < 0)) {
                caster.push(look.x * acceleration * 2, 0, look.z * acceleration * 2);
            }
            if (ticks > 100 && (Math.abs(motion.y) < speed || motion.y / look.y < 0)) {
                caster.push(0, look.y * (acceleration + ticks / 20.0) + Y_NUDGE_ACCELERATION, 0);
            }
        }

        if (world.isClientSide) {
            for (int i = 0; i < 7; i++) {
                double x = caster.getX() - 1 + world.random.nextDouble() * 2;
                double y = caster.getY() + caster.getEyeHeight() - 0.5 + world.random.nextDouble();
                double z = caster.getZ() - 1 + world.random.nextDouble() * 2;
                ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).time(15).spawn(world);
            }
        }
        return true;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
