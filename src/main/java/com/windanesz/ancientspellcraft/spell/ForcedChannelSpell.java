package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.item.ICastItem;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.CastItemDataHelper;
import com.koomplo.wizardry.content.entity.living.AbstractWizard;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.setup.registries.Spells;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * Forced Channel (1.12.2 ForcedChannel, SAGE): ray num alvo com cast item — VOCÊ casta a spell
 * atual dele (tier até max_tier, class spells nunca). Wizards NPC canalizam uma spell fixa
 * sorteada pelo UUID. Desvio: sem o cooldown de 80t no item do 1.12.2 (o cooldown da própria
 * forced_channel cobre).
 */
public class ForcedChannelSpell extends ASRaySpell implements ClassSpell {

    public static final SpellProperty<Integer> MAX_TIER = SpellProperty.intProperty("max_tier", 1);

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (ctx.world().isClientSide) return true;
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return false;
        if (!(ctx.caster() instanceof Player caster)) return false;

        Spell spell = spellFromTarget(target);
        if (spell == null || spell == Spells.NONE) return false;

        if (spell.getTier().getLevel() > property(MAX_TIER)) {
            caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.forced_channel.too_strong_spell"), true);
            return false;
        }
        if (spell instanceof ClassSpell) return false;

        return spell.cast(new PlayerCastContext(ctx.world(), caster, InteractionHand.MAIN_HAND, 0, new SpellModifiers()));
    }

    /** Spell atual do cast item do alvo; wizards NPC dão sempre a mesma spell (seed do UUID). */
    static Spell spellFromTarget(LivingEntity target) {
        if (target instanceof AbstractWizard wizard) {
            var spells = wizard.getSpells();
            if (spells.isEmpty()) return null;
            Random seeded = new Random(Math.abs(wizard.getUUID().hashCode()));
            return spells.get(Math.min(1, seeded.nextInt(spells.size())));
        }
        ItemStack held = target.getMainHandItem();
        if (!(held.getItem() instanceof ICastItem)) held = target.getOffhandItem();
        if (!(held.getItem() instanceof ICastItem)) return null;
        return CastItemDataHelper.getCurrentSpell(held);
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
