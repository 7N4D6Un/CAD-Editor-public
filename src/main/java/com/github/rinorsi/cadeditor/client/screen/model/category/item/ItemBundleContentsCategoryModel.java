package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.ItemContainerSlotEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;


public class ItemBundleContentsCategoryModel extends ItemEditorCategoryModel {
    public ItemBundleContentsCategoryModel(ItemEditorModel editor) {
        super(ModTexts.BUNDLE_CONTENTS, editor);
    }

    @Override 
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (contents != null && !contents.isEmpty()) {
            contents.itemCopyStream().forEach(item -> {
                getEntries().add(new ItemContainerSlotEntryModel(this, item));
            });
        } else {
            getEntries().add(new ItemContainerSlotEntryModel(this, ItemStack.EMPTY));
        }
    }

    @Override 
    public int getEntryListStart() {
        return 0;
    }

    @Override 
    public EntryModel createNewListEntry() {
        return new ItemContainerSlotEntryModel(this, ItemStack.EMPTY);
    }

    @Override 
    public void apply() {
        super.apply();
        ItemStack stack = getParent().getContext().getItemStack();
        List<ItemStackTemplate> items = new ArrayList<>();
        boolean hasInvalid = false;
        for (EntryModel entry : getEntries()) {
            if (entry instanceof ItemContainerSlotEntryModel) {
                ItemContainerSlotEntryModel slotEntry = (ItemContainerSlotEntryModel) entry;
                ItemStack value = slotEntry.getItemStack();
                if (value.isEmpty()) {
                    slotEntry.setValid(true);
                } else {
                    ItemStack copy = value.copy();
                    if (BundleContents.canItemBeInBundle(copy)) {
                        items.add(ItemStackTemplate.fromNonEmptyStack(copy));
                        slotEntry.setValid(true);
                    } else {
                        slotEntry.setValid(false);
                        hasInvalid = true;
                    }
                }
            }
        }
        if (hasInvalid) {
            return;
        }
        if (items.isEmpty()) {
            stack.remove(DataComponents.BUNDLE_CONTENTS);
        } else {
            stack.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(items));
        }
        cleanComponentTag();
    }

    private void cleanComponentTag() {
        CompoundTag components;
        CompoundTag data = getData();
        if (data == null || (components = data.getCompound("components").orElse(null)) == null) {
            return;
        }
        components.remove("minecraft:bundle_contents");
        if (components.isEmpty()) {
            data.remove("components");
        }
    }
}
