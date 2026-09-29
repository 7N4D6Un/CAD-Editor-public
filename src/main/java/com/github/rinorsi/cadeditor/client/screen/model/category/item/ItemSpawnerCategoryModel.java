package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.AbstractSpawnerCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.SpawnerNbtHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

public class ItemSpawnerCategoryModel extends AbstractSpawnerCategoryModel {

    private CompoundTag workingTag;

    public ItemSpawnerCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("spawner"), editor);
        ItemStack stack = editor.getContext().getItemStack();
        TypedEntityData<BlockEntityType<?>> data = stack == null ? null : stack.get(DataComponents.BLOCK_ENTITY_DATA);
        this.workingTag = data == null ? new CompoundTag() : data.getUnsafe().copy();
    }

    @Override
    protected CompoundTag tag() {
        return workingTag.copy();
    }

    @Override
    protected void commitTag(CompoundTag tag) {
        ItemStack stack = ((ItemEditorModel) getParent()).getContext().getItemStack();
        if (stack == null) {
            return;
        }
        if (SpawnerNbtHelper.isEmptyDefaultSpawner(tag)) {
            stack.remove(DataComponents.BLOCK_ENTITY_DATA);
            this.workingTag = new CompoundTag();
            return;
        }
        CompoundTag payload = tag.copy();
        payload.remove("id");
        stack.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(BlockEntityTypes.MOB_SPAWNER, payload));
        this.workingTag = payload;
    }
}
