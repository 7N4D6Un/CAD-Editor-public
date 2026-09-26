package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.ArmorStandSections;
import com.github.rinorsi.cadeditor.client.screen.model.category.entity.EntityEquipmentCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.ArmorStandEquipmentEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntityPreviewEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.TextEntryModel;
import com.github.rinorsi.cadeditor.client.util.ComponentJsonHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;

public class ItemArmorStandCategoryModel extends ItemEditorCategoryModel {
    private CompoundTag data;
    private boolean foreignEntityData;
    private final ArmorStandSections sections;

    public ItemArmorStandCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("armor_stand"), editor);
        readData();
        sections = new ArmorStandSections(this, () -> data);
    }

    @Override
    protected void setupEntries() {
        getEntries().add(sections.buildPreviewEntry());
        sections.refreshPreview();

        getEntries().addAll(sections.buildPoseEntries());

        getEntries().add(new SpacerEntryModel(this));
        getEntries().addAll(sections.buildFlagEntries());
        addBoolean("NoGravity", "no_gravity");
        addBoolean("Invulnerable", "invulnerable");
        addBoolean("CustomNameVisible", "always_show_name");

        MutableComponent customName = getCustomName();
        TextEntryModel nameEntry = new TextEntryModel(this, ModTexts.CUSTOM_NAME, customName, this::setCustomName);
        nameEntry.setFactoryDefault(customName);
        nameEntry.valueProperty().addListener(v -> nameEntry.apply());
        getEntries().add(nameEntry);

        getEntries().add(new SpacerEntryModel(this));
        getEntries().addAll(sections.buildDisabledSlotEntries());
        setupEquipmentEntries();
    }

    @Override
    public int getEntryHeight(EntryModel entry) {
        return entry instanceof EntityPreviewEntryModel ? ArmorStandSections.PREVIEW_HEIGHT : getEntryHeight();
    }

    private void addBoolean(String tag, String key) {
        getEntries().add(new BooleanEntryModel(this, ModTexts.gui(key), data.getBooleanOr(tag, false),
                b -> sections.putBooleanOrRemove(tag, b)));
    }

    private MutableComponent getCustomName() {
        Tag encoded = data.get("CustomName");
        return ComponentJsonHelper.decode(encoded, ClientUtil.registryAccess());
    }

    private void setCustomName(MutableComponent value) {
        if (value != null && !value.getString().isEmpty()) {
            Tag encoded = ComponentJsonHelper.encodeToTag(value, ClientUtil.registryAccess());
            if (encoded != null) {
                data.put("CustomName", encoded);
            }
        } else {
            data.remove("CustomName");
        }
        sections.refreshPreview();
    }

    private void readData() {
        this.foreignEntityData = false;
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack != null) {
            TypedEntityData<?> typed = stack.get(DataComponents.ENTITY_DATA);
            if (typed != null) {
                if (typed.type() == EntityTypes.ARMOR_STAND) {
                    this.data = typed.copyTagWithoutId();
                } else {
                    this.foreignEntityData = true;
                    this.data = new CompoundTag();
                }
                return;
            }
        }
        CompoundTag root = getTag();
        if (root != null) {
            CompoundTag entityTag = root.getCompound("EntityTag").orElse(null);
            if (entityTag != null) {
                CompoundTag copy = entityTag.copy();
                copy.remove("id");
                this.data = copy;
                return;
            }
        }
        this.data = new CompoundTag();
    }

    @Override
    public void apply() {
        super.apply();
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack == null) {
            return;
        }
        if (foreignEntityData && data.isEmpty()) {
            return;
        }
        CompoundTag itemData = getData();
        CompoundTag legacyTag = itemData == null ? null : itemData.getCompound("tag").orElse(null);
        if (legacyTag != null && legacyTag.contains("EntityTag")) {
            legacyTag.remove("EntityTag");
            if (legacyTag.isEmpty()) {
                itemData.remove("tag");
            }
        }
        if (data.isEmpty()) {
            stack.remove(DataComponents.ENTITY_DATA);
        } else {
            stack.set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityTypes.ARMOR_STAND, data.copy()));
        }
    }

    private void setupEquipmentEntries() {
        getEntries().add(new SpacerEntryModel(this));
        for (EntityEquipmentCategoryModel.Slot slot : ArmorStandSections.EQUIPMENT_SLOTS) {
            ArmorStandEquipmentEntryModel entry = new ArmorStandEquipmentEntryModel(this, slot, readEquipmentStack(slot));
            entry.itemStackProperty().addListener(v -> writeEquipmentStack(slot, entry.getItemStack()));
            getEntries().add(entry);
        }
    }

    private ItemStack readEquipmentStack(EntityEquipmentCategoryModel.Slot slot) {
        CompoundTag equipment = data.getCompound("equipment").orElse(null);
        if (equipment == null) {
            return ItemStack.EMPTY;
        }
        CompoundTag stackTag = equipment.getCompound(slot.equipmentKey()).orElse(null);
        if (stackTag == null || stackTag.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return ClientUtil.parseItemStack(ClientUtil.registryAccess(), stackTag);
    }

    private void writeEquipmentStack(EntityEquipmentCategoryModel.Slot slot, ItemStack stack) {
        CompoundTag equipment = data.getCompound("equipment").orElseGet(() -> {
            CompoundTag created = new CompoundTag();
            data.put("equipment", created);
            return created;
        });
        if (stack.isEmpty()) {
            equipment.remove(slot.equipmentKey());
        } else {
            equipment.put(slot.equipmentKey(), ClientUtil.saveItemStack(ClientUtil.registryAccess(), stack));
        }
        if (equipment.isEmpty()) {
            data.remove("equipment");
        }
        sections.refreshPreview();
    }
}
