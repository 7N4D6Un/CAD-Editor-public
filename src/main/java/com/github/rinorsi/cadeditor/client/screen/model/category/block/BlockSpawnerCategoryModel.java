package com.github.rinorsi.cadeditor.client.screen.model.category.block;

import com.github.rinorsi.cadeditor.client.screen.model.BlockEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.AbstractSpawnerCategoryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.nbt.CompoundTag;

public class BlockSpawnerCategoryModel extends AbstractSpawnerCategoryModel {

    public BlockSpawnerCategoryModel(BlockEditorModel editor) {
        super(ModTexts.gui("spawner"), editor);
    }

    @Override
    protected CompoundTag tag() {
        CompoundTag tag = getContext().getTag();
        if (tag == null) {
            tag = new CompoundTag();
            getContext().setTag(tag);
        }
        return tag;
    }

    @Override
    protected void commitTag(CompoundTag tag) {
        getContext().setTag(tag);
    }
}
