package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Torre de gelo (1.12.2 IceTower): ergue a torre hardcoded de neve/packed ice com escada em
 * espiral de snow slabs, porta de gelo e janelas de vidro azul. Precisa de céu aberto num raio
 * de 5 ao redor do conjurador.
 */
public class IceTower extends Spell {

    @Override
    public boolean cast(PlayerCastContext ctx) {
        var caster = ctx.caster();
        var world = ctx.world();
        if (!caster.onGround()) return false;

        BlockPos base = caster.blockPosition().below();
        for (BlockPos test : BlockPos.betweenClosed(caster.blockPosition().south(5).west(5),
                caster.blockPosition().above().north(5).east(5))) {
            if (!world.canSeeSky(test)) {
                if (!world.isClientSide) {
                    caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.ice_tower.no_room"), true);
                }
                return false;
            }
        }
        if (world.isClientSide) return true;

        List<BlockPos> walls = new ArrayList<>();
        BlockPos layer1 = base.above();

        for (int i = 0; i < 10; i++) {
            walls.add(layer1.above(i).north(2));
            walls.add(layer1.above(i).south(2));
            walls.add(layer1.above(i).east(2));
            walls.add(layer1.above(i).west(2));
            walls.add(layer1.above(i).north(2).east(1));
            walls.add(layer1.above(i).south(2).east(1));
            walls.add(layer1.above(i).south(2).west(1));
            walls.add(layer1.above(i).north(2).west(1));
            walls.add(layer1.above(i).north(1).east(2));
            walls.add(layer1.above(i).south(1).east(2));
            walls.add(layer1.above(i).north(1).west(2));
            walls.add(layer1.above(i).south(1).west(2));
            for (BlockPos floor : BlockPos.betweenClosed(base.below().north(2).west(2).offset(0, 1, 0),
                    base.south(2).west(2).offset(4, 0, 1))) {
                walls.add(floor.immutable());
            }
        }
        place(world, walls, Blocks.SNOW_BLOCK.defaultBlockState());

        BlockPos layer2 = layer1.above(10);
        List<BlockPos> upperWalls = new ArrayList<>();
        List<BlockPos> corners = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            upperWalls.add(layer2.above(i).north(3));
            upperWalls.add(layer2.above(i).south(3));
            upperWalls.add(layer2.above(i).east(3));
            upperWalls.add(layer2.above(i).west(3));
            upperWalls.add(layer2.above(i).north(1).east(3));
            upperWalls.add(layer2.above(i).north(1).west(3));
            upperWalls.add(layer2.above(i).south(1).east(3));
            upperWalls.add(layer2.above(i).south(1).west(3));
            upperWalls.add(layer2.above(i).north(3).east(1));
            upperWalls.add(layer2.above(i).north(3).west(1));
            upperWalls.add(layer2.above(i).south(3).east(1));
            upperWalls.add(layer2.above(i).south(3).west(1));
            corners.add(layer2.above(i).north(2).east(2));
            corners.add(layer2.above(i).north(2).west(2));
            corners.add(layer2.above(i).south(2).east(2));
            corners.add(layer2.above(i).south(2).west(2));
        }
        place(world, upperWalls, Blocks.SNOW_BLOCK.defaultBlockState());
        place(world, corners, Blocks.PACKED_ICE.defaultBlockState());

        // escada em espiral (1.12.2 blockposListLower/Upper)
        List<BlockPos> lower = new ArrayList<>();
        List<BlockPos> upper = new ArrayList<>();
        lower.add(layer1.north(1).east(1));
        upper.add(layer1.north(1));
        lower.add(layer1.above(1).north(1).west(1));
        upper.add(layer1.above(1).west(1));
        lower.add(layer1.above(2).south(1).west(1));
        upper.add(layer1.above(2).south(1));
        lower.add(layer1.above(3).south(1).east(1));
        upper.add(layer1.above(3).east(1));
        List<BlockPos> lowerRepeat = new ArrayList<>();
        List<BlockPos> upperRepeat = new ArrayList<>();
        for (BlockPos pos : lower) lowerRepeat.add(pos.above(4));
        for (BlockPos pos : upper) upperRepeat.add(pos.above(4));
        lower.addAll(lowerRepeat);
        upper.addAll(upperRepeat);
        lower.add(layer1.above(8).north(1).east(1));
        upper.add(layer1.above(8).north(1));
        lower.add(layer1.above(9).north(1).west(1));
        upper.add(layer1.above(9).south(1).west(1));
        upper.add(layer1.above(9).west(1));
        upper.add(layer1.above(9));
        upper.add(layer1.above(9).south(1).west(1));
        upper.add(layer1.above(9).south(1));

