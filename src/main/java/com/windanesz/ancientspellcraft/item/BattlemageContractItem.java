package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.windanesz.ancientspellcraft.entity.living.ClassWizard;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Contrato do battlemage (1.12.2 ItemBattlemageContract): usado num class wizard battlemage
 * amigável, contrata-o como mercenário por 7 dias (segue e defende o contratante). Desvio: o
 * sistema Covenant do 1.12.2 (aliança única por jogador) virou o vínculo direto no wizard.
 */
public class BattlemageContractItem extends Item {

    private static final int MERCENARY_DURATION = 168000; // 7 dias (1.12.2)

    public BattlemageContractItem() {
        super(new Properties().stacksTo(16).rarity(Rarity.RARE));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player,
                                                           @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (target instanceof ClassWizard wizard && wizard.getArmourClass() == WizardArmorType.BATTLEMAGE) {
            if (!player.level().isClientSide) {
                wizard.setMercenary(player.getUUID(), MERCENARY_DURATION);
                player.displayClientMessage(Component.translatable(
                        "spell.ancientspellcraft.covenant.following", wizard.getDisplayName()), true);
                if (!player.isCreative()) stack.shrink(1);
            }
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.ancientspellcraft.battlemage_contract.desc")
                .withStyle(ChatFormatting.GRAY));
    }
}
