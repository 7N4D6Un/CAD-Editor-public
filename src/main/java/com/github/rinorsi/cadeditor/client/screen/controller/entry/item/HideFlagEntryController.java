package com.github.rinorsi.cadeditor.client.screen.controller.entry.item;

import com.github.rinorsi.cadeditor.client.screen.controller.entry.BooleanEntryController;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.HideFlagEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.item.HideFlagEntryView;


public class HideFlagEntryController extends BooleanEntryController<HideFlagEntryModel, HideFlagEntryView> {
    public HideFlagEntryController(HideFlagEntryModel model, HideFlagEntryView view) {
        super(model, view);
    }
}
