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
        modBus.addListener((net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) -> {
            var registrar = event.registrar("1");
            registrar.playToServer(
                    com.windanesz.ancientspellcraft.network.ExtendedReachC2S.TYPE,
                    com.windanesz.ancientspellcraft.network.ExtendedReachC2S.STREAM_CODEC,
                    com.windanesz.ancientspellcraft.network.ExtendedReachC2S::handle);
            registrar.playToClient(
                    com.windanesz.ancientspellcraft.network.KnownRitualsS2C.TYPE,
                    com.windanesz.ancientspellcraft.network.KnownRitualsS2C.STREAM_CODEC,
                    com.windanesz.ancientspellcraft.network.KnownRitualsS2C::handle);
            registrar.playToServer(
                    com.windanesz.ancientspellcraft.network.WarlockCastC2S.TYPE,
                    com.windanesz.ancientspellcraft.network.WarlockCastC2S.STREAM_CODEC,
                    com.windanesz.ancientspellcraft.network.WarlockCastC2S::handle);
        });
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.client.ASClientEvents::onLeftClickEmpty);
            NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.client.ASCameraClientHandler::onClientTick);
            NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.client.ASCameraClientHandler::onComputeFovModifier);
            modBus.addListener(com.windanesz.ancientspellcraft.client.ASRenderers::onRegisterRenderers);
            modBus.addListener(com.windanesz.ancientspellcraft.client.ASRenderers::onRegisterLayers);
            modBus.addListener(com.windanesz.ancientspellcraft.client.ASRenderers::registerMageRenderers);
            modBus.addListener(com.windanesz.ancientspellcraft.client.ASRenderers::onRegisterScreens);
            modBus.addListener(com.windanesz.ancientspellcraft.client.ASKeyBindings::onRegisterKeyMappings);
            modBus.addListener(com.windanesz.ancientspellcraft.client.ASRenderers::onClientSetup);
            NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.client.ASKeyBindings::onClientTick);
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
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASSpellEvents::onProjectileImpact);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASWarlockEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASWorldgenEvents::onLootTableLoad);
        modBus.addListener(com.windanesz.ancientspellcraft.handler.ASWorldgenEvents::onRegisterSpawnPlacements);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASDiscoveryEvents::onRightClickItem);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASDiscoveryEvents::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASDiscoveryEvents::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASDiscoveryEvents::onPlayerChangedDimension);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        WizardryEventBus.getInstance().register(SpellCastEvent.Pre.class, ASPotionEvents::onSpellCastPre);
        WizardryEventBus.getInstance().register(SpellCastEvent.Pre.class,
                com.windanesz.ancientspellcraft.handler.ASSpellEvents::onClassSpellCastPre);
        WizardryEventBus.getInstance().register(SpellCastEvent.Pre.class,
                com.windanesz.ancientspellcraft.handler.ASWarlockEvents::onSpellCastPre);
        WizardryEventBus.getInstance().register(SpellCastEvent.Pre.class,
                com.windanesz.ancientspellcraft.handler.ASMetamagicEvents::onSpellCastPre);
        WizardryEventBus.getInstance().register(SpellCastEvent.Post.class,
                com.windanesz.ancientspellcraft.handler.ASSpellEvents::onSpellCastPost);
        WizardryEventBus.getInstance().register(SpellCastEvent.Pre.class,
                com.windanesz.ancientspellcraft.handler.ASSpellEvents::onJewelSetBonus);
        WizardryEventBus.getInstance().register(SpellCastEvent.Pre.class,
                com.windanesz.ancientspellcraft.handler.ASSpellEvents::onManaBatteryTransfer);
        NeoForge.EVENT_BUS.addListener(ASPotionEvents::onPendantBuffExpired);
        NeoForge.EVENT_BUS.addListener(net.neoforged.bus.api.EventPriority.LOWEST,
                com.windanesz.ancientspellcraft.handler.ASMetamagicEvents::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASContingencyEvents::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASRunewordEvents::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASSpellEvents::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASSpellEvents::onShieldBlock);
        NeoForge.EVENT_BUS.addListener(com.windanesz.ancientspellcraft.handler.ASContingencyEvents::onLivingFall);
        NeoForge.EVENT_BUS.addListener(net.neoforged.bus.api.EventPriority.HIGHEST,
                com.windanesz.ancientspellcraft.handler.ASContingencyEvents::onLivingDeath);
    }
}
