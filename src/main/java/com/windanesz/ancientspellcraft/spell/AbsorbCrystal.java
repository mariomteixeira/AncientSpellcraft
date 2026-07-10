package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.content.item.CrystalItem;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Absorve um cristal elemental do offhand: guarda o ELEMENTO absorvido (1.12.2 AbsorbCrystal).
 * Com ring_absorb_crystal, blocos de cristal do Redux também servem. */
public class AbsorbCrystal extends WarlockChannelSpell {

    @Override
    protected boolean isValidOffhand(net.minecraft.world.entity.player.Player caster, ItemStack stack) {
        if (stack.getItem() instanceof CrystalItem) return true;
        return isCrystalBlock(stack) && com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(
                caster, com.windanesz.ancientspellcraft.registry.ASItems.RING_ABSORB_CRYSTAL.get());
    }

    static boolean isCrystalBlock(ItemStack stack) {
        return stack.getItem() instanceof net.minecraft.world.item.BlockItem
                && BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals("ebwizardry")
                && BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().startsWith("crystal_block");
    }

    @Override
    protected String invalidMessage() {
        return "spell.ancientspellcraft.absorb_crystal.no_crystal";
    }

    @Override
    protected boolean absorb(PlayerCastContext ctx, ItemStack offhand) {
        var tag = ctx.caster().getData(ASAttachments.WARLOCK_DATA);
        String path = BuiltInRegistries.ITEM.getKey(offhand.getItem()).getPath();
        String element = path.startsWith("crystal_block")
                ? path.replace("crystal_block_", "").replace("crystal_block", "magic")
                : path.replace("magic_crystal_", "").replace("magic_crystal", "magic");
        tag.putString("Element", element);
        // 1.12.2: bloco de cristal absorvido da +10% de potencia (cristal comum +5%)
        tag.putBoolean("ElementBlock", isCrystalBlock(offhand));
        ctx.caster().setData(ASAttachments.WARLOCK_DATA, tag);
        ctx.caster().displayClientMessage(Component.translatable("spell.ancientspellcraft.absorb_crystal.absorbed",
                offhand.getHoverName()), true);
        offhand.shrink(1);
        return true;
    }
}
