package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.koomplo.wizardry.content.spell.abstr.ProjectileSpell;
import com.koomplo.wizardry.core.config.EBServerConfig;
import com.windanesz.ancientspellcraft.entity.ChaosOrbEntity;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.item.Item;

/** Orbe do caos (1.12.2 ChaosOrb): quanto mais potency, mais geracoes de divisao. */
public class ChaosOrbSpell extends ProjectileSpell<ChaosOrbEntity> implements ClassSpell {

    public static final SpellProperty<Integer> MAX_SPLIT_GENERATIONS = SpellProperty.intProperty("max_split_generations");

    public ChaosOrbSpell() {
        super(level -> new ChaosOrbEntity(ASEntities.CHAOS_ORB.get(), level));
    }

    @Override
    protected void addProjectileExtras(CastContext ctx, ChaosOrbEntity projectile) {
        super.addProjectileExtras(ctx, projectile);
        int gen = Math.min(property(MAX_SPLIT_GENERATIONS),
                (int) ((ctx.modifiers().get(SpellModifiers.POTENCY) - 1) / EBServerConfig.POTENCY_INCREASE_PER_TIER.get() + 0.01f) - 1);
        projectile.setGeneration(gen);
        projectile.setElement(elementOrMagic(ctx.caster()));
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.WARLOCK;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.FORBIDDEN_TOME.get();
    }
}
