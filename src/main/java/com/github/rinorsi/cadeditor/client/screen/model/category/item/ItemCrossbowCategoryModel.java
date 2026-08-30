package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.ItemContainerSlotEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;

import java.util.ArrayList;
import java.util.List;

public class ItemCrossbowCategoryModel extends ItemEditorCategoryModel {
    public ItemCrossbowCategoryModel(ItemEditorModel editor) {
        super(ModTexts.CROSSBOW, editor);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        ChargedProjectiles charged = stack.get(DataComponents.CHARGED_PROJECTILES);
        if (charged != null && !charged.isEmpty()) {
            charged.itemCopies().forEach(item -> getEntries().add(new ItemContainerSlotEntryModel(this, item)));
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
        List<ItemStack> projectiles = new ArrayList<>();
        for (EntryModel entry : getEntries()) {
            if (entry instanceof ItemContainerSlotEntryModel projectile) {
                ItemStack value = projectile.getItemStack();
                if (!value.isEmpty()) {
                    projectiles.add(value.copy());
                }
            }
        }
        if (projectiles.isEmpty()) {
            stack.remove(DataComponents.CHARGED_PROJECTILES);
        } else {
            stack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(projectiles));
        }
        CompoundTag data = getData();
        if (data == null) return;
        CompoundTag components = data.getCompound("components").orElse(null);
        if (components == null) {
            return;
        }
        components.remove("minecraft:charged_projectiles");
        if (components.isEmpty()) {
            data.remove("components");
        }
    }
}
