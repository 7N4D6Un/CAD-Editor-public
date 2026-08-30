package com.github.rinorsi.cadeditor.client.screen.controller.entry.item;

import com.github.franckyi.guapi.api.Color;
import com.github.franckyi.guapi.api.node.TexturedButton;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.screen.controller.entry.LabeledEntryController;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.FireworkColorEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.ColorSelectionScreenModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.item.FireworkColorEntryView;
import com.github.rinorsi.cadeditor.common.ColoredItemHelper;
import net.minecraft.network.chat.Component;


public class FireworkColorEntryController extends LabeledEntryController<FireworkColorEntryModel, FireworkColorEntryView> {
    public FireworkColorEntryController(FireworkColorEntryModel model, FireworkColorEntryView view) {
        super(model, view);
    }

    
    @Override 
    public void bind() {
        super.bind();
        ((FireworkColorEntryView) this.view).getChooseColorButton().onAction(() -> {
            ModScreenHandler.openColorSelectionScreen(ColorSelectionScreenModel.Target.TEXT, (Integer) ((FireworkColorEntryModel) this.model).getValue(), hex -> {
                ((FireworkColorEntryModel) this.model).setValue(Color.fromHex(hex));
            });
        });
        TexturedButton removeColorButton = ((FireworkColorEntryView) this.view).getRemoveColorButton();
        removeColorButton.onAction(((FireworkColorEntryModel) this.model)::remove);
        ((FireworkColorEntryModel) this.model).valueProperty().addListener(this::updateColorPreview);
        updateColorPreview();
    }

    
    private void updateColorPreview() {
        int color = (Integer) ((FireworkColorEntryModel) this.model).getValue();
        ((FireworkColorEntryView) this.view).getPreviewItem().setItem(ColoredItemHelper.createFireworkStarItem(color));
        String hexText = ((FireworkColorEntryModel) this.model).hasCustomColor() ? String.format("#%06X", color & 16777215) : "-";
        ((FireworkColorEntryView) this.view).getHexLabel().setLabel(Component.literal(hexText));
    }
}
