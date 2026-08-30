package com.github.rinorsi.cadeditor.client.screen.model.category.entity;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.EntityEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.entity.EntityEquipmentEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.item.ItemStack;


public class EntityEquipmentCategoryModel extends EntityCategoryModel {
    private static final String EQUIPMENT_TAG = "equipment";
    private static final String DROP_CHANCES_TAG = "drop_chances";
    private static final String LEGACY_HAND_ITEMS_TAG = "HandItems";
    private static final String LEGACY_HAND_DROPS_TAG = "HandDropChances";
    private static final String LEGACY_ARMOR_ITEMS_TAG = "ArmorItems";
    private static final String LEGACY_ARMOR_DROPS_TAG = "ArmorDropChances";
    public static final float DEFAULT_DROP_CHANCE = 0.085f;
    public static final float MAX_DROP_CHANCE = 2.0f;
    public static final float DROP_EPSILON = 1.0E-4f;
    private final List<EntityEquipmentEntryModel> equipmentEntries;

    public EntityEquipmentCategoryModel(EntityEditorModel editor) {
        super(ModTexts.ENTITY_EQUIPMENT, editor);
        this.equipmentEntries = new ArrayList<>();
    }

    @Override 
    protected void setupEntries() {
        getEntries().clear();
        this.equipmentEntries.clear();
        for (Slot slot : Slot.values()) {
            if (slot == Slot.BODY && !isBodySlotUser()) {
                continue;
            }
            ItemStack stack = readItem(slot);
            float dropChance = readDropChance(slot);
            EntityEquipmentEntryModel entry = new EntityEquipmentEntryModel(this, slot, stack, dropChance);
            this.equipmentEntries.add(entry);
            getEntries().add(entry);
        }
    }

    private ItemStack readItem(Slot slot) {
        CompoundTag itemTag;
        CompoundTag data = getData();
        if (data == null) {
            return ItemStack.EMPTY;
        }
        CompoundTag equipment = data.getCompound(EQUIPMENT_TAG).orElse(null);
        if (equipment != null && equipment.contains(slot.equipmentKey) && (itemTag = equipment.getCompound(slot.equipmentKey).orElse(null)) != null) {
            return ClientUtil.parseItemStack(ClientUtil.registryAccess(), itemTag);
        }
        if (slot.legacyItemListTag == null) {
            return ItemStack.EMPTY;
        }
        if (!data.contains(slot.legacyItemListTag)) {
            return ItemStack.EMPTY;
        }
        ListTag list = data.getListOrEmpty(slot.legacyItemListTag);
        if (slot.legacyIndex >= list.size()) {
            return ItemStack.EMPTY;
        }
        if (list.get(slot.legacyIndex) instanceof CompoundTag compound) {
            return ClientUtil.parseItemStack(ClientUtil.registryAccess(), compound);
        }
        return ItemStack.EMPTY;
    }

    private float readDropChance(Slot slot) {
        CompoundTag data = getData();
        if (data == null) {
            return slot.defaultDropChance;
        }
        CompoundTag dropChances = data.getCompound(DROP_CHANCES_TAG).orElse(null);
        if (dropChances != null && dropChances.contains(slot.equipmentKey)) {
            return dropChances.getFloatOr(slot.equipmentKey, slot.defaultDropChance);
        }
        if (slot.legacyDropChanceListTag == null) {
            return slot.defaultDropChance;
        }
        if (!data.contains(slot.legacyDropChanceListTag)) {
            return slot.defaultDropChance;
        }
        ListTag list = data.getListOrEmpty(slot.legacyDropChanceListTag);
        if (slot.legacyIndex >= list.size()) {
            return slot.defaultDropChance;
        }
        FloatTag floatTag = (FloatTag) list.get(slot.legacyIndex);
        if (floatTag instanceof FloatTag) {
            FloatTag checkedFloatTag = floatTag;
            return checkedFloatTag.floatValue();
        }
        return slot.defaultDropChance;
    }

    @Override 
    public void apply() {
        super.apply();
        writeToTag();
    }

