package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;

/** stone_punch/stone_fist: conjura o punho de pedra na mao principal vazia (1.12.2 StonePunch/StoneFist). */
public class StoneFistSpell extends ASConjureItemSpell {

    private final String name;

    public StoneFistSpell(String name, Item item) {
        super(item);
        this.name = name;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.hand() != InteractionHand.OFF_HAND) {
            if (!ctx.world().isClientSide) {
                ctx.caster().displayClientMessage(Component.translatable("spell.ancientspellcraft." + name + ".wrong_hand"), true);
            }
            return false;
        }
        if (!ctx.caster().getMainHandItem().isEmpty()) {
            if (!ctx.world().isClientSide) {
                ctx.caster().displayClientMessage(Component.translatable("spell.ancientspellcraft." + name + ".full_hand"), true);
            }
            return false;
        }
        return super.cast(ctx);
    }

    /** 1.12.2: o punho vai direto para a MAO PRINCIPAL (nao para o inventario). */
    @Override
    protected boolean conjureItem(com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext ctx) {
        if (!ctx.caster().getMainHandItem().isEmpty()
                && !(ctx.caster().getMainHandItem().getItem() instanceof com.koomplo.wizardry.api.content.item.ICastItem)) {
            return false;
        }
        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
        stack = addItemExtras(ctx, stack);
        var data = com.koomplo.wizardry.core.platform.Services.OBJECT_DATA.getConjureData(stack);
        int duration = (int) (property(com.koomplo.wizardry.content.spell.DefaultProperties.ITEM_LIFETIME)
                * ctx.modifiers().get(com.koomplo.wizardry.api.content.spell.internal.SpellModifiers.DURATION));
        data.setExpireTime(ctx.world().getGameTime() + duration);
        data.setDuration(duration);
        data.setSummoned(true);
        ctx.caster().setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, stack);
        return true;
    }
}
