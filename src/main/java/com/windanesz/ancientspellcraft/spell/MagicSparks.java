package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/** Faiscas magicas cosmeticas do aprendiz de sage (1.12.2 MagicSparks). */
public class MagicSparks extends Spell implements ClassSpell {

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.world().isClientSide) {
            for (int i = 0; i < 10; i++) {
                ParticleBuilder.create(EBParticles.SPARK)
                        .pos(ctx.caster().getX() + ctx.world().random.nextGaussian() * 0.5,
                                ctx.caster().getEyeY() + ctx.world().random.nextGaussian() * 0.3,
                                ctx.caster().getZ() + ctx.world().random.nextGaussian() * 0.5)
                        .spawn(ctx.world());
            }
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
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
