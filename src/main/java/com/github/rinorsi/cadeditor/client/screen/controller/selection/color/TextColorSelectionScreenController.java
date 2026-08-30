package com.github.rinorsi.cadeditor.client.screen.controller.selection.color;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.rinorsi.cadeditor.client.screen.model.selection.ColorSelectionScreenModel;
import com.github.rinorsi.cadeditor.client.screen.view.selection.color.TextColorSelectionScreenView;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;


public class TextColorSelectionScreenController extends ColorSelectionScreenController<TextColorSelectionScreenView> {
    public TextColorSelectionScreenController(ColorSelectionScreenModel model, TextColorSelectionScreenView view) {
        super(model, view);
    }

    
    @Override 
    protected void updateExample() {
        super.updateExample();
        MutableComponent componentAppend = GuapiHelper.text("Test ").append(GuapiHelper.text("Test").withStyle(ChatFormatting.BOLD)).append(GuapiHelper.text(" Test").withStyle(ChatFormatting.ITALIC));
        TextColor.parseColor(((ColorSelectionScreenModel) this.model).getHexValue()).result().ifPresent(color -> {
            componentAppend.withStyle(style -> style.withColor(color));
        });
        ((TextColorSelectionScreenView) this.view).getExampleLabel().setLabel(componentAppend);
        ((TextColorSelectionScreenView) this.view).getExampleLabel().getTooltip().setAll(componentAppend);
    }
}
