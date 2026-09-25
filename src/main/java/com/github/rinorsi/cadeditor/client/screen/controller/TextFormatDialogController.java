package com.github.rinorsi.cadeditor.client.screen.controller;

import com.github.franckyi.guapi.api.Guapi;
import com.github.franckyi.guapi.api.mvc.AbstractController;
import com.github.rinorsi.cadeditor.client.screen.model.TextFormatDialogModel;
import com.github.rinorsi.cadeditor.client.screen.view.TextFormatDialogView;
import com.github.rinorsi.cadeditor.common.ModTexts;

public class TextFormatDialogController extends AbstractController<TextFormatDialogModel, TextFormatDialogView> {
    public TextFormatDialogController(TextFormatDialogModel model, TextFormatDialogView view) {
        super(model, view);
    }

    @Override
    public void bind() {
        view.getHeaderLabel().setLabel(ModTexts.title(model.getTitle()));
        view.getCancelButton().onAction(Guapi.getScreenHandler()::hideScene);
        view.getDoneButton().onAction(this::confirm);
    }

    private void confirm() {
        Guapi.getScreenHandler().hideScene();
        model.apply(view.collectValues());
    }
}
