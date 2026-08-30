package com.github.rinorsi.cadeditor.client.screen.controller;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.mvc.AbstractController;
import com.github.franckyi.guapi.api.node.TexturedButton;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.screen.model.NBTTagModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.StringSuggestionListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.view.NBTTagView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public class NBTTagController extends AbstractController<NBTTagModel, NBTTagView> {
    public NBTTagController(NBTTagModel model, NBTTagView view) {
        super(model, view);
    }

    
    @Override 
    public void bind() {
        ((NBTTagView) this.view).getNameField().textProperty().bindBidirectional(((NBTTagModel) this.model).nameProperty());
        ((NBTTagView) this.view).getValueField().textProperty().bindBidirectional(((NBTTagModel) this.model).valueProperty());
        if (((NBTTagModel) this.model).getName() == null) {
            ((NBTTagView) this.view).getRoot().getChildren().removeAll(((NBTTagView) this.view).getNameField(), ((NBTTagView) this.view).getSeparator());
            if (((NBTTagModel) this.model).getParent() == null) {
                ((NBTTagView) this.view).getRoot().getChildren().add(GuapiHelper.label((Component) GuapiHelper.text("(root)")));
            } else {
                ((NBTTagView) this.view).getRoot().getChildren().add(1, GuapiHelper.label((Component) GuapiHelper.text("(%d)".formatted(((NBTTagModel) this.model).getParent().getChildren().indexOf(this.model)))));
            }
        }
        if (((NBTTagModel) this.model).getValue() == null) {
            ((NBTTagView) this.view).getRoot().getChildren().remove(((NBTTagView) this.view).getValueField());
            ((NBTTagView) this.view).getRoot().getChildren().remove(((NBTTagView) this.view).getSuggestionButton());
            ((NBTTagView) this.view).getValueField().getSuggestions().clear();
            ((NBTTagView) this.view).getSuggestionButton().setDisable(true);
            ((NBTTagView) this.view).getSuggestionButton().setVisible(false);
            return;
        }
        if (!((NBTTagView) this.view).getRoot().getChildren().contains(((NBTTagView) this.view).getValueField())) {
            int insertIndex = ((NBTTagView) this.view).getRoot().getChildren().indexOf(((NBTTagView) this.view).getSuggestionButton());
            if (insertIndex < 0) {
                insertIndex = ((NBTTagView) this.view).getRoot().getChildren().size();
            }
            ((NBTTagView) this.view).getRoot().getChildren().add(insertIndex, ((NBTTagView) this.view).getValueField());
        }
        ((NBTTagModel) this.model).validProperty().bind(((NBTTagView) this.view).getValueField().validProperty());
        ((NBTTagView) this.view).getRoot().getChildren().remove(((NBTTagView) this.view).getSuggestionButton());
        if (((NBTTagModel) this.model).getTagType() == 8) {
            List<String> suggestions = ((NBTTagModel) this.model).getStringSuggestions();
            if (!suggestions.isEmpty()) {
                List<String> distinctSuggestions = suggestions.stream().filter(value -> Objects.nonNull(value)).distinct().toList();
                if (distinctSuggestions.isEmpty()) {
                    ((NBTTagView) this.view).getValueField().getSuggestions().clear();
                    ((NBTTagView) this.view).getSuggestionButton().setDisable(true);
                    ((NBTTagView) this.view).getSuggestionButton().setVisible(false);
                    return;
                }
                ((NBTTagView) this.view).getValueField().getSuggestions().setAll(distinctSuggestions);
                TexturedButton suggestionButton = ((NBTTagView) this.view).getSuggestionButton();
                if (!((NBTTagView) this.view).getRoot().getChildren().contains(suggestionButton)) {
                    ((NBTTagView) this.view).getRoot().getChildren().add(suggestionButton);
                }
                suggestionButton.setDisable(false);
                suggestionButton.setVisible(true);
                suggestionButton.getTooltip().setAll(List.of(ModTexts.SEARCH));
                List<StringSuggestionListSelectionElementModel> items = (List) distinctSuggestions.stream().map(StringSuggestionListSelectionElementModel::new).collect(Collectors.toList());
                Map<String, String> idToValue = new LinkedHashMap<>();
                items.forEach(item -> {
                    idToValue.put(item.getId().toString(), item.getValue());
                });
                suggestionButton.onAction(() -> {
                    String currentId = (String) idToValue.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), ((NBTTagModel) this.model).getValue())).map(value -> value.getKey()).findFirst().orElse("");
                    MutableComponent title = ((NBTTagModel) this.model).getName() != null ? GuapiHelper.text(((NBTTagModel) this.model).getName()) : GuapiHelper.text("value");
                    ModScreenHandler.openListSelectionScreen(title, currentId, items, selectedId -> {
                        String selectedValue = (String) idToValue.get(selectedId);
                        if (selectedValue != null) {
                            ((NBTTagView) this.view).getValueField().setText(selectedValue);
                        }
                    });
                });
                return;
            }
            ((NBTTagView) this.view).getValueField().getSuggestions().clear();
            ((NBTTagView) this.view).getSuggestionButton().setDisable(true);
            ((NBTTagView) this.view).getSuggestionButton().setVisible(false);
            return;
        }
        ((NBTTagView) this.view).getValueField().getSuggestions().clear();
        ((NBTTagView) this.view).getSuggestionButton().setDisable(true);
        ((NBTTagView) this.view).getSuggestionButton().setVisible(false);
    }
}
