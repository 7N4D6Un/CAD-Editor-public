package com.github.rinorsi.cadeditor.client.screen.model;

import com.github.rinorsi.cadeditor.client.context.BlockEditorContext;
import com.github.rinorsi.cadeditor.client.screen.model.category.block.BlockBeehiveCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.block.BlockContainerCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.block.BlockContainerGridCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.block.BlockEntityDataCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.block.BlockSpawnerCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.block.BlockStateCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.block.BlockTrialSpawnerCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.block.BlockCommandBlockCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.block.BlockSignCategoryModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.BeehiveBlock;

public class BlockEditorModel extends StandardEditorModel {
    public BlockEditorModel(BlockEditorContext context) {
        super(context);
    }

    @Override
    public BlockEditorContext getContext() {
        return (BlockEditorContext) super.getContext();
    }

    @Override
    protected void setupCategories() {
        if (!getContext().getBlockState().getProperties().isEmpty()) {
            getCategories().add(new BlockStateCategoryModel(this));
        }
        if (isSpawnerBlock()) {
            getCategories().add(new BlockSpawnerCategoryModel(this));
        }
        if (isTrialSpawnerBlock()) {
            getCategories().add(new BlockTrialSpawnerCategoryModel(this));
        }
        if (getContext().getBlockEntity() instanceof BaseContainerBlockEntity) {
            getCategories().add(new BlockContainerCategoryModel(this));
            getCategories().add(new BlockContainerGridCategoryModel(this));
        }
        if (getContext().getBlockEntity() != null) {
            getCategories().add(new BlockEntityDataCategoryModel(this));
        }
        if (isBeehiveBlock()) {
            getCategories().add(new BlockBeehiveCategoryModel(this));
        }
        if (isCommandBlock()) {
            getCategories().add(new BlockCommandBlockCategoryModel(this));
        }
        if (isSignBlock()) {
            getCategories().add(new BlockSignCategoryModel(this));
        }
    }

    private boolean isSpawnerBlock() {
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(getContext().getBlockState().getBlock());
        return blockId != null && "minecraft".equals(blockId.getNamespace()) && "spawner".equals(blockId.getPath());
    }

    private boolean isTrialSpawnerBlock() {
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(getContext().getBlockState().getBlock());
        return blockId != null && "minecraft".equals(blockId.getNamespace()) && "trial_spawner".equals(blockId.getPath());
    }

    private boolean isBeehiveBlock() {
        return getContext().getBlockState().getBlock() instanceof BeehiveBlock;
    }

    private boolean isCommandBlock() {
        return getContext().getBlockEntity() instanceof net.minecraft.world.level.block.entity.CommandBlockEntity;
    }

    private boolean isSignBlock() {
        return getContext().getBlockEntity() instanceof net.minecraft.world.level.block.entity.SignBlockEntity;
    }
}
