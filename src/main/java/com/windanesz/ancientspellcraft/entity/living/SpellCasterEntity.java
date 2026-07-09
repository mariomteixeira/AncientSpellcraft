package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.block.SentinelBlockEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * Proxy invisível e atacável do sentinel (1.12.2 EntitySpellCaster): fica na posição do bloco,
 * carrega a vida da torreta e é quem "casta" a spell (EntityCastContext). Morreu -> bloco some;
 * bloco sumiu/não o referencia -> proxy some.
 */
public class SpellCasterEntity extends PathfinderMob {

    public SpellCasterEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        setNoAi(true);
        setInvisible(true);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes();
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount > 10
                && !(level().getBlockEntity(blockPosition()) instanceof SentinelBlockEntity sentinel && sentinel.isProxy(this))) {
            discard();
        }
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide && level().getBlockEntity(blockPosition()) instanceof SentinelBlockEntity) {
            level().destroyBlock(blockPosition(), false);
        }
        super.die(source);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushEntities() {
    }
}
