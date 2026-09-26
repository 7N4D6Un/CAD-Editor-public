package com.github.rinorsi.cadeditor.client.screen.model.category.entity;

import com.github.rinorsi.cadeditor.client.screen.model.EntityEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.ArmorStandSections;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntityPreviewEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;

public class EntityArmorStandCategoryModel extends EntityCategoryModel {
    private final ArmorStandSections sections;

    public EntityArmorStandCategoryModel(EntityEditorModel model) {
        super(ModTexts.gui("armor_stand"), model);
        sections = new ArmorStandSections(this, this::getData);
    }

    @Override
    protected void setupEntries() {
        getEntries().add(sections.buildPreviewEntry());
        sections.refreshPreview();

        getEntries().addAll(sections.buildPoseEntries());

        getEntries().add(new SpacerEntryModel(this));
        getEntries().addAll(sections.buildFlagEntries());

        getEntries().add(new SpacerEntryModel(this));
        getEntries().addAll(sections.buildDisabledSlotEntries());
    }

    @Override
    public int getEntryHeight(EntryModel entry) {
        return entry instanceof EntityPreviewEntryModel ? ArmorStandSections.PREVIEW_HEIGHT : getEntryHeight();
    }
}
