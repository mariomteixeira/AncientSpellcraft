package com.windanesz.ancientspellcraft.entity;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.core.AllyDesignation;
import com.koomplo.wizardry.setup.registries.Elements;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.spell.WarlockSpellEffects;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/** Orbe do caos (1.12.2 EntityChaosOrb): acelera, persegue e SE DIVIDE em novas orbes por alvo. */
public class ChaosOrbEntity extends MagicProjectileEntity {

    private static final EntityDataAccessor<Integer> ELEMENT_ID =
            SynchedEntityData.defineId(ChaosOrbEntity.class, EntityDataSerializers.INT);

    public static final Element[] ELEMENTS = {Elements.MAGIC, Elements.FIRE, Elements.ICE, Elements.LIGHTNING,
            Elements.NECROMANCY, Elements.EARTH, Elements.SORCERY, Elements.HEALING};

    private int generation = 0;

    public ChaosOrbEntity(EntityType<? extends ChaosOrbEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ELEMENT_ID, 0);
    }

    public void setGeneration(int generation) {
        this.generation = Math.max(0, generation);
    }

    public void setElement(Element element) {
        for (int i = 0; i < ELEMENTS.length; i++) {
            if (ELEMENTS[i] == element) {
                entityData.set(ELEMENT_ID, i);
                return;
            }
        }
    }

    public Element getElement() {
        return ELEMENTS[entityData.get(ELEMENT_ID)];
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.MAGMA_CREAM;
    }


    @Override
    public void tick() {
        super.tick();
        if (tickCount > 100) { discard(); return; }
        Element element = getElement();
        if (level().isClientSide && tickCount > 1) {
            int[] colours = WarlockSpellEffects.colours(element);
            ParticleBuilder.create(WarlockSpellEffects.particle(element), random,
                            getX() - getDeltaMovement().x / 2, getY() - getDeltaMovement().y / 2,
                            getZ() - getDeltaMovement().z / 2, 0.03, true)
                    .color(colours[0]).scale(0.3f).time(20 + random.nextInt(10)).spawn(level());
        }
        if (!level().isClientSide && (tickCount == 1 || tickCount % 2 == 0)) {
            setDeltaMovement(getDeltaMovement().scale(1.1));
            var nearby = EntityUtil.getLivingWithinRadius(5, getX(), getY(), getZ(), level());
            if (nearby.isEmpty()) nearby = EntityUtil.getLivingWithinRadius(10, getX(), getY(), getZ(), level());
            LivingEntity owner = getOwner() instanceof LivingEntity living ? living : null;
            nearby.removeIf(e -> !e.isAlive() || e == owner
                    || (owner != null && AllyDesignation.isAllied(owner, e)));
            if (!nearby.isEmpty() && generation > 0) {
                generation--;
                nearby.sort(Comparator.comparingDouble(e -> e.distanceToSqr(this)));
                LivingEntity target = nearby.get(nearby.size() > 1 ? 1 : 0);
                ChaosOrbEntity orb = new ChaosOrbEntity(ASEntities.CHAOS_ORB.get(), level());
                orb.setPos(getX(), getY(), getZ());
                orb.setOwner(getOwner());
                orb.setElement(element);
                orb.setGeneration(generation);
                orb.damageMultiplier = damageMultiplier;
                var dir = target.position().add(0, target.getBbHeight() / 2, 0).subtract(position()).normalize().scale(0.5);
                orb.setDeltaMovement(dir);
                level().addFreshEntity(orb);
            }
            if (!nearby.isEmpty()) {
                var target = nearby.get(0);
                var dir = target.position().add(0, target.getBbHeight() / 2, 0).subtract(position()).normalize()
                        .scale(Math.max(0.4, getDeltaMovement().length()));
                setDeltaMovement(getDeltaMovement().scale(0.5).add(dir.scale(0.5)));
            }
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (!(result.getEntity() instanceof LivingEntity target) || level().isClientSide) return;
        Element element = getElement();
        LivingEntity owner = getOwner() instanceof LivingEntity living ? living : null;
        float damage = 4.0f * damageMultiplier; // DAMAGE do JSON aplicado via damageMultiplier no spell
        target.hurt(MagicDamageSource.causeIndirectMagicDamage(this, owner,
                com.koomplo.wizardry.setup.registries.EBDamageSources.MAGIC), damage);
        if (!element.getName().equals("sorcery")) {
            WarlockSpellEffects.affectEntity(target, element, owner, false);
        }
        discard();
    }
}
