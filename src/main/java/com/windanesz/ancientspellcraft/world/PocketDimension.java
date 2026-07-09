package com.windanesz.ancientspellcraft.world;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

/** O espaco de bolso do warlock: plot por jogador na dimensao ancientspellcraft:pocket. */
public final class PocketDimension {

    public static final ResourceKey<Level> POCKET = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, "pocket"));
    private static final ResourceLocation LIBRARY = ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, "pocket_library");
    private static final int FLOOR_Y = 64;

    public static boolean isInside(Player player) {
        return player.level().dimension().equals(POCKET);
    }

    private static BlockPos plotOrigin(ServerPlayer player) {
        int hash = player.getUUID().hashCode();
        int gridX = (hash & 0xFFFF) % 1024 - 512;
        int gridZ = ((hash >> 16) & 0xFFFF) % 1024 - 512;
        return new BlockPos(gridX * 256, FLOOR_Y, gridZ * 256);
    }

    public static void teleportIn(ServerPlayer player) {
        teleportIn(player, null);
    }

    public static void teleportIn(ServerPlayer player, @org.jetbrains.annotations.Nullable com.koomplo.wizardry.api.content.spell.Element element) {
        ServerLevel pocket = player.server.getLevel(POCKET);
        if (pocket == null) return;

        var tag = player.getData(ASAttachments.WARLOCK_DATA);
        tag.putString("ReturnDim", player.level().dimension().location().toString());
        tag.put("ReturnPos", NbtUtils.writeBlockPos(player.blockPosition()));
        player.setData(ASAttachments.WARLOCK_DATA, tag);

        BlockPos origin = plotOrigin(player);
        var size = pocket.getStructureManager().get(LIBRARY).map(t -> t.getSize()).orElse(new net.minecraft.core.Vec3i(9, 9, 9));
        if (pocket.getBlockState(origin.offset(2, 0, 2)).isAir()) {
            var template = pocket.getStructureManager().get(LIBRARY).orElse(null);
            if (template != null) {
                template.placeInWorld(pocket, origin, origin, new StructurePlaceSettings(), pocket.random, 2);
            }
            buildShell(pocket, origin, size, element);
        }
        player.teleportTo(pocket, origin.getX() + size.getX() / 2.0 + 0.5, origin.getY() + 1.5,
                origin.getZ() + size.getZ() / 2.0 + 0.5, player.getYRot(), player.getXRot());
    }

    /**
     * Casca de dimension_boundary do elemento do orb em volta do plot + focus_gold como saída
     * física (1.12.2 OrbSpace.createPocket: paredes elementais e focus no pocket).
     */
    private static void buildShell(ServerLevel pocket, BlockPos origin, net.minecraft.core.Vec3i size,
                                   @org.jetbrains.annotations.Nullable com.koomplo.wizardry.api.content.spell.Element element) {
        var boundarySupplier = element == null ? com.windanesz.ancientspellcraft.registry.ASBlocks.DIMENSION_BOUNDARY
                : com.windanesz.ancientspellcraft.registry.ASBlocks.DIMENSION_BOUNDARIES
                        .getOrDefault(element.getName(), com.windanesz.ancientspellcraft.registry.ASBlocks.DIMENSION_BOUNDARY);
        var boundary = boundarySupplier.get().defaultBlockState();
        int margin = 4;
        BlockPos min = origin.offset(-margin, -1, -margin);
        BlockPos max = origin.offset(size.getX() + margin, size.getY() + margin, size.getZ() + margin);
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            boolean shell = pos.getX() == min.getX() || pos.getX() == max.getX()
                    || pos.getY() == min.getY() || pos.getY() == max.getY()
                    || pos.getZ() == min.getZ() || pos.getZ() == max.getZ();
            if (shell && pocket.getBlockState(pos).isAir()) {
                pocket.setBlock(pos.immutable(), boundary, 2);
            }
        }
        pocket.setBlock(origin.offset(size.getX() / 2, 0, -2),
                com.windanesz.ancientspellcraft.registry.ASBlocks.DIMENSION_FOCUS_GOLD.get().defaultBlockState(), 2);
    }

    public static void teleportBack(ServerPlayer player) {
        var tag = player.getData(ASAttachments.WARLOCK_DATA);
        ResourceLocation dim = ResourceLocation.tryParse(tag.getString("ReturnDim"));
        BlockPos pos = NbtUtils.readBlockPos(tag, "ReturnPos").orElse(null);
        ServerLevel target = dim == null ? null
                : player.server.getLevel(ResourceKey.create(Registries.DIMENSION, dim));
        if (target == null || pos == null) {
            target = player.server.overworld();
            pos = target.getSharedSpawnPos();
        }
        player.teleportTo(target, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5,
                player.getYRot(), player.getXRot());
    }

    private PocketDimension() {
    }
}
