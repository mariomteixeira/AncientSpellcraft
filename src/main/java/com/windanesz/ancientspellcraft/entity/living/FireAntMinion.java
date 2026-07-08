package com.windanesz.ancientspellcraft.entity.living;

import com.koomplo.wizardry.setup.registries.EBAttachments;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;

/** Formiga de fogo kamikaze (1.12.2 EntityFireAnt): explode quando perde o vinculo com o dono. */
public class FireAntMinion extends Spider {

    public FireAntMinion(EntityType<? extends Spider> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createFireAntAttributes() {
        return Spider.createAttributes().add(Attributes.MAX_HEALTH, 8.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            level().addParticle(net.minecraft.core.particles.ParticleTypes.FLAME,
                    getX() + (random.nextDouble() - 0.5) * getBbWidth(),
                    getY() + getBbHeight() / 2 + random.nextDouble() * getBbHeight() / 2,
                    getZ() + (random.nextDouble() - 0.5) * getBbWidth(), 0, -0.05, 0);
        } else if (tickCount > 20) {
            var data = getData(EBAttachments.MINION_DATA);
            if (data.isSummoned() && data.getOwner() == null) {
                explode();
            }
        }
    }

    private void explode() {
        if (!level().isClientSide) {
            level().explode(this, getX(), getY(), getZ(), 1.0F, Level.ExplosionInteraction.MOB);
            discard();
        }
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

    @Override
    public boolean fireImmune() {
        return true;
    }
}
