package com.windanesz.ancientspellcraft.entity.construct;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.potion.ChaosEffect;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import com.windanesz.ancientspellcraft.spell.WarlockSpellEffects;
import com.koomplo.wizardry.setup.registries.Elements;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/** Campo do caos (1.12.2 EntityChaosField): aplica CHAOS aleatorio em todos no raio. */
public class ChaosFieldConstruct extends MagicConstructEntity {

    private static final double RADIUS = 5.0;

    public ChaosFieldConstruct(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            int amplifier = random.nextInt(ChaosEffect.VARIANTS);
            for (LivingEntity target : com.koomplo.wizardry.api.content.util.EntityUtil
                    .getLivingWithinRadius(RADIUS, getX(), getY(), getZ(), level())) {
                if (!target.hasEffect(ASEffects.CHAOS)) {
                    target.addEffect(new MobEffectInstance(ASEffects.CHAOS, 140, amplifier));
                }
            }
        } else {
            int[] colours = WarlockSpellEffects.colours(Elements.MAGIC);
            for (int i = 0; i < 6; i++) {
                double angle = 2 * Math.PI * random.nextDouble();
                double distance = RADIUS * Math.sqrt(random.nextDouble());
                ParticleBuilder.create(EBParticles.FLASH)
                        .pos(getX() + Math.cos(angle) * distance, getY() + random.nextDouble() * 2,
                                getZ() + Math.sin(angle) * distance)
                        .velocity(0, 0.05, 0).color(colours[random.nextInt(3)]).time(30).spawn(level());
            }
        }
    }
}
