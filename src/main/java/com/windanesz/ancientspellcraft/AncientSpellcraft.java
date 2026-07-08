package com.windanesz.ancientspellcraft;

import com.koomplo.wizardry.api.content.event.SpellCastEvent;
import com.koomplo.wizardry.core.event.WizardryEventBus;
import com.windanesz.ancientspellcraft.handler.ASPotionEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(AncientSpellcraft.MODID)
public class AncientSpellcraft {

    public static final String MODID = "ancientspellcraft";

    public AncientSpellcraft(IEventBus modBus, ModContainer container) {
        com.windanesz.ancientspellcraft.registry.ASItems.ITEMS.register(modBus);
        com.windanesz.ancientspellcraft.registry.ASItems.CREATIVE_TABS.register(modBus);
        com.windanesz.ancientspellcraft.registry.ASEffects.EFFECTS.register(modBus);
        com.windanesz.ancientspellcraft.registry.ASBlocks.BLOCKS.register(modBus);
        com.windanesz.ancientspellcraft.registry.ASBlocks.BLOCK_ENTITIES.register(modBus);

        modBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.addListener(ASPotionEvents::onEffectExpired);
        NeoForge.EVENT_BUS.addListener(ASPotionEvents::onLivingHeal);
        NeoForge.EVENT_BUS.addListener(ASPotionEvents::onLivingDamage);
        NeoForge.EVENT_BUS.addListener(ASPotionEvents::onEffectApplicable);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        WizardryEventBus.getInstance().register(SpellCastEvent.Pre.class, ASPotionEvents::onSpellCastPre);
    }
}
