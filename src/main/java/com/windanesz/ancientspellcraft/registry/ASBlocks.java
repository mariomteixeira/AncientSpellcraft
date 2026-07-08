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

    public static final Supplier<Block> ARCANE_FLAME = BLOCKS.register("arcane_flame",
            () -> new com.windanesz.ancientspellcraft.block.ArcaneFlameBlock(BlockBehaviour.Properties.of()
                    .noCollission().instabreak().lightLevel(state -> 15).noLootTable()
                    .sound(net.minecraft.world.level.block.SoundType.WOOL).pushReaction(PushReaction.DESTROY)));

    public static final Supplier<Block> NETHER_FIRE = BLOCKS.register("nether_fire",
            () -> new com.windanesz.ancientspellcraft.block.NetherFireBlock(BlockBehaviour.Properties.of()
                    .noCollission().instabreak().lightLevel(state -> 15).noLootTable()
                    .sound(net.minecraft.world.level.block.SoundType.WOOL).pushReaction(PushReaction.DESTROY)));

    public static final Supplier<Block> CONJURED_MAGMA = BLOCKS.register("conjured_magma",
            () -> new com.windanesz.ancientspellcraft.block.TemporaryBlock(BlockBehaviour.Properties.of()
                    .strength(0.5F).lightLevel(state -> 3).noLootTable()
                    .sound(net.minecraft.world.level.block.SoundType.NETHERRACK)) {
                @Override
                public void stepOn(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
                                   net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.entity.Entity entity) {
                    if (!entity.isSteppingCarefully() && entity instanceof net.minecraft.world.entity.LivingEntity living
                            && !living.fireImmune()) {
                        entity.hurt(level.damageSources().hotFloor(), 1.0F);
                    }
                    super.stepOn(level, pos, state, entity);
                }
            });

    public static final Supplier<Block> CONJURED_DIRT = BLOCKS.register("conjured_dirt",
            () -> new com.windanesz.ancientspellcraft.block.TemporaryBlock(BlockBehaviour.Properties.of()
                    .strength(0.5F).noLootTable().sound(net.minecraft.world.level.block.SoundType.GRAVEL)));

    public static final Supplier<Block> CONJURED_SNOW = BLOCKS.register("conjured_snow",
            () -> new com.windanesz.ancientspellcraft.block.TemporaryBlock(BlockBehaviour.Properties.of()
                    .strength(0.2F).noLootTable().sound(net.minecraft.world.level.block.SoundType.SNOW)));

    public static final Supplier<Block> QUICKSAND = BLOCKS.register("quicksand",
            () -> new com.windanesz.ancientspellcraft.block.QuicksandBlock(BlockBehaviour.Properties.of()
                    .strength(0.5F).noLootTable().sound(net.minecraft.world.level.block.SoundType.SAND)));

    public static final Supplier<Block> LIGHTNING_BLOCK = BLOCKS.register("lightning_block",
            () -> new com.windanesz.ancientspellcraft.block.LightningBlock(BlockBehaviour.Properties.of()
                    .noCollission().instabreak().lightLevel(state -> 10).noLootTable()
                    .pushReaction(PushReaction.DESTROY)));

    /** Cogumelos magicos (1.12.2 mushroom_<tipo>); sem block item, so via spells. */
    public static final java.util.Map<String, Supplier<Block>> MUSHROOMS = new java.util.LinkedHashMap<>();

    static {
        record M(String name, com.windanesz.ancientspellcraft.block.MagicMushroomBlock.Type type) {}
        for (var m : new M[]{
                new M("mushroom_poison", com.windanesz.ancientspellcraft.block.MagicMushroomBlock.Type.POISON),
                new M("mushroom_ice", com.windanesz.ancientspellcraft.block.MagicMushroomBlock.Type.ICE),
                new M("mushroom_fire", com.windanesz.ancientspellcraft.block.MagicMushroomBlock.Type.FIRE),
                new M("mushroom_wither", com.windanesz.ancientspellcraft.block.MagicMushroomBlock.Type.WITHER),
                new M("mushroom_force", com.windanesz.ancientspellcraft.block.MagicMushroomBlock.Type.FORCE),
                new M("mushroom_healing", com.windanesz.ancientspellcraft.block.MagicMushroomBlock.Type.HEALING),
                new M("mushroom_shocking", com.windanesz.ancientspellcraft.block.MagicMushroomBlock.Type.SHOCKING),
                new M("mushroom_mind", com.windanesz.ancientspellcraft.block.MagicMushroomBlock.Type.MIND),
                new M("mushroom_cleansing", com.windanesz.ancientspellcraft.block.MagicMushroomBlock.Type.CLEANSING),
                new M("mushroom_explosive", com.windanesz.ancientspellcraft.block.MagicMushroomBlock.Type.EXPLOSIVE),
                new M("mushroom_empowering", com.windanesz.ancientspellcraft.block.MagicMushroomBlock.Type.EMPOWERING)}) {
            MUSHROOMS.put(m.name(), BLOCKS.register(m.name(),
                    () -> new com.windanesz.ancientspellcraft.block.MagicMushroomBlock(BlockBehaviour.Properties.of()
                            .strength(0.2F).noCollission().randomTicks().noLootTable()
                            .sound(net.minecraft.world.level.block.SoundType.GRASS)
                            .pushReaction(PushReaction.DESTROY), m.type())));
        }
    }

    public static final Supplier<BlockEntityType<com.windanesz.ancientspellcraft.block.TemporaryBlockEntity>> TEMPORARY_BE =
            BLOCK_ENTITIES.register("temporary_block", () -> BlockEntityType.Builder.of(
                    com.windanesz.ancientspellcraft.block.TemporaryBlockEntity::new,
                    java.util.stream.Stream.concat(
                            java.util.stream.Stream.of(ARCANE_FLAME.get(), CONJURED_MAGMA.get(), CONJURED_DIRT.get(),
                                    CONJURED_SNOW.get(), QUICKSAND.get(), LIGHTNING_BLOCK.get()),
                            MUSHROOMS.values().stream().map(Supplier::get)).toArray(Block[]::new)).build(null));

    public static final Supplier<BlockEntityType<MageLightBlockEntity>> MAGE_LIGHT_BE = BLOCK_ENTITIES.register("mage_light",
            () -> BlockEntityType.Builder.of(MageLightBlockEntity::new, MAGELIGHT.get(), CANDLELIGHT.get()).build(null));

    private ASBlocks() {
    }
}
