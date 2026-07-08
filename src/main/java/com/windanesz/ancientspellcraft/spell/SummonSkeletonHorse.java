package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.content.spell.abstr.MinionSpell;
import com.windanesz.ancientspellcraft.entity.living.SkeletonHorseMinion;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Invoca um cavalo esqueleto domado, selado e mais rapido por potency (1.12.2 SummonSkeletonHorse). */
public class SummonSkeletonHorse extends MinionSpell<SkeletonHorseMinion> {

    public SummonSkeletonHorse() {
        super(level -> new SkeletonHorseMinion(ASEntities.SKELETON_HORSE_MINION.get(), level));
    }

    @Override
    protected void addMinionExtras(SkeletonHorseMinion minion, CastContext ctx, int alreadySpawned) {
        super.addMinionExtras(minion, ctx, alreadySpawned);
        if (ctx.caster() instanceof Player player) {
            minion.setTamed(true);
            minion.setOwnerUUID(player.getUUID());
            minion.equipSaddle(new ItemStack(Items.SADDLE), null);
            var speed = minion.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                speed.addTransientModifier(new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "horse_potency"),
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
