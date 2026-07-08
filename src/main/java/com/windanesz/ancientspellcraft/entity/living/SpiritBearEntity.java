package com.windanesz.ancientspellcraft.entity.living;

import com.koomplo.wizardry.setup.registries.EBAttachments;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

/** Urso espiritual companheiro (1.12.2 EntitySpiritBear): segue o dono; permanente ate morrer. */
public class SpiritBearEntity extends PolarBear {

    public SpiritBearEntity(EntityType<? extends PolarBear> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new FollowOwnerGoal());
    }

    public LivingEntity getSpiritOwner() {
        var data = getData(EBAttachments.MINION_DATA);
        return data.isSummoned() ? data.getOwner() : null;
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

    /** Segue o dono a partir de 10 blocos, para a 4 (1.12.2 SpiritBearAIFollowOwner). */
    class FollowOwnerGoal extends Goal {

        FollowOwnerGoal() {
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = getSpiritOwner();
            return owner != null && distanceToSqr(owner) > 100;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity owner = getSpiritOwner();
            return owner != null && distanceToSqr(owner) > 16 && !getNavigation().isDone();
        }

        @Override
        public void start() {
            LivingEntity owner = getSpiritOwner();
            if (owner != null) getNavigation().moveTo(owner, 1.2D);
        }
    }
}
