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
}
