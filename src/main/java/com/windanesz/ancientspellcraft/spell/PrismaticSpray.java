package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.core.AllyDesignation;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.windanesz.ancientspellcraft.entity.ChaosOrbEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Jato prismatico (1.12.2 PrismaticSpray): cada inimigo no raio recebe um FEIXE ELEMENTAL DISTINTO
 * (dano direto + status do elemento). Desvio documentado: usa a tabela WarlockSpellEffects
 * (equivalente por elemento) em vez do switch 1:1; TODO charm_prismatic_spray (modo feixe unico).
 */
public class PrismaticSpray extends Spell {

    public static final SpellProperty<Integer> DIRECT_DAMAGE = SpellProperty.intProperty("direct_damage");

    @Override
    public boolean cast(PlayerCastContext ctx) {
        double radius = property(DefaultProperties.BLAST_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST);
        float damage = property(DIRECT_DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY);

        // charm_prismatic_spray: modo feixe único — todos os elementos convergem no alvo mirado
        if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(ctx.caster(),
                com.windanesz.ancientspellcraft.registry.ASItems.CHARM_PRISMATIC_SPRAY.get())) {
            return castFocusedBeam(ctx, damage);
        }

        List<Element> beams = new ArrayList<>(List.of(ChaosOrbEntity.ELEMENTS));
        beams.remove(0); // MAGIC fora, como o original
        boolean foundTarget = false;
        Vec3 origin = ctx.caster().getEyePosition().add(0, -0.3, 0);

        for (LivingEntity target : EntityUtil.getLivingWithinRadius(radius,
                ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.world())) {
            if (target == ctx.caster() || !AllyDesignation.isValidTarget(ctx.caster(), target)) continue;
            foundTarget = true;
            Element element = beams.remove(ctx.world().random.nextInt(beams.size()));
            if (!ctx.world().isClientSide) {
                EntityUtil.attackEntityWithoutKnockback(target,
                        MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.MAGIC), damage);
                WarlockSpellEffects.affectEntity(target, element, ctx.caster(), false);
            } else {
                int[] colours = WarlockSpellEffects.colours(element);
                Vec3 end = target.position().add(0, target.getBbHeight() / 2, 0);
                double distance = origin.distanceTo(end);
                Vec3 direction = end.subtract(origin).normalize();
                for (double d = 0.5; d < distance; d += 0.4) {
                    Vec3 pos = origin.add(direction.scale(d));
                    ParticleBuilder.create(WarlockSpellEffects.particle(element))
                            .pos(pos.x, pos.y, pos.z).color(colours[ctx.world().random.nextInt(2)])
                            .scale(1.2f).time(12).spawn(ctx.world());
                }
            }
            if (beams.isEmpty()) break;
        }
        if (!foundTarget) {
            if (!ctx.world().isClientSide) {
                ctx.caster().displayClientMessage(Component.translatable("spell.ancientspellcraft.prismatic_spray.no_target"), true);
            }
            return false;
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    /**
     * Modo do charm (1.12.2 branch com artefato): ray no olhar; o alvo único toma dano x1.5 +
     * poison/paralysis/wither/blindness/frost + fogo. Visual: os 7 feixes convergem nele.
     */
    private boolean castFocusedBeam(PlayerCastContext ctx, float damage) {
        double range = 10 * ctx.modifiers().get(SpellModifiers.RANGE);
        Vec3 origin = ctx.caster().getEyePosition().add(0, -0.3, 0);
        Vec3 look = ctx.caster().getLookAngle();
        Vec3 endpoint = origin.add(look.scale(range));
        var entityHit = net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(
                ctx.world(), ctx.caster(), origin, endpoint,
                ctx.caster().getBoundingBox().expandTowards(look.scale(range)).inflate(1.0),
                e -> e instanceof LivingEntity && e != ctx.caster());
        if (entityHit == null || !(entityHit.getEntity() instanceof LivingEntity target)) {
            if (!ctx.world().isClientSide) {
                ctx.caster().displayClientMessage(Component.translatable("spell.ancientspellcraft.prismatic_spray.no_target"), true);
            }
            return false;
        }
        int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
        if (!ctx.world().isClientSide) {
            EntityUtil.attackEntityWithoutKnockback(target,
                    MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.MAGIC), damage * 1.5f);
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON, duration));
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                            com.koomplo.wizardry.setup.registries.EBMobEffects.PARALYSIS.get()), duration, 0));
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WITHER, duration));
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.BLINDNESS, duration, 0));
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(
                            com.koomplo.wizardry.setup.registries.EBMobEffects.FROST.get()), duration, 0));
            target.setRemainingFireTicks(duration);
        } else {
            Vec3 end = target.position().add(0, target.getBbHeight() / 2, 0);
            List<Element> beams = new ArrayList<>(List.of(ChaosOrbEntity.ELEMENTS));
            beams.remove(0);
            for (int i = 0; i < beams.size(); i++) {
                // feixes saem de um leque vertical e convergem no alvo (aproximação do 1.12.2)
                Vec3 beamOrigin = origin.add(0, Math.cos((i * Math.PI) / 3.5) * 2, 0);
                Vec3 direction = end.subtract(beamOrigin).normalize();
                double distance = beamOrigin.distanceTo(end);
                int[] colours = WarlockSpellEffects.colours(beams.get(i));
                for (double d = 0.5; d < distance; d += 0.4) {
                    Vec3 pos = beamOrigin.add(direction.scale(d));
                    ParticleBuilder.create(WarlockSpellEffects.particle(beams.get(i)))
                            .pos(pos.x, pos.y, pos.z).color(colours[0]).scale(1.2f).time(12).spawn(ctx.world());
                }
            }
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
