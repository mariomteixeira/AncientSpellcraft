package com.windanesz.ancientspellcraft.entity.living;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.setup.registries.EBAttachments;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/** Vulcao imovel que cospe formigas de fogo (1.12.2 EntityVolcano). */
public class VolcanoEntity extends PathfinderMob {

    public VolcanoEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createVolcanoAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.MAX_HEALTH, 30.0D);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            if (random.nextInt(4) == 0) {
                level().addParticle(ParticleTypes.LARGE_SMOKE, getX(), getY() + 1.3, getZ(), 0, 0, 0);
            }
            ParticleBuilder.create(EBParticles.FLASH).pos(getX(), getY() + 0.1, getZ())
                    .color(252 / 255f, 105 / 255f, 0f).scale(4.3F).time(10).spawn(level());
            for (int i = 0; i < 2; i++) {
                level().addParticle(ParticleTypes.FLAME,
                        getX() + (random.nextDouble() - 0.5) * getBbWidth(),
                        getY() + getBbHeight() / 2 + random.nextDouble() * getBbHeight() / 2,
                        getZ() + (random.nextDouble() - 0.5) * getBbWidth(), 0, -0.1, 0);
            }
            if (random.nextInt(60) == 0) {
                for (int i = 0; i < 8; i++) {
                    level().addParticle(ParticleTypes.LAVA, getX() + random.nextFloat() - 0.5,
                            getY() + getBbHeight() / 2 + random.nextFloat() - 0.5, getZ() + random.nextFloat() - 0.5, 0, 0, 0);
                }
            }
        } else if (random.nextInt(60) == 0) {
            FireAntMinion ant = new FireAntMinion(ASEntities.FIRE_ANT_MINION.get(), level());
            ant.setPos(getX(), getY() + 1, getZ());
            var antData = ant.getData(EBAttachments.MINION_DATA);
            var ownData = this.getData(EBAttachments.MINION_DATA);
            antData.setSummoned(true);
            if (ownData.getOwner() != null) antData.setOwner(ownData.getOwner());
            antData.setLifetime(300);
            antData.markGoalRestart(true);
            level().addFreshEntity(ant);
            ant.push(random.nextFloat() * 0.3 * (random.nextBoolean() ? -1 : 1), random.nextFloat() * 0.3,
                    random.nextFloat() * 0.3 * (random.nextBoolean() ? -1 : 1));
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }
}
