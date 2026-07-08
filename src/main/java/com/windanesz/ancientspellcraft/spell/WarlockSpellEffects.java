package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.DeferredObject;
import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.koomplo.wizardry.setup.registries.EBMobEffects;
import com.koomplo.wizardry.setup.registries.Elements;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

/** Tabela elemental do warlock attunement (1.12.2 WarlockElementalSpellEffects). */
public final class WarlockSpellEffects {

    public static int[] colours(Element element) {
        String name = element.getName();
        return switch (name) {
            case "fire" -> new int[]{0xff9600, 0xab3603, 0xd02700, 0xffffff};
            case "ice" -> new int[]{0xfafeff, 0xebfcff, 0xb5f4ff, 0xffffff};
            case "lightning" -> new int[]{0xffffff, 0x2f3d52, 0x225474, 0xffffff};
            case "necromancy" -> new int[]{0x56096b, 0x540154, 0x382366, 0xffffff};
            case "earth" -> new int[]{0x75a808, 0x6e8014, 0x795c28, 0xffffff};
            case "sorcery" -> new int[]{0x56e88e, 0x308264, 0x16a676, 0xffffff};
            case "healing" -> new int[]{0xfffff6, 0xfff69e, 0xffe163, 0xffffff};
            default -> new int[]{0xfc0303, 0x400101, 0x9d2cf3, 0xffffff};
        };
    }

    public static DeferredObject<SimpleParticleType> particle(Element element) {
        String name = element.getName();
        return switch (name) {
            case "fire" -> EBParticles.MAGIC_FIRE;
            case "ice" -> EBParticles.SNOW;
            case "lightning" -> EBParticles.SPARK;
            case "earth" -> EBParticles.LEAF;
            case "necromancy", "healing" -> EBParticles.SPARKLE;
            default -> EBParticles.FLASH;
        };
    }

    /** Dano+status elemental 1:1 do 1.12.2 (SORCERY aproximado: FORCE + empurrao no lugar do force_shove proxy). */
    public static void affectEntity(LivingEntity target, Element element, LivingEntity caster, boolean damageEntity) {
        if (target.level().isClientSide) return;
        String name = element.getName();
        switch (name) {
            case "fire" -> {
                if (!target.isOnFire()) target.igniteForSeconds(4);
                hit(target, caster, EBDamageSources.FIRE, damageEntity ? 2 : 0);
            }
            case "ice" -> {
                target.addEffect(new MobEffectInstance(EBMobEffects.holder(EBMobEffects.FROST), 60));
                hit(target, caster, EBDamageSources.FROST, damageEntity ? 2 : 0);
            }
            case "lightning" -> {
                if (damageEntity && !MagicDamageSource.isEntityImmune(EBDamageSources.SHOCK, target)) {
                    target.hurt(MagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.SHOCK), 3);
                }
            }
            case "necromancy" -> {
                hit(target, caster, EBDamageSources.WITHER, damageEntity ? 2 : 0);
                if (!target.hasEffect(MobEffects.WITHER)) target.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 1));
            }
            case "earth" -> {
                hit(target, caster, EBDamageSources.POISON, damageEntity ? 2 : 0);
                if (!target.hasEffect(MobEffects.POISON)) target.addEffect(new MobEffectInstance(MobEffects.POISON, 60));
            }
            case "sorcery" -> {
                if (!MagicDamageSource.isEntityImmune(EBDamageSources.FORCE, target)) {
                    hit(target, caster, EBDamageSources.FORCE, damageEntity ? 2 : 0);
                    EntityUtil.applyStandardKnockback(caster, target, 1.5f);
                }
            }
            case "healing" -> {
                hit(target, caster, EBDamageSources.RADIANT, damageEntity ? 2 : 0);
                if (!target.hasEffect(MobEffects.BLINDNESS)) target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));
                if (target.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD) && !target.isOnFire()) target.igniteForSeconds(4);
            }
            default -> hit(target, caster, EBDamageSources.MAGIC, damageEntity ? 4 : 0);
        }
    }

    private static void hit(LivingEntity target, LivingEntity caster, ResourceKey<DamageType> type, float damage) {
        if (damage > 0 && !MagicDamageSource.isEntityImmune(type, target)) {
            EntityUtil.attackEntityWithoutKnockback(target, MagicDamageSource.causeDirectMagicDamage(caster, type), damage);
        }
    }

    private WarlockSpellEffects() {
    }
}
