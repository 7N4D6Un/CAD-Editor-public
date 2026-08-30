package com.github.rinorsi.cadeditor.client.screen.view.entry;

import com.github.franckyi.guapi.api.node.Button;
import com.github.franckyi.guapi.api.node.HBox;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.TextField;
import com.github.franckyi.guapi.api.node.TexturedButton;
import com.github.franckyi.guapi.api.node.builder.TexturedButtonBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

import static com.github.franckyi.guapi.api.GuapiHelper.*;

public class StringWithActionsEntryView extends LabeledEntryView {
    private TextField textField;
    private HBox container;
    private final List<Node> buttons = new ArrayList<>();

    @Override
    protected Node createLabeledContent() {
        return container = hBox(box -> {
            box.add(textField = textField().prefHeight(16), 1);
            box.align(CENTER).spacing(4);
        });
    }

    public TextField getTextField() {
        return textField;
    }

    public void addButton(Identifier icon, MutableComponent tooltip, Runnable action) {
        TexturedButtonBuilder button = texturedButton(icon, 16, 16, false);
        if (tooltip != null) {
            button.tooltip(tooltip);
        }
        if (action != null) {
            button.onAction(action);
        }
        TexturedButton texturedButton = button;
        container.getChildren().add(texturedButton);
        buttons.add(texturedButton);
    }

    public void addButton(Component text, MutableComponent tooltip, Runnable action) {
        Button built = button(text).prefHeight(16).prefWidth(52);
        if (tooltip != null) {
            built.getTooltip().add(tooltip);
        }
        if (action != null) {
            built.onAction(action);
        }
        container.getChildren().add(built);
        buttons.add(built);
    }

    public List<Node> getButtons() {
        return buttons;
    }
}
