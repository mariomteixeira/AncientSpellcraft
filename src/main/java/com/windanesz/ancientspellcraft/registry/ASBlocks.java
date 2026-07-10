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

    // AS-8e: natureza/minerios (1.12.2: BlockSageFlax, BlockCrystalLog/Leaves, ores dropam shard/nugget + 1-5 xp)
    public static final Supplier<Block> SAGE_FLAX = BLOCKS.register("sage_flax",
            () -> new com.windanesz.ancientspellcraft.block.SageFlaxBlock(BlockBehaviour.Properties.of()
                    .mapColor(net.minecraft.world.level.material.MapColor.PLANT).noCollission().instabreak()
                    .randomTicks().lightLevel(state -> 1).sound(net.minecraft.world.level.block.SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)));

    public static final Supplier<Block> LOG_CRYSTAL_TREE = BLOCKS.register("log_crystal_tree",
            () -> new net.minecraft.world.level.block.RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(net.minecraft.world.level.material.MapColor.WOOD).strength(2.0F)
                    .sound(net.minecraft.world.level.block.SoundType.WOOD)));

    public static final Supplier<Block> LEAVES_CRYSTAL_TREE = BLOCKS.register("leaves_crystal_tree",
            () -> new net.minecraft.world.level.block.LeavesBlock(BlockBehaviour.Properties.of()
                    .mapColor(net.minecraft.world.level.material.MapColor.PLANT).strength(0.2F).randomTicks()
                    .sound(net.minecraft.world.level.block.SoundType.GRASS).noOcclusion()
                    .isValidSpawn((s, l, p, t) -> false).isSuffocating((s, l, p) -> false)
                    .isViewBlocking((s, l, p) -> false).pushReaction(PushReaction.DESTROY).ignitedByLava()));

    public static final Supplier<Block> ASTRAL_DIAMOND_ORE = BLOCKS.register("astral_diamond_ore",
            () -> new net.minecraft.world.level.block.DropExperienceBlock(
                    net.minecraft.util.valueproviders.UniformInt.of(1, 5),
                    BlockBehaviour.Properties.of().strength(3.0F, 5.0F)
                            .requiresCorrectToolForDrops().sound(net.minecraft.world.level.block.SoundType.STONE)));

    public static final Supplier<Block> CRYSTAL_SILVER_ORE = BLOCKS.register("crystal_silver_ore",
            () -> new net.minecraft.world.level.block.DropExperienceBlock(
                    net.minecraft.util.valueproviders.UniformInt.of(1, 5),
                    BlockBehaviour.Properties.of().strength(3.0F, 5.0F)
                            .requiresCorrectToolForDrops().sound(net.minecraft.world.level.block.SoundType.STONE)));

    // AS-8f: paredes/saída do espaço de bolso (1.12.2: inquebráveis estilo bedrock; luz 12 base, 6 elementais)
    private static BlockBehaviour.Properties boundaryProps(int light) {
        return BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable()
                .lightLevel(state -> light).sound(net.minecraft.world.level.block.SoundType.STONE);
    }

    public static final Supplier<Block> DIMENSION_BOUNDARY = BLOCKS.register("dimension_boundary",
            () -> new Block(boundaryProps(12)));

    /** Variantes elementais (textura = crystal_block do Redux), chave = nome do elemento. */
    public static final java.util.Map<String, Supplier<Block>> DIMENSION_BOUNDARIES = new java.util.LinkedHashMap<>();

    static {
        for (String element : new String[]{"magic", "fire", "ice", "lightning", "necromancy", "earth", "sorcery", "healing"}) {
            DIMENSION_BOUNDARIES.put(element, BLOCKS.register("dimension_boundary_" + element,
                    () -> new Block(boundaryProps(6))));
        }
    }

    public static final Supplier<Block> DIMENSION_FOCUS = BLOCKS.register("dimension_focus",
            () -> new com.windanesz.ancientspellcraft.block.DimensionFocusBlock(boundaryProps(12)));

    public static final Supplier<Block> DIMENSION_FOCUS_GOLD = BLOCKS.register("dimension_focus_gold",
            () -> new com.windanesz.ancientspellcraft.block.DimensionFocusBlock(boundaryProps(6)));

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

    /** Blocos estruturais dos templates 1.12.2 (AS-8c; lógica no AS-8g). */
    public static final Supplier<Block> SEALED_STONE = BLOCKS.register("sealed_stone",
            () -> new com.windanesz.ancientspellcraft.block.SealedStoneBlock(
                    net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
                    .mapColor(net.minecraft.world.level.material.MapColor.STONE).strength(-1.0F, 3600000.0F)
                    .randomTicks().noLootTable().sound(net.minecraft.world.level.block.SoundType.STONE)));
    public static final Supplier<Block> UNSEALED_STONE = BLOCKS.register("unsealed_stone",
            () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
                    .mapColor(net.minecraft.world.level.material.MapColor.STONE).strength(3.0F)));
    // 1.12.2: sentinel_block_iron (5 de vida) e _diamond (20); hardness 10.5, sem harvest level
    public static final Supplier<Block> SENTINEL_BLOCK = BLOCKS.register("sentinel_block",
            () -> new com.windanesz.ancientspellcraft.block.SentinelBlock(
                    net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
                    .mapColor(net.minecraft.world.level.material.MapColor.METAL).strength(10.5F, 10.5F)
                    .noOcclusion().lightLevel(state -> 9)));
    public static final Supplier<Block> SENTINEL_BLOCK_DIAMOND = BLOCKS.register("sentinel_block_diamond",
            () -> new com.windanesz.ancientspellcraft.block.SentinelBlock(
                    net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
                    .mapColor(net.minecraft.world.level.material.MapColor.DIAMOND).strength(10.5F, 10.5F)
                    .noOcclusion().lightLevel(state -> 9)));

    public static final Supplier<BlockEntityType<com.windanesz.ancientspellcraft.block.SentinelBlockEntity>> SENTINEL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("sentinel", () -> BlockEntityType.Builder.of(
                    com.windanesz.ancientspellcraft.block.SentinelBlockEntity::new,
                    SENTINEL_BLOCK.get(), SENTINEL_BLOCK_DIAMOND.get()).build(null));
    public static final Supplier<Block> SAGE_LECTERN = BLOCKS.register("sage_lectern",
            () -> new Block(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
                    .mapColor(net.minecraft.world.level.material.MapColor.WOOD).strength(2.0F).noOcclusion())); // TODO estacao de lectern
    public static final Supplier<Block> UNSEAL_BUTTON = BLOCKS.register("unseal_button",
            () -> new com.windanesz.ancientspellcraft.block.UnsealButtonBlock(
                    net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
                    .mapColor(net.minecraft.world.level.material.MapColor.STONE).strength(-1.0F, 3600000.0F)
                    .noLootTable().sound(net.minecraft.world.level.block.SoundType.STONE)));
    public static final Supplier<Block> PLACED_RUNE = BLOCKS.register("placed_rune",
            () -> new com.windanesz.ancientspellcraft.block.PlacedRuneBlock());

    // AS-9c: âncora dos rituais contínuos (arcane_barrier/condensing/forest)
    public static final Supplier<Block> RITUAL_CORE = BLOCKS.register("ritual_core",
            () -> new com.windanesz.ancientspellcraft.block.RitualCoreBlock(BlockBehaviour.Properties.of()
                    .noCollission().instabreak().noLootTable().lightLevel(state -> 5)
                    .pushReaction(PushReaction.DESTROY)));

    public static final Supplier<BlockEntityType<com.windanesz.ancientspellcraft.block.RitualCoreBlockEntity>> RITUAL_CORE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("ritual_core", () -> BlockEntityType.Builder.of(
                    com.windanesz.ancientspellcraft.block.RitualCoreBlockEntity::new, RITUAL_CORE.get()).build(null));

    // AS-10a: bloco escondido pelo conceal_object (invisível/atravessável; guarda o original)
    public static final Supplier<Block> CONCEALED_BLOCK = BLOCKS.register("concealed_block",
            () -> new com.windanesz.ancientspellcraft.block.ConcealedBlock(BlockBehaviour.Properties.of()
                    .noCollission().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion()));

    public static final Supplier<BlockEntityType<com.windanesz.ancientspellcraft.block.ConcealedBlockEntity>> CONCEALED_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("concealed_block", () -> BlockEntityType.Builder.of(
                    com.windanesz.ancientspellcraft.block.ConcealedBlockEntity::new, CONCEALED_BLOCK.get()).build(null));

    // AS-10b: estações restantes
    public static final Supplier<Block> SKULL_WATCH = BLOCKS.register("skull_watch",
            () -> new com.windanesz.ancientspellcraft.block.SkullWatchBlock(BlockBehaviour.Properties.of()
                    .mapColor(net.minecraft.world.level.material.MapColor.STONE).strength(3.0F, 5.0F)
                    .noOcclusion().sound(net.minecraft.world.level.block.SoundType.BONE_BLOCK)));

    public static final Supplier<BlockEntityType<com.windanesz.ancientspellcraft.block.SkullWatchBlockEntity>> SKULL_WATCH_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("skull_watch", () -> BlockEntityType.Builder.of(
                    com.windanesz.ancientspellcraft.block.SkullWatchBlockEntity::new, SKULL_WATCH.get()).build(null));

    public static final Supplier<Block> ARTEFACT_PENSIVE = BLOCKS.register("artefact_pensive",
            () -> new com.windanesz.ancientspellcraft.block.ArtefactPensiveBlock(BlockBehaviour.Properties.of()
                    .mapColor(net.minecraft.world.level.material.MapColor.STONE).strength(3.0F, 5.0F)
                    .noOcclusion().sound(net.minecraft.world.level.block.SoundType.STONE)));

    // AS-11: marcador do master_bolt (inquebrável; recolhido via sneak-cast)
    public static final Supplier<Block> MASTER_BOLT = BLOCKS.register("master_bolt",
            () -> new com.windanesz.ancientspellcraft.block.MasterBoltBlock(BlockBehaviour.Properties.of()
                    .strength(-1.0F, 3600000.0F).noLootTable().noOcclusion().noCollission()
                    .lightLevel(state -> 11).sound(net.minecraft.world.level.block.SoundType.AMETHYST)));

    public static final Supplier<BlockEntityType<com.windanesz.ancientspellcraft.block.ArtefactPensiveBlockEntity>> ARTEFACT_PENSIVE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("artefact_pensive", () -> BlockEntityType.Builder.of(
                    com.windanesz.ancientspellcraft.block.ArtefactPensiveBlockEntity::new, ARTEFACT_PENSIVE.get()).build(null));
    public static final java.util.Map<String, Supplier<Block>> RUNE_BLOCKS = new java.util.LinkedHashMap<>();

    static {
        for (String rune : new String[]{"ansuz", "kaunan", "laguz", "mannaz", "naudiz", "odal", "raido"}) {
            RUNE_BLOCKS.put(rune, BLOCKS.register("rune_" + rune,
                    com.windanesz.ancientspellcraft.block.PlacedRuneBlock::new));
        }
    }

    public static final Supplier<Block> ARCANE_WALL = BLOCKS.register("arcane_wall",
            () -> new com.windanesz.ancientspellcraft.block.TemporaryBlock(
                    net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
                            .mapColor(net.minecraft.world.level.material.MapColor.COLOR_PURPLE)
                            .sound(net.minecraft.world.level.block.SoundType.GLASS)
                            .noLootTable().strength(-1.0F, 3600000.0F).noOcclusion()
                            .lightLevel(state -> 5)));

    public static final Supplier<Block> ARCANE_ANVIL = BLOCKS.register("arcane_anvil",
            com.windanesz.ancientspellcraft.block.ArcaneAnvilBlock::new);

    public static final Supplier<net.minecraft.world.level.block.entity.BlockEntityType<com.windanesz.ancientspellcraft.block.ArcaneAnvilBlockEntity>> ARCANE_ANVIL_BE =
            BLOCK_ENTITIES.register("arcane_anvil",
                    () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
                            com.windanesz.ancientspellcraft.block.ArcaneAnvilBlockEntity::new, ARCANE_ANVIL.get()).build(null));

    public static final Supplier<Block> SCRIBING_DESK = BLOCKS.register("scribing_desk",
            com.windanesz.ancientspellcraft.block.ScribingDeskBlock::new);

    public static final Supplier<net.minecraft.world.level.block.entity.BlockEntityType<com.windanesz.ancientspellcraft.block.ScribingDeskBlockEntity>> SCRIBING_DESK_BE =
            BLOCK_ENTITIES.register("scribing_desk",
                    () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
                            com.windanesz.ancientspellcraft.block.ScribingDeskBlockEntity::new, SCRIBING_DESK.get()).build(null));

    public static final Supplier<Block> SPHERE_COGNIZANCE = BLOCKS.register("sphere_cognizance",
            () -> new com.windanesz.ancientspellcraft.block.SphereCognizanceBlock(BlockBehaviour.Properties.of()
                    .strength(0.5F).lightLevel(state -> 15).noOcclusion()
                    .sound(net.minecraft.world.level.block.SoundType.GLASS)));

    public static final Supplier<BlockEntityType<com.windanesz.ancientspellcraft.block.SphereCognizanceBlockEntity>> SPHERE_COGNIZANCE_BE =
            BLOCK_ENTITIES.register("sphere_cognizance", () -> BlockEntityType.Builder.of(
                    com.windanesz.ancientspellcraft.block.SphereCognizanceBlockEntity::new, SPHERE_COGNIZANCE.get()).build(null));

    public static final Supplier<Block> ICE_CRAFTING_TABLE = BLOCKS.register("ice_crafting_table",
            () -> new com.windanesz.ancientspellcraft.block.IceCraftingTableBlock(BlockBehaviour.Properties.of()
                    .strength(1.0F).friction(0.98F).sound(net.minecraft.world.level.block.SoundType.GLASS)));

    public static final Supplier<Block> IMBUEMENT_ALTAR_RUINED = BLOCKS.register("imbuement_altar_ruined",
            () -> new Block(BlockBehaviour.Properties.of().strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops().sound(net.minecraft.world.level.block.SoundType.STONE)));

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
                                    CONJURED_SNOW.get(), QUICKSAND.get(), LIGHTNING_BLOCK.get(), ARCANE_WALL.get()),
                            MUSHROOMS.values().stream().map(Supplier::get)).toArray(Block[]::new)).build(null));

    public static final Supplier<BlockEntityType<MageLightBlockEntity>> MAGE_LIGHT_BE = BLOCK_ENTITIES.register("mage_light",
            () -> BlockEntityType.Builder.of(MageLightBlockEntity::new, MAGELIGHT.get(), CANDLELIGHT.get()).build(null));

    private ASBlocks() {
    }
}
