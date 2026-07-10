package com.windanesz.ancientspellcraft.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * Wizard Tankard (1.12.2 ItemWizardTankard): enche sozinho (+1 mana/6s, cap 100); cheio, dá para
 * beber — mana_regeneration VIII por 20s e esvazia.
 */
public class WizardTankardItem extends ManaArtifactItem {

    public WizardTankardItem(Rarity rarity) {
        super(rarity, 100, new com.koomplo.wizardry.core.IArtifactEffect() {
            @Override
            public void onTick(Player player, Level level, ItemStack artifact) {
                if (!level.isClientSide && player.tickCount % 120 == 0
                        && artifact.getItem() instanceof ManaArtifactItem mana && !mana.isManaFull(artifact)) {
                    mana.rechargeMana(artifact, 1);
                }
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // recarga por cristal do pai continua no sneak
        if (player.isShiftKeyDown()) return super.use(level, player, hand);
        if (getMana(stack) >= getManaCapacity(stack)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public net.minecraft.world.item.ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player) {
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    com.windanesz.ancientspellcraft.registry.ASEffects.MANA_REGENERATION, 400, 7));
            setMana(stack, 0);
        }
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }
}
