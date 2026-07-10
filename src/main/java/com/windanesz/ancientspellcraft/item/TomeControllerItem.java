package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.entity.living.AnimatedItemEntity;
import com.windanesz.ancientspellcraft.spell.AwakenTomeSpell;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Controlador de tomo (1.12.2 ItemTomeController): recebido ao animar um tomo com awaken_tome.
 * Right-click chama o tomo de volta (devolve o tomo e some); sneak-right-click com tome_warp
 * gravado no tomo animado troca de lugar com ele; bater num alvo redireciona o ataque do tomo.
 */
public class TomeControllerItem extends Item {

    public TomeControllerItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return AwakenTomeSpell.handleControllerUse(level, player, hand);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity wielder) {
        if (wielder instanceof Player player && !player.level().isClientSide) {
            AnimatedItemEntity tome = AwakenTomeSpell.getTome(player);
            if (tome != null) tome.setTarget(target);
        }
        return true;
    }
}
