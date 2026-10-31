package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.level.saveddata.maps.MapId;


public class ItemMapCategoryModel extends ItemEditorCategoryModel {
    private boolean mapIdEnabled;
    private int mapIdValue;
    private boolean mapLocked;

    public ItemMapCategoryModel(ItemEditorModel editor) {
        super(ModTexts.MAP, editor);
    }

    @Override 
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        MapId idComponent = (MapId) stack.get(DataComponents.MAP_ID);
        this.mapIdEnabled = idComponent != null;
        this.mapIdValue = idComponent != null ? idComponent.id() : 0;
        this.mapLocked = stack.get(DataComponents.MAP_POST_PROCESSING) == MapPostProcessing.LOCK;
        getEntries().add(new BooleanEntryModel(this, ModTexts.MAP_ID_TOGGLE, this.mapIdEnabled, value -> this.mapIdEnabled = value != null && value));
        getEntries().add(new IntegerEntryModel(this, ModTexts.MAP_ID_VALUE, this.mapIdValue, value -> this.mapIdValue = value == null ? 0 : value));
        getEntries().add(new BooleanEntryModel(this, ModTexts.MAP_LOCK, this.mapLocked, value -> this.mapLocked = value != null && value));
    }

    @Override 
    public void apply() {
        CompoundTag components;
        super.apply();
        ItemStack stack = getParent().getContext().getItemStack();
        if (this.mapIdEnabled) {
            stack.set(DataComponents.MAP_ID, new MapId(this.mapIdValue));
        } else {
            stack.remove(DataComponents.MAP_ID);
        }
        if (this.mapLocked) {
            stack.set(DataComponents.MAP_POST_PROCESSING, MapPostProcessing.LOCK);
        } else {
            stack.remove(DataComponents.MAP_POST_PROCESSING);
        }
        CompoundTag data = getData();
        if (data != null && (components = data.getCompound("components").orElse(null)) != null) {
            components.remove("minecraft:map_id");
            components.remove("minecraft:map_post_processing");
            components.remove("minecraft:map_decorations");
            if (components.isEmpty()) {
                data.remove("components");
            }
        }
    }
}
