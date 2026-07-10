package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.item.IManaItem;
import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.content.entity.projectile.IceShardEntity;
import com.koomplo.wizardry.core.AllyDesignation;
import com.koomplo.wizardry.core.integrations.ArtifactChannel;
import com.koomplo.wizardry.core.platform.Services;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.koomplo.wizardry.setup.registries.EBItems;
import com.koomplo.wizardry.setup.registries.EBMobEffects;
import com.windanesz.ancientspellcraft.block.MagicMushroomBlock;
import com.windanesz.ancientspellcraft.entity.living.AnimatedItemEntity;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Efeitos elementais 1:1 da espada de battlemage (1.12.2 EnumElementalSwordEffect): lesser em todo
 * golpe, greater no golpe com a carga cheia. Desvios: partículas dos embers/beam aproximadas ou
 * omitidas (server-side); o lesser do SORCERY era um evento de left-click à parte no 1.12.2 (TODO);
 * o ramo do glyph_leeching no greater da NECROMANCY já existe genérico no port (onda 3b).
 */
public final class ElementalSwordEffects {

    private static final String LAST_POS_TAG = "LastPos";

    /** Golpe da espada (server): aplica lesser sempre e greater quando carregado. Wielder pode ser NPC. */
    public static void hit(Element element, ItemStack sword, LivingEntity target, LivingEntity wielder, boolean charged) {
        if (wielder.level().isClientSide) return;
        switch (element.getName()) {
            case "fire" -> {
                target.igniteForSeconds(3);
                // 1.12.2: 20% de embers (40% se matou) — aproximado com partículas de lava
                double roll = wielder.getRandom().nextDouble();
                if ((roll < 0.2 || (target.getHealth() <= 0 && roll < 0.4)) && wielder.level() instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.LAVA, target.getX(), target.getY() + target.getBbHeight() / 2,
                            target.getZ(), 6, 0.3, 0.3, 0.3, 0.02);
                }
                if (charged) {
                    wielder.addEffect(new MobEffectInstance(holder(EBMobEffects.FIRESKIN.get()), 160, 0));
                    wielder.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 160, 0));
                    if (target.isOnFire()) {
                        target.addEffect(new MobEffectInstance(ASEffects.SOUL_SCORCH, 100, 0));
                    }
                }
            }
            case "ice" -> {
                var frost = holder(EBMobEffects.FROST.get());
                int duration = 40;
                int amplifier = 0;
                MobEffectInstance current = target.getEffect(frost);
                if (current != null) {
                    duration += current.getDuration();
                    amplifier = current.getAmplifier();
                    if (target.getRandom().nextDouble() < 0.2 && amplifier < 3) amplifier++;
                }
                // 25% se matou um alvo congelado: estilhaça em 8 ice shards
                if (wielder.getRandom().nextDouble() < 0.25f && target.getHealth() == 0f && current != null) {
                    for (int i = 0; i < 8; i++) {
                        double dx = wielder.getRandom().nextDouble() - 0.5;
                        double dy = wielder.getRandom().nextDouble() - 0.5;
                        double dz = wielder.getRandom().nextDouble() - 0.5;
                        IceShardEntity shard = new IceShardEntity(target.level());
                        shard.setPos(target.getX() + dx + Math.signum(dx) * target.getBbWidth(),
                                target.getY() + target.getBbHeight() / 2 + dy,
                                target.getZ() + dz + Math.signum(dz) * target.getBbWidth());
                        shard.setDeltaMovement(dx * 1.5, dy * 1.5, dz * 1.5);
                        shard.setOwner(wielder);
                        target.level().addFreshEntity(shard);
                    }
                }
                target.addEffect(new MobEffectInstance(frost, duration, amplifier));
                if (charged) {
                    for (LivingEntity curr : EntityUtil.getLivingWithinRadius(
                            4, target.getX(), target.getY(), target.getZ(), target.level())) {
                        if (curr == wielder || curr == target || AllyDesignation.isAllied(wielder, curr)) continue;
                        EntityUtil.attackEntityWithoutKnockback(curr,
                                MagicDamageSource.causeDirectMagicDamage(wielder, EBDamageSources.FROST), 3.5f);
                        curr.addEffect(new MobEffectInstance(frost, 60, 0));
                        double angle = (getAngleBetweenEntities(wielder, curr) + 90) * Math.PI / 180;
                        double distance = wielder.distanceTo(curr) - 4;
                        double strength = Math.min(1 / (distance * distance), 1);
                        curr.setDeltaMovement(curr.getDeltaMovement().add(
                                strength * -Math.cos(angle), 0, strength * -Math.sin(angle)));
                    }
                }
            }
            case "lightning" -> {
                EntityUtil.getLivingWithinRadius(4, target.getX(), target.getY() + target.getBbHeight() / 2,
                                target.getZ(), wielder.level()).stream()
                        .filter(e -> e != target && e != wielder)
                        .filter(e -> AllyDesignation.isValidTarget(wielder, e))
                        .limit(3)
                        .forEach(secondary -> electrocute(wielder, secondary, 4));
                if (charged) {
                    rechargeSwordOrOffhand(sword, wielder, 60);
                }
            }
            case "necromancy" -> {
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
                wielder.heal(0.5f);
                if (target.getHealth() == 0f) {
                    wielder.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0));
                    wielder.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0));
                }
                if (charged) {
                    EntityUtil.attackEntityWithoutKnockback(target,
                            MagicDamageSource.causeDirectMagicDamage(wielder, EBDamageSources.WITHER), 2.5f);
                    wielder.heal(1.5f);
                    if (wielder instanceof Player player) player.getFoodData().eat(2, 0.1f);
                }
            }
            case "earth" -> {
                int duration = 80;
                int amplifier = 0;
                MobEffectInstance poison = target.getEffect(MobEffects.POISON);
                if (poison != null) {
                    duration += (int) (poison.getDuration() * 0.1);
                    amplifier = poison.getAmplifier();
                    if (target.getRandom().nextDouble() < 0.2 && amplifier < 2) amplifier++;
                }
                target.addEffect(new MobEffectInstance(MobEffects.POISON, duration, amplifier));
                if (wielder.getRandom().nextDouble() < 0.5) {
                    var pos = BlockUtil.findNearbyFloorSpace(wielder.level(), target.blockPosition(), 4, 7, false, target);
                    if (pos != null) {
                        MagicMushroomBlock.tryPlace(wielder.level(), pos, wielder,
                                MagicMushroomBlock.randomMushroom(wielder.level(), true), 600, 1f);
                    }
                }
            }
            case "sorcery" -> {
                // lesser: no 1.12.2 era tratado num evento de left-click à parte (TODO)
                if (charged) {
                    double d = wielder.getRandom().nextDouble();
                    if (d < 0.2) {
                        target.addEffect(new MobEffectInstance(holder(EBMobEffects.CONTAINMENT.get()), 60, 0));
                    } else if (d < 0.5) {
                        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                    }
                    int duration = 100;
                    if (wielder instanceof Player player
                            && ArtifactChannel.isEquipped(player, EBItems.RING_CONJURER.get())) duration += 60;
                    ItemStack conjured;
                    if (wielder.getRandom().nextDouble() < 0.8f) {
                        conjured = new ItemStack(EBItems.SPECTRAL_SWORD.get());
                    } else {
                        conjured = new ItemStack(EBItems.SPECTRAL_BOW.get());
                        duration += 60;
                    }
                    var pos = BlockUtil.findNearbyFloorSpace(wielder.level(), wielder.blockPosition(), 4, 8, false, wielder);
                    if (pos != null && wielder.level() instanceof ServerLevel server) {
                        AnimatedItemEntity minion = new AnimatedItemEntity(ASEntities.ANIMATED_ITEM.get(), wielder.level());
                        minion.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                        minion.setItemInHand(InteractionHand.MAIN_HAND, conjured);
                        minion.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
                        var data = Services.OBJECT_DATA.getMinionData(minion);
                        data.setSummoned(true);
                        data.setOwnerUUID(wielder.getUUID());
                        data.setLifetime(duration);
                        data.updateGoals();
                        server.addFreshEntity(minion);
                    }
                }
            }
            case "healing" -> {
                if (target.getType().is(EntityTypeTags.UNDEAD)) {
                    target.igniteForSeconds(2);
                    var mark = holder(EBMobEffects.MARK_OF_SACRIFICE.get());
                    if (!target.hasEffect(mark)) {
                        target.addEffect(new MobEffectInstance(mark, 40, 1));
                    }
                }
                if (charged) {
                    List<MobEffectInstance> pool = List.of(
                            new MobEffectInstance(MobEffects.REGENERATION, 100, 1),
                            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 1),
                            new MobEffectInstance(MobEffects.ABSORPTION, 200, 1),
                            new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0),
                            new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0),
                            new MobEffectInstance(MobEffects.DIG_SPEED, 200, 0),
                            new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0),
                            new MobEffectInstance(MobEffects.SATURATION, 60, 0),
                            new MobEffectInstance(holder(EBMobEffects.EMPOWERMENT.get()), 200, 0),
                            new MobEffectInstance(holder(EBMobEffects.FONT_OF_MANA.get()), 200, 0),
                            new MobEffectInstance(holder(EBMobEffects.WARD.get()), 200, 0),
                            new MobEffectInstance(ASEffects.FORTIFIED_ARCHERY, 200, 0),
                            new MobEffectInstance(ASEffects.PROJECTILE_WARD, 200, 0),
                            new MobEffectInstance(ASEffects.WIZARD_SHIELD, 200, 8));
                    MobEffectInstance chosen = pool.get(wielder.getRandom().nextInt(pool.size()));
                    for (LivingEntity ally : EntityUtil.getLivingWithinRadius(
                            16, wielder.getX(), wielder.getY(), wielder.getZ(), wielder.level())) {
                        if (AllyDesignation.isAllied(wielder, ally)) {
                            ally.addEffect(new MobEffectInstance(chosen));
                        }
                    }
                }
            }
            default -> { /* magic: sem efeito, igual ao 1.12.2 */ }
        }
    }

    /** LIGHTNING (1.12.2 onUpdateEffect): andar no chão recarrega 1 de mana a cada 20t. */
    public static void tick(Element element, ItemStack sword, Player player) {
        if (player.level().isClientSide || !"lightning".equals(element.getName())
                || player.level().getGameTime() % 20 != 0 || !player.onGround()) return;
        long pos = player.blockPosition().asLong();
        CustomData data = sword.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        long last = data.copyTag().getLong(LAST_POS_TAG);
        CustomData.update(DataComponents.CUSTOM_DATA, sword, tag -> tag.putLong(LAST_POS_TAG, pos));
        if (last == pos) return;
        rechargeSwordOrOffhand(sword, player, 1);
    }

    private static void rechargeSwordOrOffhand(ItemStack sword, LivingEntity wielder, int amount) {
        if (sword.getItem() instanceof IManaItem swordMana && !swordMana.isManaFull(sword)) {
            swordMana.rechargeMana(sword, amount);
        } else if (wielder.getOffhandItem().getItem() instanceof IManaItem offhandMana) {
            offhandMana.rechargeMana(wielder.getOffhandItem(), amount);
        }
    }

    private static void electrocute(LivingEntity wielder, LivingEntity target, float damage) {
        if (MagicDamageSource.isEntityImmune(EBDamageSources.SHOCK, target)) {
            if (wielder instanceof Player player) {
                player.displayClientMessage(Component.translatable("spell.resist",
                        target.getName(), Component.translatable("element.ebwizardry.lightning")), true);
            }
        } else {
            EntityUtil.attackEntityWithoutKnockback(target,
                    MagicDamageSource.causeDirectMagicDamage(wielder, EBDamageSources.SHOCK), damage);
        }
    }

    private static double getAngleBetweenEntities(net.minecraft.world.entity.Entity first, net.minecraft.world.entity.Entity second) {
        return Math.atan2(second.getZ() - first.getZ(), second.getX() - first.getX()) * (180 / Math.PI) + 90;
    }

    private static net.minecraft.core.Holder<MobEffect> holder(MobEffect effect) {
        return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
    }

    private ElementalSwordEffects() {}
}
