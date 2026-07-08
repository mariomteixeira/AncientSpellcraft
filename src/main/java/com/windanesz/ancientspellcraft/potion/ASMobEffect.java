package com.windanesz.ancientspellcraft.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/** MobEffect com ctor publico e tick opcional por lambda (roda todo tick; a lambda decide a cadencia). */
public class ASMobEffect extends MobEffect {

    public interface Tick {
        void tick(LivingEntity entity, int amplifier);
    }

    private final Tick tick;

    public ASMobEffect(MobEffectCategory category, int color) {
        this(category, color, null);
    }

    public ASMobEffect(MobEffectCategory category, int color, Tick tick) {
        super(category, color);
        this.tick = tick;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return tick != null;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (tick != null) tick.tick(entity, amplifier);
        return true;
    }
}
