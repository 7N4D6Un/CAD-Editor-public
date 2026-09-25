package com.github.rinorsi.cadeditor.client.screen.mvc;

import com.github.franckyi.guapi.api.mvc.MVC;
import com.github.rinorsi.cadeditor.client.screen.controller.TextFormatDialogController;
import com.github.rinorsi.cadeditor.client.screen.model.TextFormatDialogModel;
import com.github.rinorsi.cadeditor.client.screen.view.TextFormatDialogView;

public final class TextFormatDialogMVC implements MVC<TextFormatDialogModel, TextFormatDialogView, TextFormatDialogController> {
    public static final TextFormatDialogMVC INSTANCE = new TextFormatDialogMVC();

    private TextFormatDialogMVC() {
    }

    @Override
    public TextFormatDialogView setup(TextFormatDialogModel model) {
        return MVC.createViewAndBind(model, () -> new TextFormatDialogView(model.getFields()), TextFormatDialogController::new);
    }
}
