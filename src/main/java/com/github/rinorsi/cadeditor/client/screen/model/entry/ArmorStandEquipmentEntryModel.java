package com.github.rinorsi.cadeditor.client.screen.model.entry;

import com.github.franckyi.databindings.api.ObjectProperty;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemArmorStandCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.entity.EntityEquipmentCategoryModel;
import net.minecraft.world.item.ItemStack;

public class ArmorStandEquipmentEntryModel extends EntryModel {
    private final ItemArmorStandCategoryModel category;
    private final EntityEquipmentCategoryModel.Slot slot;
    private final ObjectProperty<ItemStack> itemProperty = ObjectProperty.create();

    public ArmorStandEquipmentEntryModel(ItemArmorStandCategoryModel category, EntityEquipmentCategoryModel.Slot slot, ItemStack stack) {
        super(category);
        this.category = category;
        this.slot = slot;
        this.itemProperty.setValue(sanitize(stack));
        setReorderable(false);
    }

    public EntityEquipmentCategoryModel.Slot getSlot() {
        return slot;
    }

    public ItemStack getItemStack() {
        ItemStack current = itemProperty.getValue();
        return current == null ? ItemStack.EMPTY : current;
    }

    public ObjectProperty<ItemStack> itemStackProperty() {
        return itemProperty;
    }

    public void setItemStack(ItemStack stack) {
        itemProperty.setValue(sanitize(stack));
    }

    private ItemStack sanitize(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = stack.copy();
        if (copy.getCount() <= 0) {
            copy.setCount(1);
        }
        return copy;
    }

    @Override
    public void apply() {
    }

    @Override
    public boolean isDeletable() {
        return false;
    }

    @Override
    public boolean isResetable() {
        return false;
    }

    @Override
    public Type getType() {
        return Type.ARMOR_STAND_EQUIPMENT;
    }
}
