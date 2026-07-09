package com.windanesz.ancientspellcraft.entity.living;

import com.koomplo.wizardry.api.content.entity.living.ISpellCaster;
import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.content.entity.goal.AttackSpellGoal;
import com.koomplo.wizardry.setup.registries.Elements;
import com.koomplo.wizardry.setup.registries.Spells;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Esqueleto mago elemental (1.12.2 EntitySkeletonMage): casta uma spell do wizardry conforme o elemento.
 * TODO elemento por bioma (config 1.12.2) e radiant_spark do morphspellpack (fallback magic_missile).
 */
public class SkeletonMageEntity extends AbstractSkeleton implements ISpellCaster {

    private static final EntityDataAccessor<Integer> ELEMENT =
            SynchedEntityData.defineId(SkeletonMageEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> RARE =
            SynchedEntityData.defineId(SkeletonMageEntity.class, EntityDataSerializers.BOOLEAN);

    public static final Element[] MAGE_ELEMENTS = {Elements.FIRE, Elements.ICE, Elements.LIGHTNING,
            Elements.NECROMANCY, Elements.EARTH, Elements.SORCERY, Elements.HEALING};

    protected List<Spell> spells = new ArrayList<>(1);

    public SkeletonMageEntity(EntityType<? extends AbstractSkeleton> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ELEMENT, 0);
        builder.define(RARE, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new AttackSpellGoal<>(this, 1.0D, 15f, 25, 40));
    }

    @Override
    protected void populateDefaultEquipmentSlots(@NotNull net.minecraft.util.RandomSource random, @NotNull DifficultyInstance difficulty) {
        // Sem arco: o mago luta com spells
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty,
                                        @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        setRare(level.getRandom().nextFloat() < 0.4f);
        setMageElement(level.getRandom().nextInt(MAGE_ELEMENTS.length));
        return data;
    }

    public void setRare(boolean rare) {
        entityData.set(RARE, rare);
    }

    public boolean isRare() {
        return entityData.get(RARE);
    }

    public void setMageElement(int index) {
        entityData.set(ELEMENT, index);
        populateSpellList(MAGE_ELEMENTS[index]);
    }

    public Element getMageElement() {
        return MAGE_ELEMENTS[entityData.get(ELEMENT)];
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && spells.isEmpty()) {
            populateSpellList(getMageElement());
        }
    }

    public void populateSpellList(Element element) {
        spells = new ArrayList<>(1);
        boolean rare = isRare();
        if (element == Elements.FIRE) spells.add(rare ? Spells.FIRE_BREATH : Spells.FIREBALL);
        else if (element == Elements.ICE) spells.add(rare ? Spells.ICE_LANCE : Spells.ICE_BALL);
        else if (element == Elements.LIGHTNING) spells.add(rare ? Spells.LIGHTNING_DISC : Spells.LIGHTNING_ARROW);
        else if (element == Elements.EARTH) spells.add(rare ? Spells.FANGS : Spells.DART);
        else if (element == Elements.SORCERY) spells.add(rare ? Spells.FORCE_ORB : Spells.FORCE_ARROW);
        else if (element == Elements.HEALING) spells.add(rare ? Spells.RAY_OF_PURIFICATION : Spells.MAGIC_MISSILE);
        else spells.add(rare ? Spells.SUMMON_WITHER_SKELETON : Spells.SUMMON_ZOMBIE);
    }

    @Override
    public @NotNull List<Spell> getSpells() {
        return spells;
    }

    @Override
    public void addAdditionalSaveData(@NotNull net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MageElement", entityData.get(ELEMENT));
        tag.putBoolean("Rare", isRare());
    }

    @Override
    public void readAdditionalSaveData(@NotNull net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setRare(tag.getBoolean("Rare"));
        setMageElement(tag.getInt("MageElement"));
    }

    @Override
    protected @NotNull SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }
}
