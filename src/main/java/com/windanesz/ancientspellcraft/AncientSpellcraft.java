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
        com.windanesz.ancientspellcraft.registry.ASSpells.SPELLS.register(modBus);
        com.windanesz.ancientspellcraft.registry.ASElements.ELEMENTS.register(modBus);
        com.windanesz.ancientspellcraft.registry.ASAttachments.ATTACHMENTS.register(modBus);
        com.windanesz.ancientspellcraft.registry.ASEntities.ENTITIES.register(modBus);
        com.windanesz.ancientspellcraft.registry.ASMenus.MENUS.register(modBus);
        modBus.addListener(com.windanesz.ancientspellcraft.registry.ASEntities::onAttributeCreation);
        modBus.addListener((net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) ->
                event.registrar("1").playToServer(
                        com.windanesz.ancientspellcraft.network.ExtendedReachC2S.TYPE,
                        com.windanesz.ancientspellcraft.network.ExtendedReachC2S.STREAM_CODEC,
                        com.windanesz.ancientspellcraft.network.ExtendedReachC2S::handle));
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.client.ASClientEvents::onLeftClickEmpty);
            modBus.addListener(com.windanesz.ancientspellcraft.client.ASRenderers::onRegisterRenderers);
            modBus.addListener(com.windanesz.ancientspellcraft.client.ASRenderers::onRegisterLayers);
            modBus.addListener(com.windanesz.ancientspellcraft.client.ASRenderers::registerMageRenderers);
            modBus.addListener(com.windanesz.ancientspellcraft.client.ASRenderers::onRegisterScreens);
        }
        com.windanesz.ancientspellcraft.registry.ASBlocks.BLOCKS.register(modBus);
        com.windanesz.ancientspellcraft.registry.ASBlocks.BLOCK_ENTITIES.register(modBus);

        modBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.addListener(ASPotionEvents::onEffectExpired);
        NeoForge.EVENT_BUS.addListener(ASPotionEvents::onTimeKnotExpired);
        NeoForge.EVENT_BUS.addListener(ASPotionEvents::onLivingHeal);
        NeoForge.EVENT_BUS.addListener(ASPotionEvents::onLivingDamage);
        NeoForge.EVENT_BUS.addListener(ASPotionEvents::onEffectApplicable);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASSpellEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASWarlockEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASWorldgenEvents::onLootTableLoad);
        modBus.addListener(com.windanesz.ancientspellcraft.handler.ASWorldgenEvents::onRegisterSpawnPlacements);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        WizardryEventBus.getInstance().register(SpellCastEvent.Pre.class, ASPotionEvents::onSpellCastPre);
        WizardryEventBus.getInstance().register(SpellCastEvent.Pre.class,
                com.windanesz.ancientspellcraft.handler.ASSpellEvents::onClassSpellCastPre);
        WizardryEventBus.getInstance().register(SpellCastEvent.Pre.class,
                com.windanesz.ancientspellcraft.handler.ASWarlockEvents::onSpellCastPre);
        WizardryEventBus.getInstance().register(SpellCastEvent.Pre.class,
                com.windanesz.ancientspellcraft.handler.ASMetamagicEvents::onSpellCastPre);
        NeoForge.EVENT_BUS.addListener(net.neoforged.bus.api.EventPriority.LOWEST,
                com.windanesz.ancientspellcraft.handler.ASMetamagicEvents::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASContingencyEvents::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASRunewordEvents::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASSpellEvents::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASContingencyEvents::onLivingFall);
        NeoForge.EVENT_BUS.addListener(net.neoforged.bus.api.EventPriority.HIGHEST,
                com.windanesz.ancientspellcraft.handler.ASContingencyEvents::onLivingDeath);
    }
}
