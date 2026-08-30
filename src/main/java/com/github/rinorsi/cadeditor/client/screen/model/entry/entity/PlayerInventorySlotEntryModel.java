package com.github.rinorsi.cadeditor.client.screen.model.entry.entity;

import com.github.rinorsi.cadeditor.client.screen.model.category.EditorCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.ItemContainerSlotEntryModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;


public class PlayerInventorySlotEntryModel extends ItemContainerSlotEntryModel {
    private final int slotId;
    private final Component slotLabel;

    public PlayerInventorySlotEntryModel(EditorCategoryModel category, Component slotLabel, int slotId, ItemStack stack) {
        super(category, stack);
        this.slotId = slotId;
        this.slotLabel = slotLabel;
    }

    public int getSlotId() {
        return this.slotId;
    }

    @Override 
    public Component getSlotLabel() {
        return this.slotLabel;
    }
}
