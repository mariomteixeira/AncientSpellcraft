package com.windanesz.ancientspellcraft.potion;

import com.koomplo.wizardry.api.content.effect.CurseMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/** Curse do Redux com tick opcional por lambda. */
public class ASCurseMobEffect extends CurseMobEffect {

    private final ASMobEffect.Tick tick;

    public ASCurseMobEffect(MobEffectCategory category, int color) {
        this(category, color, null);
    }

    public ASCurseMobEffect(MobEffectCategory category, int color, ASMobEffect.Tick tick) {
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
