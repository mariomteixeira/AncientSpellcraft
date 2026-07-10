package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.core.config.EBServerConfig;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

/**
 * Static Charge (1.12.2 StaticCharge): imbui a primeira espada da hotbar/offhand com carga
 * elétrica por um tempo — cada golpe dela dá +2 de dano por nível (leitura em ASSpellEvents).
 * Nível cresce com a potência do cast (1 + (potency-1)/potency_per_tier). Desvio documentado:
 * sem o enchantment ASEnchantments (1.21 é data-driven) — a carga vive no CustomData do item,
 * sem glint; expira sozinha.
 */
public class StaticChargeSpell extends Spell {

    public static final String LEVEL_TAG = "StaticChargeLevel";
    public static final String EXPIRY_TAG = "StaticChargeExpiry";

    @Override
    public boolean cast(PlayerCastContext ctx) {
        var caster = ctx.caster();
        for (int slot = -1; slot < 9; slot++) {
            ItemStack stack = slot == -1 ? caster.getOffhandItem() : caster.getInventory().getItem(slot);
            if (!(stack.getItem() instanceof SwordItem)) continue;
            var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (tag.getLong(EXPIRY_TAG) > ctx.world().getGameTime()) continue; // já carregada

            if (!ctx.world().isClientSide) {
                float potency = ctx.modifiers().get(SpellModifiers.POTENCY);
                int level = potency == 1.0F ? 1
                        : (int) ((potency - 1.0F) / EBServerConfig.POTENCY_INCREASE_PER_TIER.get() + 0.5F);
                int duration = (int) (this.property("effect_duration", 1200)
                        * ctx.modifiers().get(SpellModifiers.DURATION));
                CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
                    t.putInt(LEVEL_TAG, Math.max(1, level));
                    t.putLong(EXPIRY_TAG, ctx.world().getGameTime() + duration);
                });
            } else {
                for (int i = 0; i < 10; i++) {
                    ParticleBuilder.create(EBParticles.SPARKLE, caster).color(0xe5b3ff)
                            .pos(0, 1 + ctx.world().random.nextDouble(), 0).time(20).spawn(ctx.world());
                }
            }
            this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
            return true;
        }
        return false;
    }

    private int property(String key, int fallback) {
        var prop = com.koomplo.wizardry.api.content.spell.properties.SpellProperty.intProperty(key, fallback);
        return this.property(prop);
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
