package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.block.MageLightBlock;
import com.windanesz.ancientspellcraft.block.MageLightBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ASBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AncientSpellcraft.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AncientSpellcraft.MODID);

    private static BlockBehaviour.Properties lightProps() {
        return BlockBehaviour.Properties.of().noCollission().noOcclusion().air()
                .lightLevel(state -> 15).pushReaction(PushReaction.DESTROY).noLootTable();
    }

    public static final Supplier<Block> MAGELIGHT = BLOCKS.register("magelight",
            () -> new MageLightBlock(lightProps(), () -> ASEffects.MAGELIGHT));

    public static final Supplier<Block> CANDLELIGHT = BLOCKS.register("candlelight",
            () -> new MageLightBlock(lightProps(), () -> ASEffects.CANDLELIGHT));

    public static final Supplier<Block> HARD_FROSTED_ICE = BLOCKS.register("hard_frosted_ice",
            () -> new com.windanesz.ancientspellcraft.block.HardFrostedIceBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F).friction(0.98F).sound(net.minecraft.world.level.block.SoundType.GLASS).noLootTable()));

    public static final Supplier<BlockEntityType<MageLightBlockEntity>> MAGE_LIGHT_BE = BLOCK_ENTITIES.register("mage_light",
            () -> BlockEntityType.Builder.of(MageLightBlockEntity::new, MAGELIGHT.get(), CANDLELIGHT.get()).build(null));

    private ASBlocks() {
    }
}
