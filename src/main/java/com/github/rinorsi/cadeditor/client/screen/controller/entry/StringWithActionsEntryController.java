package com.github.rinorsi.cadeditor.client.screen.controller.entry;

import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.StringWithActionsEntryView;
import net.minecraft.network.chat.Component;

public class StringWithActionsEntryController<M extends StringWithActionsEntryModel> extends ValueEntryController<M, StringWithActionsEntryView> {
    public StringWithActionsEntryController(M model, StringWithActionsEntryView view) {
        super(model, view);
    }

    @Override
    public void bind() {
        super.bind();
        view.getTextField().setText(model.getValue());
        view.getTextField().textProperty().addListener(model::setValue);
        model.valueProperty().addListener(value -> {
            String safe = value == null ? "" : value;
            if (!safe.equals(view.getTextField().getText())) {
                view.getTextField().setText(safe);
            }
        });
        view.getTextField().validProperty().addListener(model::setValid);
        String placeholder = model.getPlaceholder();
        if (placeholder != null) {
            view.getTextField().setPlaceholder(Component.literal(placeholder));
        }
        for (var button : model.getButtons()) {
            if (button.text() != null) {
                view.addButton(button.text(), button.tooltip(), button.action());
            } else {
                view.addButton(button.icon(), button.tooltip(), button.action());
            }
        }
    }
}
