package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.item.ICastItem;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.content.spell.abstr.MinionSpell;
import com.windanesz.ancientspellcraft.entity.living.AnimatedItemEntity;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;

/**
 * animate_weapon/animate_item (1.12.2 Animate): anima o item do offhand.
 * TODO devoritium (aviso) e sage tome/armadura (AS-6/7).
 */
public class AnimateSpell extends MinionSpell<AnimatedItemEntity> {

    private final boolean weaponVariant;
    private final String name;

    public AnimateSpell(String name, boolean weaponVariant) {
        super(level -> new AnimatedItemEntity(ASEntities.ANIMATED_ITEM.get(), level));
        this.name = name;
        this.weaponVariant = weaponVariant;
    }

    private static boolean isWeaponLike(ItemStack stack) {
        if (stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof BowItem || stack.getItem() instanceof ICastItem) {
            return true;
        }
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.getPath().matches(".*(sword|cestus|bow|waraxe|spear|lance|battleaxe|blade|knife|axe|javelin|rapier|saber|pike|glaive|club|halberd|mace|katana|dagger).*");
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        ItemStack offhand = ctx.caster().getOffhandItem();
        if (offhand.isEmpty()) {
            if (!ctx.world().isClientSide) {
                ctx.caster().displayClientMessage(Component.translatable("spell.ancientspellcraft." + name + ".no_item"), true);
            }
            return false;
        }
        if (!weaponVariant && isWeaponLike(offhand)) {
            if (!ctx.world().isClientSide) {
                ctx.caster().displayClientMessage(Component.translatable("spell.ancientspellcraft." + name + ".spell_too_weak"), true);
            }
            return false;
        }
        return super.cast(ctx);
    }

    @Override
    protected void addMinionExtras(AnimatedItemEntity minion, CastContext ctx, int alreadySpawned) {
        super.addMinionExtras(minion, ctx, alreadySpawned);
        if (ctx.caster() == null || ctx.caster().getOffhandItem().isEmpty()) return;

        ItemStack stack = ctx.caster().getOffhandItem().copy();
        stack.setCount(1);
        ctx.caster().getOffhandItem().shrink(1);
        minion.setItemInHand(InteractionHand.MAIN_HAND, stack);
        minion.setDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND, 0.0F);

        if (weaponVariant) {
            var attack = minion.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attack != null) {
                attack.addTransientModifier(new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "animate_potency"),
                        ctx.modifiers().get(SpellModifiers.POTENCY) - 1,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }
}
