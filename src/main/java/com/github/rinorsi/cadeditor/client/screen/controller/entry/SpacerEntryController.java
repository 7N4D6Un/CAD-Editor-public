package com.github.rinorsi.cadeditor.client.screen.controller.entry;

import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.SpacerEntryView;

public class SpacerEntryController extends EntryController<SpacerEntryModel, SpacerEntryView> {
    public SpacerEntryController(SpacerEntryModel model, SpacerEntryView view) {
        super(model, view);
    }

    @Override
    public void bind() {
        super.bind();
        view.getButtonBox().setVisible(false);
    }
}