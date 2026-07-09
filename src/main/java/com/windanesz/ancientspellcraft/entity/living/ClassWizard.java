package com.windanesz.ancientspellcraft.entity.living;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.util.InventoryUtil;
import com.koomplo.wizardry.api.content.util.RegistryUtils;
import com.koomplo.wizardry.content.entity.living.Wizard;
import com.koomplo.wizardry.content.item.armor.WizardArmorType;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.spell.ClassSpell;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Mago de classe amigavel (1.12.2 EntityClassWizard-friendly): wizard do Redux com set de classe e class spells. Trades herdadas do wizard (TODO trades de class books). */
public class ClassWizard extends Wizard {

    public ClassWizard(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    private static final EntityDataAccessor<Integer> ARMOUR_CLASS =
            SynchedEntityData.defineId(ClassWizard.class, EntityDataSerializers.INT);

    public static final WizardArmorType[] CLASSES = {WizardArmorType.SAGE, WizardArmorType.WARLOCK, WizardArmorType.BATTLEMAGE};

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ARMOUR_CLASS, 0);
    }

    public WizardArmorType getArmourClass() {
        return CLASSES[entityData.get(ARMOUR_CLASS)];
    }

    public void setArmourClass(WizardArmorType type) {
        for (int i = 0; i < CLASSES.length; i++) {
            if (CLASSES[i] == type) entityData.set(ARMOUR_CLASS, i);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty,
                                        @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, mobSpawnType, spawnData);
        // sem battlemage por enquanto (melee AI propria fica com o lote da classe)
        setArmourClass(random.nextBoolean() ? WizardArmorType.SAGE : WizardArmorType.WARLOCK);
        applyClass();
        return result;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && spells.isEmpty()) {
            applyClass();
        }
    }

    /** Re-equipa com a armadura da classe e troca a lista de spells pelas class spells. */
    public void applyClass() {
        if (getElement() == null) {
            var elements = new java.util.ArrayList<>(com.koomplo.wizardry.core.platform.Services.REGISTRY_UTIL.getElements());
            setElement(elements.get(random.nextInt(elements.size())));
        }
        WizardArmorType type = getArmourClass();
        for (EquipmentSlot slot : InventoryUtil.ARMOR_SLOTS) {
            Item armor = RegistryUtils.getArmor(type, getElement(), slot);
            if (armor != Items.AIR) setItemSlot(slot, new ItemStack(armor));
        }
        List<Spell> classSpells = RegistryUtils.getSpells(s -> s instanceof ClassSpell cs && cs.armourClass() == type);
        if (!classSpells.isEmpty()) {
            spells.clear();
            for (int i = 0; i < 4 && !classSpells.isEmpty(); i++) {
                spells.add(classSpells.remove(random.nextInt(classSpells.size())));
            }
        }
        setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(
                type == WizardArmorType.WARLOCK ? ASItems.FORBIDDEN_TOME.get() : ASItems.MYSTIC_SPELL_BOOK.get()));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(ARMOUR_CLASS, tag.getInt("ArmourClass"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ArmourClass", entityData.get(ARMOUR_CLASS));
    }
}
