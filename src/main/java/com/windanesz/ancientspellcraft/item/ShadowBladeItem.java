package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Lamina sombria conjurada (1.12.2 ItemShadowBlade). Com o charm_shadow_blade equipado,
 * right-click vira a forma sombria por 6 ticks: invisível/invulnerável, dash na direção do olhar
 * (agachado: sobe), wither em quem encostar (cooldown de 80t).
 */
public class ShadowBladeItem extends SwordItem {

    private static final String CHARGE_TAG = "ShadowCharge";
    private static final String UP_TAG = "ShadowUp";
    private static final double EXTRA_HIT_MARGIN = 1;
    /** wither_duration do conjure_shadow_blade (1.12.2). */
    private static final int WITHER_DURATION = 80;

    public ShadowBladeItem() {
        super(Tiers.IRON, new Properties().durability(1200).rarity(Rarity.UNCOMMON)
                .attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4F)));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player, ASItems.CHARM_SHADOW_BLADE.get())
                && !player.getCooldowns().isOnCooldown(this)) {
            var flags = player.getData(ASAttachments.PLAYER_DATA);
            flags.putInt(CHARGE_TAG, 6);
            flags.putBoolean(UP_TAG, player.isShiftKeyDown());
            player.setData(ASAttachments.PLAYER_DATA, flags);
            player.getCooldowns().addCooldown(this, 80);
            player.setInvulnerable(true);
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) tickShadowForm(player);
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    private static void tickShadowForm(Player player) {
        var flags = player.getData(ASAttachments.PLAYER_DATA);
        int charge = flags.getInt(CHARGE_TAG);
        if (charge <= 0) return;

        player.setInvisible(true);
        Vec3 look = player.getLookAngle();
        if (flags.getBoolean(UP_TAG)) {
            player.setDeltaMovement(look.x / 2, 1.0, look.z / 2);
        } else {
            player.setDeltaMovement(look.x * 1.5, player.getDeltaMovement().y, look.z * 1.5);
        }

        if (player.level().isClientSide) {
            var rand = player.level().random;
            for (int i = 0; i < 5; i++) {
                float x = rand.nextFloat() * (rand.nextBoolean() ? 1 : -1);
                float y = rand.nextFloat() * (rand.nextBoolean() ? 1 : -1);
                float z = rand.nextFloat() * (rand.nextBoolean() ? 1 : -1);

                ParticleBuilder.create(EBParticles.FLASH).entity(player).pos(0, player.getBbHeight() / 2, 0)
                        .time(6).velocity(rand.nextGaussian() / 40, rand.nextDouble() / 40, rand.nextGaussian() / 40)
                        .color(0, 0, 0).collide(false).scale(2F).spawn(player.level());

                ParticleBuilder.create(EBParticles.FLASH).entity(player).pos(x, player.getBbHeight() / 2 + y, z)
                        .time(6 + rand.nextInt(5)).velocity(rand.nextGaussian() / 40, rand.nextDouble() / 40, rand.nextGaussian() / 40)
                        .color(0, 0, 0).collide(false).scale(1F).spawn(player.level());
            }
        } else {
            for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(EXTRA_HIT_MARGIN))) {
                if (target != player && !isImmuneToWither(target)) {
                    target.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_DURATION));
                }
            }
        }

        charge--;
        flags.putInt(CHARGE_TAG, charge);
        player.setData(ASAttachments.PLAYER_DATA, flags);
        if (charge == 0) {
            if (!player.hasEffect(MobEffects.INVISIBILITY)) player.setInvisible(false);
            player.setInvulnerable(false);
        }
    }

    private static boolean isImmuneToWither(LivingEntity entity) {
        return entity.hasEffect(MobEffects.WITHER) || entity instanceof WitherSkeleton || entity instanceof WitherBoss;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack stack, @NotNull ItemStack other) {
        return false;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }
}
