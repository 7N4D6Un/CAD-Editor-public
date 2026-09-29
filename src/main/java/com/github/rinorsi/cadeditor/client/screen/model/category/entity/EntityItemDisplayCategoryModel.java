package com.github.rinorsi.cadeditor.client.screen.model.category.entity;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.EntityEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.entity.ItemFrameItemEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class EntityItemDisplayCategoryModel extends EntityDisplayCategoryModel {
    private ItemFrameItemEntryModel itemEntry;
    private ItemDisplayContext context;

    public EntityItemDisplayCategoryModel(EntityEditorModel editor) {
        super(editor);
    }

    @Override
    protected void setupEntries() {
        itemEntry = new ItemFrameItemEntryModel(this, readDisplayedItem(), ModTexts.ITEM_FRAME_ITEM);
        itemEntry.itemStackProperty().addListener(stack -> updateItem());
        getEntries().add(itemEntry);

        context = context(getData().getStringOr("item_display", "none"));
        EnumEntryModel<ItemDisplayContext> contextEntry = new EnumEntryModel<>(this, ModTexts.DISPLAY_CONTEXT,
                ItemDisplayContext.values(), context, this::setContext);
        contextEntry.withTextFactory(ModTexts::displayContext);
        getEntries().add(contextEntry);

        getEntries().add(new SpacerEntryModel(this));
        super.setupEntries();

        updateItem();
    }

    private ItemStack readDisplayedItem() {
        CompoundTag itemTag = getData().getCompoundOrEmpty("item");
        if (itemTag.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return ClientUtil.parseItemStack(ClientUtil.registryAccess(), itemTag);
    }

    private void updateItem() {
        if (itemEntry == null) {
            return;
        }
        ItemStack stack = itemEntry.getItemStack();
        CompoundTag data = getData();
        if (stack.isEmpty()) {
            data.remove("item");
        } else {
            data.put("item", ClientUtil.saveItemStack(ClientUtil.registryAccess(), stack));
        }
    }

    private static ItemDisplayContext context(String name) {
        for (ItemDisplayContext context : ItemDisplayContext.values()) {
            if (context.getSerializedName().equals(name)) {
                return context;
            }
        }
        return ItemDisplayContext.NONE;
    }

    private void setContext(ItemDisplayContext value) {
        CompoundTag data = getData();
        if (value == null || value == ItemDisplayContext.NONE) {
            data.remove("item_display");
        } else {
            data.putString("item_display", value.getSerializedName());
        }
    }

    @Override
    public void apply() {
        super.apply();
        if (itemEntry != null) {
            itemEntry.apply();
        }
        updateItem();
    }
}
