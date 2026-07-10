package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Pocket Library (1.12.2 PocketLibrary, SAGE): invoca a torre-biblioteca pessoal (template
 * pocket_library, 7x15x7) na sua frente; recastar a até 3 blocos do centro desfaz a torre e
 * restaura o terreno original (snapshot no attachment). Desvios documentados: sem persistência
 * dos blocos colocados pelo jogador dentro da torre (área 3x3x3 do 1.12.2) e uma torre por vez.
 */
public class PocketLibrarySpell extends Spell implements ClassSpell {

    private static final String SUMMONED_TAG = "PocketLibrarySummoned";
    private static final String CENTER_TAG = "PocketLibraryCenter";
    private static final String DIM_TAG = "PocketLibraryDim";
    private static final String SNAPSHOT_TAG = "PocketLibrarySnapshot";
    private static final double MAX_PACK_DISTANCE = 3.0;
    private static final ResourceLocation TEMPLATE = ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, "pocket_library");

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!(ctx.world() instanceof ServerLevel serverLevel) || !(ctx.caster() instanceof Player caster)) {
            return ctx.world().isClientSide;
        }
        CompoundTag tag = caster.getData(ASAttachments.PLAYER_DATA);

        if (tag.getBoolean(SUMMONED_TAG)) {
            return pack(serverLevel, caster, tag);
        }
        return summon(serverLevel, caster, tag);
    }

    private boolean summon(ServerLevel level, Player caster, CompoundTag tag) {
        var template = level.getStructureManager().get(TEMPLATE).orElse(null);
        if (template == null) return false;
        var size = template.getSize();
        BlockPos origin = caster.blockPosition()
                .relative(caster.getDirection(), 2)
                .offset(-size.getX() / 2, 0, -size.getZ() / 2);

        // snapshot do terreno substituído
        ListTag snapshot = new ListTag();
        for (BlockPos pos : BlockPos.betweenClosed(origin, origin.offset(size.getX() - 1, size.getY() - 1, size.getZ() - 1))) {
            CompoundTag entry = new CompoundTag();
            entry.put("Pos", NbtUtils.writeBlockPos(pos.immutable()));
            entry.put("State", NbtUtils.writeBlockState(level.getBlockState(pos)));
            snapshot.add(entry);
        }
        template.placeInWorld(level, origin, origin, new StructurePlaceSettings(), level.random, 2);

        tag.putBoolean(SUMMONED_TAG, true);
        tag.put(CENTER_TAG, NbtUtils.writeBlockPos(origin.offset(size.getX() / 2, 0, size.getZ() / 2)));
        tag.putString(DIM_TAG, level.dimension().location().toString());
        tag.put(SNAPSHOT_TAG, snapshot);
        caster.setData(ASAttachments.PLAYER_DATA, tag);
        this.playSound(level, caster, 0, -1);
        return true;
    }

    private boolean pack(ServerLevel level, Player caster, CompoundTag tag) {
        if (!level.dimension().location().toString().equals(tag.getString(DIM_TAG))) {
            caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.pocket_library.wrong_dimension"), true);
            return false;
        }
        BlockPos center = NbtUtils.readBlockPos(tag, CENTER_TAG).orElse(null);
        if (center == null || caster.distanceToSqr(center.getX() + 0.5, center.getY(), center.getZ() + 0.5)
                > MAX_PACK_DISTANCE * MAX_PACK_DISTANCE * 4) {
            caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.pocket_library.too_far"), true);
            return false;
        }
        for (Tag t : tag.getList(SNAPSHOT_TAG, Tag.TAG_COMPOUND)) {
            CompoundTag entry = (CompoundTag) t;
            BlockPos pos = NbtUtils.readBlockPos(entry, "Pos").orElse(null);
            if (pos == null) continue;
            BlockState state = NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK), entry.getCompound("State"));
            level.setBlock(pos, state, 2);
        }
        tag.remove(SUMMONED_TAG);
        tag.remove(CENTER_TAG);
        tag.remove(DIM_TAG);
        tag.remove(SNAPSHOT_TAG);
        caster.setData(ASAttachments.PLAYER_DATA, tag);
        this.playSound(level, caster, 0, -1);
        return true;
    }

    @Override
    public WizardArmorType armourClass() {
        return WizardArmorType.SAGE;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.MYSTIC_SPELL_BOOK.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
