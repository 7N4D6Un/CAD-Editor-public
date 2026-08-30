package com.github.rinorsi.cadeditor.client.screen.model.category.config;

import com.github.franckyi.databindings.api.ObservableList;
import com.github.rinorsi.cadeditor.client.screen.model.ConfigEditorScreenModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.ActionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.common.CommonConfiguration;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.network.chat.MutableComponent;


public class CommonConfigCategoryModel extends ConfigCategoryModel {
    private IntegerEntryModel permissionLevelEntry;
    private BooleanEntryModel creativeOnly;

    public CommonConfigCategoryModel(ConfigEditorScreenModel editor) {
        super(ModTexts.COMMON, editor);
    }

    @Override 
    protected void setupEntries() {
        ObservableList<EntryModel> entries = getEntries();
        MutableComponent permissionLevelLabel = ModTexts.PERMISSION_LEVEL;
        int permissionLevel = CommonConfiguration.INSTANCE.getPermissionLevel();
        IntegerEntryModel integerEntryModel = new IntegerEntryModel(this, permissionLevelLabel, permissionLevel, CommonConfiguration.INSTANCE::setPermissionLevel).withWeight(2);
        this.permissionLevelEntry = integerEntryModel;
        MutableComponent creativeOnlyLabel = ModTexts.CREATIVE_ONLY;
        boolean isCreativeOnly = CommonConfiguration.INSTANCE.isCreativeOnly();
        BooleanEntryModel booleanEntryModel = new BooleanEntryModel(this, creativeOnlyLabel, isCreativeOnly, CommonConfiguration.INSTANCE::setCreativeOnly).withWeight(2);
        this.creativeOnly = booleanEntryModel;
        entries.addAll(integerEntryModel, booleanEntryModel, new ActionEntryModel(this, ModTexts.RELOAD_CONFIG, this::reload));
        CommonConfiguration defaults = CommonConfiguration.defaults();
        integerEntryModel.setFactoryDefault(defaults.getPermissionLevel());
        booleanEntryModel.setFactoryDefault(defaults.isCreativeOnly());
    }

    private void reload() {
        CommonConfiguration.load();
        syncEntries();
    }

    public void syncEntries() {
        this.permissionLevelEntry.setValue(CommonConfiguration.INSTANCE.getPermissionLevel());
        this.creativeOnly.setValue(CommonConfiguration.INSTANCE.isCreativeOnly());
        this.permissionLevelEntry.markClean();
        this.creativeOnly.markClean();
    }

    @Override 
    public void apply() {
        super.apply();
        CommonConfiguration.save();
    }
}