    private void writeToTag() {
        CompoundTag data = getData();
        CompoundTag equipment = data.getCompound(EQUIPMENT_TAG).map(value -> value.copy()).orElseGet(CompoundTag::new);
        CompoundTag dropChances = data.getCompound(DROP_CHANCES_TAG).map(value -> value.copy()).orElseGet(CompoundTag::new);
        for (EntityEquipmentEntryModel entry : this.equipmentEntries) {
            Slot slot = entry.getSlot();
            equipment.remove(slot.equipmentKey);
            dropChances.remove(slot.equipmentKey);
        }
        boolean managedEquipment = false;
        boolean managedDropChances = false;
        for (EntityEquipmentEntryModel entry : this.equipmentEntries) {
            Slot slot = entry.getSlot();
            CompoundTag itemTag = entry.createItemTag();
            if (!itemTag.isEmpty()) {
                equipment.put(slot.equipmentKey, itemTag);
                managedEquipment = true;
            }
            if (!itemTag.isEmpty() && !entry.isDefaultDropChance()) {
                dropChances.putFloat(slot.equipmentKey, entry.getDropChance());
                managedDropChances = true;
            }
        }
        if (managedEquipment || !equipment.isEmpty()) {
            data.put(EQUIPMENT_TAG, equipment);
        } else {
            data.remove(EQUIPMENT_TAG);
        }
        if (managedDropChances || !dropChances.isEmpty()) {
            data.put(DROP_CHANCES_TAG, dropChances);
        } else {
            data.remove(DROP_CHANCES_TAG);
        }
        data.remove(LEGACY_HAND_ITEMS_TAG);
        data.remove(LEGACY_HAND_DROPS_TAG);
        data.remove(LEGACY_ARMOR_ITEMS_TAG);
        data.remove(LEGACY_ARMOR_DROPS_TAG);
    }

    public String formatDropChance(float value) {
        return String.format(Locale.ROOT, "%.3f", value);
    }

    private boolean isBodySlotUser() {
        return getEntity() instanceof SulfurCube
                || getEntity() instanceof AbstractHorse
                || getEntity() instanceof Wolf
                || getEntity() instanceof HappyGhast
                || getEntity() instanceof AbstractNautilus;
    }

    public enum Slot {
        MAIN_HAND("mainhand", EntityEquipmentCategoryModel.LEGACY_HAND_ITEMS_TAG, EntityEquipmentCategoryModel.LEGACY_HAND_DROPS_TAG, 0, true, () -> {
            return ModTexts.MAIN_HAND.copy();
        }),
        OFF_HAND("offhand", EntityEquipmentCategoryModel.LEGACY_HAND_ITEMS_TAG, EntityEquipmentCategoryModel.LEGACY_HAND_DROPS_TAG, 1, true, () -> {
            return ModTexts.OFF_HAND.copy();
        }),
        FEET("feet", EntityEquipmentCategoryModel.LEGACY_ARMOR_ITEMS_TAG, EntityEquipmentCategoryModel.LEGACY_ARMOR_DROPS_TAG, 0, false, () -> {
            return ModTexts.FEET.copy();
        }),
        LEGS("legs", EntityEquipmentCategoryModel.LEGACY_ARMOR_ITEMS_TAG, EntityEquipmentCategoryModel.LEGACY_ARMOR_DROPS_TAG, 1, false, () -> {
            return ModTexts.LEGS.copy();
        }),
        CHEST("chest", EntityEquipmentCategoryModel.LEGACY_ARMOR_ITEMS_TAG, EntityEquipmentCategoryModel.LEGACY_ARMOR_DROPS_TAG, 2, false, () -> {
            return ModTexts.CHEST.copy();
        }),
        HEAD("head", EntityEquipmentCategoryModel.LEGACY_ARMOR_ITEMS_TAG, EntityEquipmentCategoryModel.LEGACY_ARMOR_DROPS_TAG, 3, false, () -> {
            return ModTexts.HEAD.copy();
        }),
        BODY("body", null, null, -1, false, () -> {
            return ModTexts.BODY_SLOT.copy();
        });

        private final String equipmentKey;
        private final String legacyItemListTag;
        private final String legacyDropChanceListTag;
        private final int legacyIndex;
        private final boolean hand;
        private final Supplier<MutableComponent> labelSupplier;
        private final float defaultDropChance;

        Slot(String equipmentKey, String legacyItemListTag, String legacyDropChanceListTag, int legacyIndex, boolean hand, Supplier supplier) {
            this(equipmentKey, legacyItemListTag, legacyDropChanceListTag, legacyIndex, hand, supplier, 0.085f);
        }

        Slot(String equipmentKey, String legacyItemListTag, String legacyDropChanceListTag, int legacyIndex, boolean hand, Supplier supplier, float defaultDropChance) {
            this.equipmentKey = equipmentKey;
            this.legacyItemListTag = legacyItemListTag;
            this.legacyDropChanceListTag = legacyDropChanceListTag;
            this.legacyIndex = legacyIndex;
            this.hand = hand;
            this.labelSupplier = supplier;
            this.defaultDropChance = defaultDropChance;
        }

        public boolean isHand() {
            return this.hand;
        }

        public MutableComponent label() {
            return this.labelSupplier.get();
        }

        public float defaultDropChance() {
            return this.defaultDropChance;
        }
    }
}
