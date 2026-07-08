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

    private static BlockBehaviour.Properties devoritiumProps() {
        return BlockBehaviour.Properties.of().strength(5.0F, 6.0F)
                .requiresCorrectToolForDrops().sound(net.minecraft.world.level.block.SoundType.METAL);
    }

    public static final Supplier<Block> DEVORITIUM_BLOCK = BLOCKS.register("devoritium_block",
            () -> new Block(devoritiumProps()));

    public static final Supplier<Block> DEVORITIUM_ORE = BLOCKS.register("devoritium_ore",
            () -> new Block(BlockBehaviour.Properties.of().strength(3.0F, 5.0F)
                    .requiresCorrectToolForDrops().sound(net.minecraft.world.level.block.SoundType.STONE)));

    public static final Supplier<Block> DEVORITIUM_GILDED_STONE = BLOCKS.register("devoritium_gilded_stone",
            () -> new Block(BlockBehaviour.Properties.of().strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops().sound(net.minecraft.world.level.block.SoundType.STONE)));

    public static final Supplier<Block> DEVORITIUM_BARS = BLOCKS.register("devoritium_bars",
            () -> new net.minecraft.world.level.block.IronBarsBlock(devoritiumProps().noOcclusion()));

    public static final Supplier<Block> DEVORITIUM_DOOR = BLOCKS.register("devoritium_door",
            () -> new net.minecraft.world.level.block.DoorBlock(net.minecraft.world.level.block.state.properties.BlockSetType.IRON,
                    devoritiumProps().noOcclusion()));

    // Minerios de cristal elemental (dureza 3/resistencia 5; dropam 1-5 cristais do Redux)
    public static final java.util.Map<String, Supplier<Block>> CRYSTAL_ORES = new java.util.LinkedHashMap<>();

    static {
        for (String element : new String[]{"fire", "earth", "healing", "ice", "lightning", "necromancy", "sorcery"}) {
            CRYSTAL_ORES.put(element, BLOCKS.register("crystal_ore_" + element,
                    () -> new Block(BlockBehaviour.Properties.of().strength(3.0F, 5.0F)
                            .requiresCorrectToolForDrops().sound(net.minecraft.world.level.block.SoundType.STONE))));
        }
    }

    public static final Supplier<BlockEntityType<MageLightBlockEntity>> MAGE_LIGHT_BE = BLOCK_ENTITIES.register("mage_light",
            () -> BlockEntityType.Builder.of(MageLightBlockEntity::new, MAGELIGHT.get(), CANDLELIGHT.get()).build(null));

    private ASBlocks() {
    }
}
