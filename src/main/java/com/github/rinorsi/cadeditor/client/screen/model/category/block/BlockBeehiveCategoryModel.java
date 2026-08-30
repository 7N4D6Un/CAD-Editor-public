package com.github.rinorsi.cadeditor.client.screen.model.category.block;

import com.github.rinorsi.cadeditor.client.screen.model.BlockEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.BeehiveNbtHelper;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.BeehiveBlock;

public class BlockBeehiveCategoryModel extends BlockEditorCategoryModel {
    private IntegerEntryModel beeCountEntry;
    private IntegerEntryModel honeyLevelEntry;

    public BlockBeehiveCategoryModel(BlockEditorModel editor) {
        super(ModTexts.gui("beehive"), editor);
    }

    @Override
    protected void setupEntries() {
        int beeCount = 0;
        CompoundTag data = getData();
        if (data != null) {
            beeCount = data.getListOrEmpty(BeehiveNbtHelper.KEY_BEES).size();
        }
        int honey = getBlockState().getValue(BeehiveBlock.HONEY_LEVEL);
        this.beeCountEntry = new IntegerEntryModel(this, ModTexts.gui("bee_count"), Math.max(0, beeCount), value -> {
        }, BeehiveNbtHelper.INT_COUNT);
        this.honeyLevelEntry = new IntegerEntryModel(this, ModTexts.gui("honey_level"), honey, value -> {
        }, BeehiveNbtHelper.INT_HONEY);
        getEntries().add(this.beeCountEntry);
        getEntries().add(this.honeyLevelEntry);
    }

    @Override
    public void apply() {
        super.apply();
        CompoundTag data = getData();
        int targetBees = Math.max(0, this.beeCountEntry.getValue());
        ListTag bees = data == null ? new ListTag() : data.getListOrEmpty(BeehiveNbtHelper.KEY_BEES);
        ListTag resized = BeehiveNbtHelper.resizeBees(bees, targetBees);
        if (data == null) {
            data = new CompoundTag();
            getContext().setTag(data);
        }
        data.put(BeehiveNbtHelper.KEY_BEES, resized);
        int honey = Math.max(0, Math.min(5, this.honeyLevelEntry.getValue()));
        updateState(BeehiveBlock.HONEY_LEVEL, honey);
    }
}