        // telhado
        BlockPos topcenter = layer1.above(12).north(2).west(2);
        List<BlockPos> roof = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(topcenter.offset(0, 1, 0), topcenter.offset(4, 1, 4))) {
            roof.add(pos.immutable());
        }
        roof.add(topcenter.north(1));
        roof.add(topcenter.north(1).east(1));
        roof.add(topcenter.north(1).east(2));
        roof.add(topcenter.north(1).east(3));
        roof.add(topcenter.north(1).east(4));
        roof.add(topcenter.east(4));
        roof.add(topcenter.west(1));
        roof.add(topcenter.south(1).west(1));
        roof.add(topcenter.south(2).west(1));
        roof.add(topcenter.south(3).west(1));
        roof.add(topcenter.south(4).west(1));
        roof.add(topcenter.south(5));
        roof.add(topcenter.south(5).east(1));
        roof.add(topcenter.south(5).east(2));
        roof.add(topcenter.south(5).east(3));
        roof.add(topcenter.south(5).east(4));
        roof.add(topcenter.east(5));
        roof.add(topcenter.south(1).east(5));
        roof.add(topcenter.south(2).east(5));
        roof.add(topcenter.south(3).east(5));
        roof.add(topcenter.south(4).east(5));
        for (BlockPos pos : BlockPos.betweenClosed(topcenter.above().south(1).east(1).offset(0, 1, 0),
                topcenter.offset(3, 3, 3))) {
            roof.add(pos.immutable());
        }
        roof.add(topcenter.above(2).south(2));
        roof.add(topcenter.above(2).east(2));
        roof.add(topcenter.above(2).east(4).south(2));
        roof.add(topcenter.above(2).east(2).south(4));
        roof.add(topcenter.above(4).south(2).east(2));
        roof.add(topcenter.above(5).south(2).east(2));
        place(world, roof, Blocks.PACKED_ICE.defaultBlockState());

        BlockState slabBottom = ASBlocks.SNOW_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM);
        BlockState slabTop = ASBlocks.SNOW_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP);
        for (BlockPos pos : lower) world.setBlockAndUpdate(pos, slabBottom);
        for (BlockPos pos : upper) world.setBlockAndUpdate(pos, slabTop);

        // porta
        world.setBlockAndUpdate(base.above().south(2), ASBlocks.ICE_DOOR.get().defaultBlockState()
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER).setValue(DoorBlock.FACING, Direction.SOUTH));
        world.setBlockAndUpdate(base.above(2).south(2), ASBlocks.ICE_DOOR.get().defaultBlockState()
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER).setValue(DoorBlock.FACING, Direction.SOUTH));

        // janelas
        List<BlockPos> windows = new ArrayList<>();
        windows.add(base.above(3).north(2));
        windows.add(base.above(7).north(2));
        windows.add(base.above(12).north(3));
        windows.add(base.above(12).south(3));
        windows.add(base.above(12).east(3));
        windows.add(base.above(12).west(3));
        for (BlockPos window : windows) {
            world.setBlockAndUpdate(window, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE.defaultBlockState());
        }
        return true;
    }

    private static void place(net.minecraft.world.level.Level world, List<BlockPos> positions, BlockState state) {
        for (BlockPos pos : positions) {
            if (world.getBlockState(pos).canBeReplaced()) {
                world.setBlockAndUpdate(pos, state);
            }
        }
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull com.koomplo.wizardry.api.content.spell.properties.SpellProperties properties() {
        return com.koomplo.wizardry.api.content.spell.properties.SpellProperties.empty();
    }
}
