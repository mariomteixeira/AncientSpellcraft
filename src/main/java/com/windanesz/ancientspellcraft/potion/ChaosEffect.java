package com.windanesz.ancientspellcraft.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/** Caos (1.12.2 PotionChaos): a cada 3s, o resultado do amplifier (0-16) acontece. */
public class ChaosEffect extends ASMobEffect {

    public static final int VARIANTS = 17;

    public ChaosEffect() {
        super(MobEffectCategory.HARMFUL, 0xfc0303);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 60 == 0;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;
        switch (amplifier) {
            case 0 -> entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3));
            case 1 -> entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60));
            case 2 -> entity.hurt(entity.damageSources().magic(), Math.min(10, entity.getMaxHealth() * 0.2f));
            case 3 -> entity.heal(entity.getMaxHealth() * 0.1f);
            case 4 -> entity.level().explode(null, entity.getX(), entity.getY(), entity.getZ(), 0.1f, Level.ExplosionInteraction.NONE);
            case 5 -> entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60));
            case 6 -> entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60));
            case 7 -> entity.addEffect(new MobEffectInstance(MobEffects.SATURATION, 10));
            case 8 -> entity.igniteForSeconds(3);
            case 9 -> entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60));
            case 10 -> entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 30));
            case 11 -> entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 60));
            case 12 -> entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30));
            case 13 -> entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60));
            case 14 -> entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60));
            case 15 -> entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60));
            case 16 -> randomTeleport(entity);
        }
        return true;
    }

    private static void randomTeleport(LivingEntity entity) {
        for (int attempt = 0; attempt < 8; attempt++) {
            double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 20;
            double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 20;
            double y = entity.getY() + entity.getRandom().nextInt(8) - 4;
            if (entity.randomTeleport(x, y, z, true)) return;
        }
    }
}
