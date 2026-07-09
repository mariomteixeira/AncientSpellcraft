package com.windanesz.ancientspellcraft.entity.projectile;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.LocationCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.core.platform.Services;
import com.koomplo.wizardry.setup.registries.Spells;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Projétil do metamagic_projectile (1.12.2 EntityMetamagicProjectile): carrega uma spell e a casta
 * no ponto de impacto (LocationCastContext). Minions conjurados pelo impacto (órfãos recém-nascidos
 * num raio de 1.5) são adotados pelo atirador. Desvio documentado: som de dispel próprio do pack
 * substituído por vanilla; adoção usa MinionData do Redux (sem o par ISummonedCreature/constructs 1:1).
 */
public class MetamagicProjectileEntity extends MagicProjectileEntity {

    private Spell storedSpell = Spells.NONE;

    public MetamagicProjectileEntity(EntityType<? extends MetamagicProjectileEntity> type, Level level) {
        super(type, level);
    }

    public void setStoredSpell(Spell spell) {
        this.storedSpell = spell;
    }

    @Override
    public int getLifeTime() {
        return 16;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.AIR;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide) return;

        if (result instanceof BlockHitResult blockHit && storedSpell != Spells.NONE) {
            BlockPos pos = blockHit.getBlockPos().above();
            storedSpell.cast(new LocationCastContext(level(), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    blockHit.getDirection(), 0, 0, new SpellModifiers()));

            if (getOwner() != null) {
                for (Mob mob : level().getEntitiesOfClass(Mob.class, new AABB(pos).inflate(1.5))) {
                    if (mob.tickCount < 40) {
                        var data = Services.OBJECT_DATA.getMinionData(mob);
                        if (data != null && data.isSummoned() && data.getOwnerUUID() == null) {
                            data.setOwnerUUID(getOwner().getUUID());
                        }
                    }
                }
            }
        }
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.HOSTILE, 1.0F, 1.2F);
        discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            for (int i = 0; i < 5; i++) {
                double dx = (random.nextDouble() - 0.5) * getBbWidth();
                double dy = (random.nextDouble() - 0.5) * getBbHeight() + getBbHeight() / 2 - 0.1;
                double dz = (random.nextDouble() - 0.5) * getBbWidth();
                ParticleBuilder.create(EBParticles.DUST).color(0xffffff)
                        .pos(getX() + dx, getY() + dy, getZ() + dz).time(10).spawn(level());
            }
            if (tickCount > 1) {
                ParticleBuilder.create(EBParticles.FLASH)
                        .pos(getX() - getDeltaMovement().x / 2, getY() - getDeltaMovement().y / 2, getZ() - getDeltaMovement().z / 2)
                        .color(0xffffff).time(20 + random.nextInt(10)).spawn(level());
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("StoredSpell", storedSpell.getLocation().toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        Spell spell = Services.REGISTRY_UTIL.getSpell(ResourceLocation.tryParse(tag.getString("StoredSpell")));
        if (spell != null) storedSpell = spell;
    }
}
