package com.github.rinorsi.cadeditor.client.screen.controller.entry.item;

import com.github.rinorsi.cadeditor.client.screen.controller.entry.StringWithActionsEntryController;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.BannerLayerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.StringWithActionsEntryView;

public class BannerLayerEntryController extends StringWithActionsEntryController<BannerLayerEntryModel> {
    public BannerLayerEntryController(BannerLayerEntryModel model, StringWithActionsEntryView view) {
        super(model, view);
    }

    @Override
    public void bind() {
        super.bind();
        view.getTextField().setValidator(model.getValidator());
    }
}
