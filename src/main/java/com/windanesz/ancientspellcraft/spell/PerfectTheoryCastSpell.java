package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.content.ForfeitRegistry;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * Perfect Theory Spell (1.12.2 PerfectTheorySpell): a spell escrita pelo perfect_theory —
 * reproduz o efeito do último experimento gravado (forfeit por id ou efeito de poção).
 */
public class PerfectTheoryCastSpell extends Spell implements ClassSpell {

    public static final String LAST_EXPERIMENT_TAG = "LastExperiment";

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (ctx.world().isClientSide) return true;

        CompoundTag last = caster.getData(ASAttachments.PLAYER_DATA).getCompound(LAST_EXPERIMENT_TAG);
        if (last.isEmpty()) {
            caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.perfect_theory_spell.no_theory"), true);
            return false;
        }
        String type = last.getString("Type");
        String effect = last.getString("Effect");

        if ("forfeit".equals(type)) {
            ResourceLocation id = ResourceLocation.tryParse(effect);
            ForfeitRegistry.getForfeits().stream()
                    .filter(f -> f.getName().equals(id))
                    .findFirst()
                    .ifPresent(f -> f.apply(ctx.world(), caster));
        } else if ("mob_effect".equals(type)) {
            String[] parts = effect.split(",");
            var holder = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(parts[0])).orElse(null);
            if (holder != null) {
                caster.addEffect(new MobEffectInstance(holder, Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
            }
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.SAGE;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.MYSTIC_SPELL_BOOK.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
