package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.CastItemDataHelper;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.setup.registries.EBDataComponents;
import com.koomplo.wizardry.setup.registries.Spells;
import com.windanesz.ancientspellcraft.item.SageTomeItem;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Thoughtsteal (1.12.2 Thoughtsteal, SAGE): ray num alvo com cast item — copia a spell atual dele
 * para o slot SELECIONADO do seu sage tome na mainhand (tier até max_tier, class spells nunca).
 */
public class ThoughtstealSpell extends ASRaySpell implements ClassSpell {

    public static final SpellProperty<Integer> MAX_TIER = SpellProperty.intProperty("max_tier", 1);

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (ctx.world().isClientSide) return true;
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return false;
        if (!(ctx.caster() instanceof Player caster)) return false;

        ItemStack tome = caster.getMainHandItem();
        if (!(tome.getItem() instanceof SageTomeItem)) return false;

        Spell spell = ForcedChannelSpell.spellFromTarget(target);
        if (spell == null || spell == Spells.NONE) return false;

        if (spell.getTier().getLevel() > property(MAX_TIER)) {
            caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.thoughtsteal.too_strong_spell"), true);
            return false;
        }
        if (spell instanceof ClassSpell) return false;

        List<Spell> spells = new ArrayList<>(CastItemDataHelper.getSpells(tome));
        if (spells.isEmpty()) return false;
        int selected = tome.getOrDefault(EBDataComponents.SELECTED_SPELL.get(), 0);
        if (selected < 0 || selected >= spells.size()) return false;
        spells.set(selected, spell);
        CastItemDataHelper.setSpells(tome, spells);
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
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
