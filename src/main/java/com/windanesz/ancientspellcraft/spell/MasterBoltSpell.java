package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Master Bolt (1.12.2 MasterBolt): com o item raio no inventário, castar consome e dispara o
 * raio-projétil (vira bloco onde cai); sem o item, castar puxa você como um raio até o bloco
 * (countdown de 20t no attachment, lido em ASSpellEvents); sneak-cast na mesma dimensão
 * recolhe o bloco e devolve o item.
 */
public class MasterBoltSpell extends Spell {

    public static final String LOCATION_TAG = "MasterBoltPos";
    public static final String DIMENSION_TAG = "MasterBoltDim";
    public static final String COUNTDOWN_TAG = "MasterBoltCountdown";

    public static void storeLocation(Player player, BlockPos pos) {
        CompoundTag tag = player.getData(ASAttachments.PLAYER_DATA);
        tag.put(LOCATION_TAG, NbtUtils.writeBlockPos(pos));
        tag.putString(DIMENSION_TAG, player.level().dimension().location().toString());
        player.setData(ASAttachments.PLAYER_DATA, tag);
        player.displayClientMessage(Component.translatable("spell.ancientspellcraft.master_bolt.remember"), true);
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (ctx.world().isClientSide) return true;
        CompoundTag tag = caster.getData(ASAttachments.PLAYER_DATA);

        if (!caster.isShiftKeyDown() && consumeBoltItem(caster)) {
            // dispara o raio
            caster.playSound(SoundEvents.TRIDENT_THROW.value(), 0.5F, 0.4F / (ctx.world().random.nextFloat() * 0.4F + 0.8F));
            var bolt = new com.windanesz.ancientspellcraft.entity.projectile.MasterBoltEntity(
                    ASEntities.MASTER_BOLT.get(), ctx.world());
            float speed = caster.onGround() ? 1.5F : 4.5F;
            bolt.aim(caster, speed);
            ctx.world().addFreshEntity(bolt);
            caster.getCooldowns().addCooldown(ASItems.MASTER_BOLT.get(), 20);
            return true;
        }

        if (!tag.contains(LOCATION_TAG)) return false;
        BlockPos destination = NbtUtils.readBlockPos(tag, LOCATION_TAG).orElse(null);
        if (destination == null) return false;
        boolean sameDimension = caster.level().dimension().location().toString().equals(tag.getString(DIMENSION_TAG));

        if (caster.isShiftKeyDown()) {
            // recolhe o bolt
            if (sameDimension && caster.level().getBlockState(destination).is(ASBlocks.MASTER_BOLT.get())) {
                caster.level().removeBlock(destination, false);
                tag.remove(LOCATION_TAG);
                tag.remove(DIMENSION_TAG);
                caster.setData(ASAttachments.PLAYER_DATA, tag);
                if (!caster.getInventory().contains(new ItemStack(ASItems.MASTER_BOLT.get()))) {
                    caster.getInventory().placeItemBackInInventory(new ItemStack(ASItems.MASTER_BOLT.get()));
                }
                return true;
            }
            return false;
        }

        if (!sameDimension) return false;
        tag.putInt(COUNTDOWN_TAG, 20);
        caster.setData(ASAttachments.PLAYER_DATA, tag);
        caster.level().playSound(null, caster.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.6F, 1.4F);
        return true;
    }

    private static boolean consumeBoltItem(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ASItems.MASTER_BOLT.get())) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
