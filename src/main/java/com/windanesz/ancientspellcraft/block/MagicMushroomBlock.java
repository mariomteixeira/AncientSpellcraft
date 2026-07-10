package com.windanesz.ancientspellcraft.block;

import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.core.AllyDesignation;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.koomplo.wizardry.setup.registries.EBMobEffects;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Cogumelo magico (1.12.2 BlockMagicMushroom): cresce em 8 estagios; maduro, estoura no toque com o efeito do tipo. */
public class MagicMushroomBlock extends BushBlock implements EntityBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);
    public static final int POTION_DURATION = 140;

    public enum Type {
        POISON(true), ICE(true), FIRE(true), WITHER(true), FORCE(true), HEALING(false),
        SHOCKING(true), MIND(true), CLEANSING(false), EXPLOSIVE(true), EMPOWERING(false);

        public final boolean combat;

        Type(boolean combat) {
            this.combat = combat;
        }
    }

    private final Type type;

    public MagicMushroomBlock(Properties properties, Type type) {
        super(properties);
        this.type = type;
        registerDefaultState(stateDefinition.any().setValue(AGE, 7));
    }

    @Override
    protected @NotNull com.mojang.serialization.MapCodec<? extends BushBlock> codec() {
        return simpleCodec(props -> new MagicMushroomBlock(props, type));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState state, @NotNull net.minecraft.world.level.BlockGetter world, @NotNull BlockPos pos) {
        return state.isSolidRender(world, pos);
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return state.getValue(AGE) < 7;
    }

    @Override
    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        int age = state.getValue(AGE);
        if (age < 7) level.setBlock(pos, state.setValue(AGE, age + 1), 2);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (level.isClientSide || state.getValue(AGE) < 7 || !(entity instanceof LivingEntity target)) return;

        LivingEntity caster = null;
        float potency = 1f;
        if (level.getBlockEntity(pos) instanceof TemporaryBlockEntity be) {
            caster = be.getCaster();
            if (be.getDamage() > 0) potency = be.getDamage();
        }
        float damage = 2.0f * potency; // base DAMAGE do fairy_ring aplicado por potency
        var source = caster == null ? target.damageSources().cactus()
                : MagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.MAGIC);

        boolean beneficial = !type.combat
                || (caster != null && !AllyDesignation.isValidTarget(caster, target));
        if (apply(level, pos, target, caster, source, damage, potency, beneficial)) {
            level.removeBlock(pos, false);
        }
    }

    private boolean apply(Level level, BlockPos pos, LivingEntity target, @Nullable LivingEntity caster,
                          net.minecraft.world.damagesource.DamageSource source, float damage, float potency, boolean beneficial) {
        switch (type) {
            case POISON -> {
                if (beneficial) return false;
                target.addEffect(new MobEffectInstance(MobEffects.POISON, POTION_DURATION, 1));
                EntityUtil.attackEntityWithoutKnockback(target, source, damage);
            }
            case ICE -> {
                if (beneficial) return false;
                target.addEffect(new MobEffectInstance(EBMobEffects.holder(EBMobEffects.FROST), POTION_DURATION, 1));
                EntityUtil.attackEntityWithoutKnockback(target, source, damage);
            }
            case FIRE -> {
                if (beneficial) return false;
                EntityUtil.attackEntityWithoutKnockback(target, source, damage);
                target.igniteForSeconds(POTION_DURATION / 10f);
            }
            case WITHER -> {
                if (beneficial) return false;
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, POTION_DURATION, 1));
                EntityUtil.attackEntityWithoutKnockback(target, source, damage);
            }
            case FORCE -> {
                if (beneficial || MagicDamageSource.isEntityImmune(EBDamageSources.SHOCK, target)) return false;
                // amulet_anchoring (do Redux) impede o arremesso (1.12.2 BlockMushroomForce)
                if (target instanceof net.minecraft.world.entity.player.Player player
                        && com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                        com.koomplo.wizardry.setup.registries.EBItems.AMULET_ANCHORING.get())) {
                    return false;
                }
                target.setDeltaMovement(target.getX() - (pos.getX() + 0.5), target.getY() + 1 - (pos.getY() + 0.5),
                        target.getZ() - (pos.getZ() + 0.5));
                target.hurtMarked = true;
            }
            case HEALING -> {
                if (beneficial) target.heal(1.5f * potency);
                else EntityUtil.attackEntityWithoutKnockback(target, source, damage * 4f);
            }
            case SHOCKING -> {
                if (beneficial) return false;
                target.hurt(caster == null ? source : MagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.SHOCK), damage);
                for (LivingEntity secondary : EntityUtil.getLivingWithinRadius(3, pos.getX(), pos.getY(), pos.getZ(), level)) {
                    if (secondary != target && (caster == null || AllyDesignation.isValidTarget(caster, secondary))) {
                        EntityUtil.attackEntityWithoutKnockback(secondary, source, damage * 0.5f);
                    }
                }
            }
            case MIND -> {
                if (beneficial) return false;
                if (target instanceof net.minecraft.world.entity.player.Player) {
                    // 1.12.2 BlockMushroomMind: players ganham só nausea + blindness
                    target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, POTION_DURATION / 2, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, POTION_DURATION / 2, 0));
                } else if (target instanceof net.minecraft.world.entity.Mob mob) {
                    // 1.12.2: mobs — 30% mind control, 20% fear, resto mind_trick (efeitos do Redux)
                    float chance = level.random.nextFloat();
                    if (chance > 0.7f && caster != null) {
                        mob.addEffect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                                .wrapAsHolder(com.koomplo.wizardry.setup.registries.EBMobEffects.MIND_CONTROL.get()),
                                POTION_DURATION * 10, 0));
                    } else if (chance > 0.5f && caster != null) {
                        mob.addEffect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                                .wrapAsHolder(com.koomplo.wizardry.setup.registries.EBMobEffects.FEAR.get()),
                                POTION_DURATION * 5, 0));
                    } else {
                        mob.setTarget(null);
                        mob.addEffect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                                .wrapAsHolder(com.koomplo.wizardry.setup.registries.EBMobEffects.MIND_TRICK.get()),
                                POTION_DURATION * 5, 0));
                    }
                }
            }
            case CLEANSING -> {
                if (!beneficial) return false;
                List.copyOf(target.getActiveEffects()).forEach(effect -> target.removeEffect(effect.getEffect()));
            }
            case EXPLOSIVE -> {
                if (beneficial) return false;
                EntityUtil.attackEntityWithoutKnockback(target, source, damage);
                target.igniteForSeconds(5);
                for (LivingEntity secondary : EntityUtil.getLivingWithinRadius(3, pos.getX(), pos.getY(), pos.getZ(), level)) {
                    if (secondary != target && (caster == null || AllyDesignation.isValidTarget(caster, secondary))) {
                        EntityUtil.attackEntityWithoutKnockback(secondary, source, damage);
                        secondary.igniteForSeconds(4);
                    }
                }
                level.levelEvent(2001, pos, net.minecraft.world.level.block.Block.getId(level.getBlockState(pos)));
            }
            case EMPOWERING -> {
                if (!beneficial) return false;
                var options = List.of(ASEffects.MANA_REGENERATION, ASEffects.SPELL_SIPHON, ASEffects.SPELL_COOLDOWN);
                target.addEffect(new MobEffectInstance(options.get(level.random.nextInt(options.size())), POTION_DURATION * 2, 0));
            }
        }
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TemporaryBlockEntity(pos, state);
    }

    // ===== helpers estaticos (1.12.2 getRandomMushroom/tryPlaceMushroom) =====

    public static net.minecraft.world.level.block.Block randomMushroom(Level level, boolean combat) {
        var pool = ASBlocks.MUSHROOMS.entrySet().stream()
                .filter(e -> ((MagicMushroomBlock) e.getValue().get()).type.combat == combat)
                .map(java.util.Map.Entry::getValue).toList();
        return pool.get(level.random.nextInt(pool.size())).get();
    }

    public static boolean tryPlace(Level level, BlockPos pos, @Nullable LivingEntity caster,
                                   net.minecraft.world.level.block.Block mushroom, int lifetime, float potency) {
        if (!level.getBlockState(pos).canBeReplaced()
                || !level.getBlockState(pos.below()).isSolidRender(level, pos.below())) return false;
        level.setBlockAndUpdate(pos, mushroom.defaultBlockState().setValue(AGE, 0));
        if (level.getBlockEntity(pos) instanceof TemporaryBlockEntity be) {
            be.setCaster(caster);
            be.setLifetime(lifetime);
            be.setDamage(potency);
        }
        return true;
    }

    @Override
    public <T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            net.minecraft.world.level.Level level, net.minecraft.world.level.block.state.BlockState state,
            net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof TemporaryBlockEntity temporary) TemporaryBlockEntity.serverTick(lvl, pos, st, temporary);
        };
    }
}
