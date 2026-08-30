package com.github.rinorsi.cadeditor.client.screen.view.entry.entity;

import com.github.franckyi.databindings.api.ObservableList;
import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.HBox;
import com.github.franckyi.guapi.api.node.ItemView;
import com.github.franckyi.guapi.api.node.Label;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.TextField;
import com.github.franckyi.guapi.api.node.TexturedButton;
import com.github.franckyi.guapi.api.node.builder.HBoxBuilder;
import com.github.franckyi.guapi.api.node.builder.ItemViewBuilder;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.view.entry.EntryView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;


public class EntityEquipmentEntryView extends EntryView {
    private Label slotLabel;
    private ItemView itemView;
    private Label itemNameLabel;
    private TextField dropChanceField;
    private TexturedButton chooseItemButton;
    private TexturedButton loadVaultButton;
    private TexturedButton clearButton;
    private TexturedButton openEditorButton;
    private TexturedButton openNbtEditorButton;
    private TexturedButton openSnbtEditorButton;

    @Override 
    protected Node createContent() {
        return GuapiHelper.hBox((Consumer<HBoxBuilder>) content -> {
            Label label = GuapiHelper.label().prefWidth(70);
            this.slotLabel = label;
            content.add(label);
            content.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) itemBox -> {
                ItemViewBuilder itemView = GuapiHelper.itemView().drawDecorations();
                this.itemView = itemView;
                itemBox.add(itemView);
                Label nameLabel = GuapiHelper.label().prefWidth(140);
                this.itemNameLabel = nameLabel;
                itemBox.add(nameLabel);
                itemBox.spacing(6).align(GuapiHelper.CENTER_LEFT);
            }), 1);
            content.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) chanceBox -> {
                chanceBox.add(GuapiHelper.label(ModTexts.DROP_CHANCE.copy().withStyle(ChatFormatting.GRAY)));
                TextField textField = GuapiHelper.textField().prefWidth(60);
                this.dropChanceField = textField;
                chanceBox.add(textField);
                chanceBox.spacing(4).align(GuapiHelper.CENTER_LEFT);
            }));
            content.spacing(8).align(GuapiHelper.CENTER_LEFT);
        });
    }

    @Override
    public void build() {
        super.build();
        HBox buttonBox = getButtonBox();
        ObservableList<Node> children = buttonBox.getChildren();
        TexturedButton chooseItemButton = GuapiHelper.texturedButton(ModTextures.SEARCH, 16, 16, false).tooltip(ModTexts.CHOOSE_ITEM);
        this.chooseItemButton = chooseItemButton;
        children.add(chooseItemButton);
        TexturedButton loadVaultButton = GuapiHelper.texturedButton(ModTextures.PASTE, 16, 16, false).tooltip(ModTexts.LOAD_VAULT);
        this.loadVaultButton = loadVaultButton;
        children.add(loadVaultButton);
        TexturedButton openEditorButton = GuapiHelper.texturedButton(ModTextures.EDITOR, 16, 16, false).tooltip(ModTexts.OPEN_EDITOR);
        this.openEditorButton = openEditorButton;
        children.add(openEditorButton);
        TexturedButton openSnbtEditorButton = GuapiHelper.texturedButton(ModTextures.SNBT_EDITOR, 16, 16, false).tooltip(ModTexts.OPEN_SNBT_EDITOR);
        this.openSnbtEditorButton = openSnbtEditorButton;
        children.add(openSnbtEditorButton);
        TexturedButton clearButton = GuapiHelper.texturedButton(ModTextures.REMOVE, 16, 16, false).tooltip(ModTexts.REMOVE);
        this.clearButton = clearButton;
        children.add(clearButton);
    }

    public Label getSlotLabel() {
        return this.slotLabel;
    }

    public ItemView getItemView() {
        return this.itemView;
    }

    public Label getItemNameLabel() {
        return this.itemNameLabel;
    }

    public TextField getDropChanceField() {
        return this.dropChanceField;
    }

    public TexturedButton getClearButton() {
        return this.clearButton;
    }

    public TexturedButton getOpenEditorButton() {
        return this.openEditorButton;
    }

    public TexturedButton getChooseItemButton() {
        return this.chooseItemButton;
    }

    public TexturedButton getLoadVaultButton() {
        return this.loadVaultButton;
    }

    public TexturedButton getOpenNbtEditorButton() {
        return this.openNbtEditorButton;
    }

    public TexturedButton getOpenSnbtEditorButton() {
        return this.openSnbtEditorButton;
    }
}
