package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.util.CastItemDataHelper;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.content.spell.abstr.MinionSpell;
import com.windanesz.ancientspellcraft.entity.living.AnimatedItemEntity;
import com.windanesz.ancientspellcraft.item.SageTomeItem;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Awaken Tome (1.12.2 AwakenTome, SAGE): anima o tomo da mainhand — vira um minion que casta as
 * spells do tomo; o caster recebe o tome_controller (right-click chama de volta, bater redireciona
 * o alvo, sneak com tome_warp gravado troca de lugar com o tomo). Recast também chama de volta.
 */
public class AwakenTomeSpell extends MinionSpell<AnimatedItemEntity> implements ClassSpell {

    public static final String TOME_UUID_TAG = "AwakenedTomeUUID";

    public AwakenTomeSpell() {
        super(level -> new AnimatedItemEntity(ASEntities.ANIMATED_ITEM.get(), level));
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (!(caster.getMainHandItem().getItem() instanceof SageTomeItem)) return false;

        AnimatedItemEntity oldTome = getTome(caster);
        if (oldTome != null) {
            if (!ctx.world().isClientSide) recallTome(caster, oldTome);
            this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
            return true;
        }
        return super.cast(ctx);
    }

    @Override
    protected void addMinionExtras(AnimatedItemEntity minion, CastContext ctx, int alreadySpawned) {
        super.addMinionExtras(minion, ctx, alreadySpawned);
        if (!(ctx.caster() instanceof Player caster)) return;
        ItemStack tome = caster.getMainHandItem();
        if (!(tome.getItem() instanceof SageTomeItem)) return;

        minion.setItemInHand(InteractionHand.MAIN_HAND, tome.copyWithCount(1));
        minion.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        caster.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ASItems.TOME_CONTROLLER.get()));

        var tag = caster.getData(ASAttachments.PLAYER_DATA);
        tag.putUUID(TOME_UUID_TAG, minion.getUUID());
        caster.setData(ASAttachments.PLAYER_DATA, tag);
    }

    public static AnimatedItemEntity getTome(Player caster) {
        if (!(caster.level() instanceof ServerLevel serverLevel)) return null;
        var tag = caster.getData(ASAttachments.PLAYER_DATA);
        if (!tag.hasUUID(TOME_UUID_TAG)) return null;
        return serverLevel.getEntity(tag.getUUID(TOME_UUID_TAG)) instanceof AnimatedItemEntity tome ? tome : null;
    }

    /** Chama o tomo de volta: a entidade dropa o tomo ao morrer e o controller é consumido. */
    public static void recallTome(Player caster, AnimatedItemEntity tome) {
        tome.discard();
        var tag = caster.getData(ASAttachments.PLAYER_DATA);
        tag.remove(TOME_UUID_TAG);
        caster.setData(ASAttachments.PLAYER_DATA, tag);
        ItemStack controller = new ItemStack(ASItems.TOME_CONTROLLER.get());
        for (int i = 0; i < caster.getInventory().getContainerSize(); i++) {
            if (caster.getInventory().getItem(i).is(controller.getItem())) {
                caster.getInventory().removeItem(i, 1);
                break;
            }
        }
    }

    /** Right-click do tome_controller: sneak + tome_warp gravado no tomo animado = troca de lugar; senão recall. */
    public static InteractionResultHolder<ItemStack> handleControllerUse(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.pass(stack);

        AnimatedItemEntity tome = getTome(player);
        if (tome == null) return InteractionResultHolder.pass(stack);

        boolean hasWarp = CastItemDataHelper.getSpells(tome.getMainHandItem()).stream()
                .anyMatch(s -> s instanceof TomeWarpSpell);
        if (player.isShiftKeyDown() && hasWarp && !player.getCooldowns().isOnCooldown(stack.getItem())) {
            Vec3 playerPos = player.position();
            Vec3 tomePos = tome.position();
            player.teleportTo(tomePos.x, tomePos.y, tomePos.z);
            tome.teleportTo(playerPos.x, playerPos.y, playerPos.z);
            player.getCooldowns().addCooldown(stack.getItem(), 200);
            return InteractionResultHolder.success(stack);
        }

        recallTome(player, tome);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.SAGE;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.MYSTIC_SPELL_BOOK.get();
    }
}
