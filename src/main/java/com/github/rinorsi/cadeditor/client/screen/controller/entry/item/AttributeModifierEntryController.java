package com.github.rinorsi.cadeditor.client.screen.controller.entry.item;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.screen.controller.entry.EntryController;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.AttributeModifierEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.item.AttributeModifierEntryView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;


public class AttributeModifierEntryController extends EntryController<AttributeModifierEntryModel, AttributeModifierEntryView> {
    public AttributeModifierEntryController(AttributeModifierEntryModel model, AttributeModifierEntryView view) {
        super(model, view);
    }

    
    @Override 
    public void bind() {
        super.bind();
        ((AttributeModifierEntryView) this.view).getAmountField().textProperty().addListener(value -> {
            if (((AttributeModifierEntryView) this.view).getAmountField().isValid()) {
                ((AttributeModifierEntryModel) this.model).setAmount(Double.parseDouble(value));
            }
        });
        ((AttributeModifierEntryModel) this.model).amountProperty().addListener(newValue -> {
            ((AttributeModifierEntryView) this.view).getAmountField().setText(String.valueOf(newValue));
        });
        ((AttributeModifierEntryView) this.view).getAttributeNameField().textProperty().bindBidirectional(((AttributeModifierEntryModel) this.model).attributeNameProperty());
        ((AttributeModifierEntryView) this.view).getSlotButton().valueProperty().bindBidirectional(((AttributeModifierEntryModel) this.model).slotProperty());
        ((AttributeModifierEntryView) this.view).getOperationButton().valueProperty().bindBidirectional(((AttributeModifierEntryModel) this.model).operationProperty());
        ((AttributeModifierEntryView) this.view).getAmountField().setText(Double.toString(((AttributeModifierEntryModel) this.model).getAmount()));
        ((AttributeModifierEntryView) this.view).getAmountField().validProperty().addListener(((AttributeModifierEntryModel) this.model)::setValid);
        ((AttributeModifierEntryView) this.view).getAttributeNameField().getSuggestions().setAll(ClientCache.getAttributeSuggestions());
        ((AttributeModifierEntryView) this.view).getAttributeListButton().onAction(this::openAttributeList);
        ((AttributeModifierEntryView) this.view).getInfinityAmountButton().onAction(() -> {
            ((AttributeModifierEntryView) this.view).getAmountField().setText("Infinity");
        });
        ((AttributeModifierEntryView) this.view).getNegativeInfinityAmountButton().onAction(() -> {
            ((AttributeModifierEntryView) this.view).getAmountField().setText("-Infinity");
        });
        ((AttributeModifierEntryModel) this.model).attributeNameProperty().addListener(this::updateAttributePreview);
        updateAttributePreview(((AttributeModifierEntryModel) this.model).getAttributeName());
    }

    
    private void openAttributeList() {
        MutableComponent attributeLabel = ModTexts.ATTRIBUTE;
        String attributeName = ((AttributeModifierEntryModel) this.model).getAttributeName().contains(":") ? ((AttributeModifierEntryModel) this.model).getAttributeName() : "minecraft:" + ((AttributeModifierEntryModel) this.model).getAttributeName();
        List<ListSelectionElementModel> attributeSelectionItems = ClientCache.getAttributeSelectionItems();
        ModScreenHandler.openListSelectionScreen(attributeLabel, attributeName, attributeSelectionItems, ((AttributeModifierEntryModel) this.model)::setAttributeName);
    }

    private void updateAttributePreview(String value) {
        Identifier id = ClientUtil.parseResourceLocation(value);
        if (id == null) {
            ((AttributeModifierEntryView) this.view).getAttributePreviewLabel().setVisible(false);
        } else {
            ClientCache.findAttributeSelectionItem(id).ifPresentOrElse(item -> {
                ((AttributeModifierEntryView) this.view).getAttributePreviewLabel().setLabel(GuapiHelper.translated(item.getName()).withStyle(ChatFormatting.GRAY));
                ((AttributeModifierEntryView) this.view).getAttributePreviewLabel().setVisible(true);
            }, () -> {
                ((AttributeModifierEntryView) this.view).getAttributePreviewLabel().setVisible(false);
            });
        }
    }
}
