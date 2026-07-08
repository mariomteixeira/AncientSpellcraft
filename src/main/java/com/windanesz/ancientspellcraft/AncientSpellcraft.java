package com.windanesz.ancientspellcraft;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(AncientSpellcraft.MODID)
public class AncientSpellcraft {

    public static final String MODID = "ancientspellcraft";

    public AncientSpellcraft(IEventBus modBus, ModContainer container) {
        com.windanesz.ancientspellcraft.registry.ASItems.ITEMS.register(modBus);
        com.windanesz.ancientspellcraft.registry.ASItems.CREATIVE_TABS.register(modBus);
        com.windanesz.ancientspellcraft.registry.ASEffects.EFFECTS.register(modBus);
        com.windanesz.ancientspellcraft.registry.ASBlocks.BLOCKS.register(modBus);
        com.windanesz.ancientspellcraft.registry.ASBlocks.BLOCK_ENTITIES.register(modBus);
    }
}
