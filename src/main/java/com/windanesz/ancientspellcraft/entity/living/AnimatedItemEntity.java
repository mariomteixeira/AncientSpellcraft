package com.windanesz.ancientspellcraft.entity.living;

import com.koomplo.wizardry.api.content.entity.living.ISpellCaster;
import com.koomplo.wizardry.api.content.item.ICastItem;
import com.koomplo.wizardry.api.content.item.IManaItem;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.content.entity.goal.AttackSpellGoal;
import com.koomplo.wizardry.setup.registries.EBAttachments;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Item animado que luta sozinho (1.12.2 EntityAnimatedItem): espada corpo-a-corpo, arco a distancia,
 * wand casta a spell atual (consumindo mana), TNT explode. Corpo invisivel - o renderer mostra o item.
 * Com HAS_ARMOUR veste o set espectral (animate_weapon + conjure_armour no offhand).
 */
public class AnimatedItemEntity extends PathfinderMob implements ISpellCaster, RangedAttackMob {

    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> HAS_ARMOUR =
            net.minecraft.network.syncher.SynchedEntityData.defineId(AnimatedItemEntity.class,
                    net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);

    public AnimatedItemEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_ARMOUR, false);
    }

    public boolean hasArmour() {
        return this.entityData.get(HAS_ARMOUR);
    }

    public void setHasArmour(boolean hasArmour) {
        this.entityData.set(HAS_ARMOUR, hasArmour);
    }

    private boolean lostArmour() {
        for (var slot : new net.minecraft.world.entity.EquipmentSlot[]{
                net.minecraft.world.entity.EquipmentSlot.HEAD, net.minecraft.world.entity.EquipmentSlot.CHEST,
                net.minecraft.world.entity.EquipmentSlot.LEGS, net.minecraft.world.entity.EquipmentSlot.FEET}) {
            if (!getItemBySlot(slot).isEmpty()) return false;
        }
        return true;
    }

    public static AttributeSupplier.Builder createAnimatedItemAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.ATTACK_SPEED, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F) {
            @Override
            public boolean canUse() {
                return isHolding(is -> is.getItem() instanceof BowItem) && super.canUse();
            }
        });
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true) {
            @Override
            public boolean canUse() {
                return !isHolding(is -> is.getItem() instanceof BowItem) && super.canUse();
            }
        });
        this.goalSelector.addGoal(4, new AttackSpellGoal<>(this, 1.0D, 15f, 25, 40));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, net.minecraft.world.entity.player.Player.class, 8.0F));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        var owner = getData(EBAttachments.MINION_DATA).getOwner();
        if (tickCount > 20 && ((getData(EBAttachments.MINION_DATA).isSummoned() && (owner == null || !owner.isAlive()))
                || (hasArmour() ? lostArmour() : getMainHandItem().isEmpty()))) {
            discard();
            return;
        }

        // TNT animada: explode ao alcancar o alvo
        if (getMainHandItem().is(Items.TNT) && getTarget() != null && distanceTo(getTarget()) < 2.5f) {
            setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            level().explode(this, getX(), getY(), getZ(), 2.0F, Level.ExplosionInteraction.MOB);
            discard();
            return;
        }

        // Wand: consome mana a cada 3s; sem mana, o encanto acaba
        if (tickCount % 60 == 0 && getMainHandItem().getItem() instanceof IManaItem manaItem) {
            manaItem.consumeMana(getMainHandItem(), 3, this);
            if (manaItem.getMana(getMainHandItem()) <= 0) {
                discard();
            }
        }
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (!level().isClientSide && reason != RemovalReason.CHANGED_DIMENSION && !getMainHandItem().isEmpty()) {
            spawnAtLocation(getMainHandItem().copy());
            setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        super.remove(reason);
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float velocity) {
        ItemStack arrowStack = new ItemStack(Items.ARROW);
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this, arrowStack, velocity, getMainHandItem());
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.333) - arrow.getY();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        arrow.shoot(dx, dy + dist * 0.2, dz, 1.6F, 14 - level().getDifficulty().getId() * 4);
        playSound(net.minecraft.sounds.SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
        level().addFreshEntity(arrow);
    }

    @Override
    public @NotNull List<Spell> getSpells() {
        if (getMainHandItem().getItem() instanceof ICastItem castItem) {
            Spell spell = castItem.getCurrentSpell(getMainHandItem());
            if (spell != null) return List.of(spell);
        }
        return List.of();
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
