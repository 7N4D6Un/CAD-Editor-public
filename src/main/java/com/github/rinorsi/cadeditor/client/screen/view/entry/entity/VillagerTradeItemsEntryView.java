package com.github.rinorsi.cadeditor.client.screen.view.entry.entity;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.ItemView;
import com.github.franckyi.guapi.api.node.Label;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.TexturedButton;
import com.github.franckyi.guapi.api.node.builder.HBoxBuilder;
import com.github.franckyi.guapi.api.node.builder.ItemViewBuilder;
import com.github.franckyi.guapi.api.node.builder.VBoxBuilder;
import com.github.franckyi.guapi.api.util.Align;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.view.entry.EntryView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public class VillagerTradeItemsEntryView extends EntryView {
    private static final int SECTION_SPACING = 20;

    public static final int ENTRY_HEIGHT = 210;
    private static final int BUTTON_GROUP_WIDTH = 120;
    private static final int NAME_LABEL_WIDTH = 100;
    private static final int ROW_HEIGHT = 24;
    private Label tradeTitleLabel;
    private ItemView primaryItemView;
    private Label primaryItemNameLabel;
    private TexturedButton primaryChooseButton;
    private TexturedButton primaryEditButton;
    private TexturedButton primarySnbtButton;
    private TexturedButton primaryClearButton;
    private TexturedButton primaryVaultButton;
    private ItemView secondaryItemView;
    private Label secondaryItemNameLabel;
    private TexturedButton secondaryChooseButton;
    private TexturedButton secondaryEditButton;
    private TexturedButton secondarySnbtButton;
    private TexturedButton secondaryClearButton;
    private TexturedButton secondaryVaultButton;
    private ItemView resultItemView;
    private Label resultItemNameLabel;
    private TexturedButton resultChooseButton;
    private TexturedButton resultEditButton;
    private TexturedButton resultSnbtButton;
    private TexturedButton resultClearButton;
    private TexturedButton resultVaultButton;

    @Override 
    public void build() {
        super.build();
        getRoot().setAlignment(Align.TOP_LEFT);
        getRoot().setSpacing(4);
        getRoot().setMinHeight(210);
        getRoot().setPrefHeight(210);
        getRoot().setMaxHeight(210);
        getRight().setAlignment(Align.TOP_RIGHT);
    }

    @Override
    protected Node createContent() {
        return GuapiHelper.vBox((Consumer<VBoxBuilder>) root -> {
            Label label = GuapiHelper.label().prefWidth(0);
            this.tradeTitleLabel = label;
            root.add(label);
            root.add(createItemSection(ModTexts.TRADE_INPUT_PRIMARY.copy(), () -> {
                ItemViewBuilder builder = GuapiHelper.itemView().prefWidth(ROW_HEIGHT).prefHeight(ROW_HEIGHT).drawDecorations();
                this.primaryItemView = builder;
                return builder;
            }, () -> {
                Label nameLabel = GuapiHelper.label().prefWidth(0);
                this.primaryItemNameLabel = nameLabel;
                return nameLabel;
            }, buttons -> {
                TexturedButton vaultButton = GuapiHelper.texturedButton(ModTextures.PASTE, 16, 16, false).tooltip(ModTexts.LOAD_VAULT);
                this.primaryVaultButton = vaultButton;
                buttons.add(vaultButton);
                TexturedButton chooseButton = GuapiHelper.texturedButton(ModTextures.SEARCH, 16, 16, false).tooltip(ModTexts.CHOOSE_ITEM);
                this.primaryChooseButton = chooseButton;
                buttons.add(chooseButton);
                TexturedButton editButton = GuapiHelper.texturedButton(ModTextures.EDITOR, 16, 16, false).tooltip(ModTexts.OPEN_EDITOR);
                this.primaryEditButton = editButton;
                buttons.add(editButton);
                TexturedButton snbtButton = GuapiHelper.texturedButton(ModTextures.SNBT_EDITOR, 16, 16, false).tooltip(ModTexts.OPEN_SNBT_EDITOR);
                this.primarySnbtButton = snbtButton;
                buttons.add(snbtButton);
                TexturedButton clearButton = GuapiHelper.texturedButton(ModTextures.REMOVE, 16, 16, false).tooltip(ModTexts.REMOVE);
                this.primaryClearButton = clearButton;
                buttons.add(clearButton);
            }));
            root.add(createItemSection(ModTexts.TRADE_INPUT_SECONDARY.copy(), () -> {
                ItemViewBuilder builder = GuapiHelper.itemView().prefWidth(ROW_HEIGHT).prefHeight(ROW_HEIGHT).drawDecorations();
                this.secondaryItemView = builder;
                return builder;
            }, () -> {
                Label nameLabel = GuapiHelper.label().prefWidth(0);
                this.secondaryItemNameLabel = nameLabel;
                return nameLabel;
            }, buttons -> {
                TexturedButton vaultButton = GuapiHelper.texturedButton(ModTextures.PASTE, 16, 16, false).tooltip(ModTexts.LOAD_VAULT);
                this.secondaryVaultButton = vaultButton;
                buttons.add(vaultButton);
                TexturedButton chooseButton = GuapiHelper.texturedButton(ModTextures.SEARCH, 16, 16, false).tooltip(ModTexts.CHOOSE_ITEM);
                this.secondaryChooseButton = chooseButton;
                buttons.add(chooseButton);
                TexturedButton editButton = GuapiHelper.texturedButton(ModTextures.EDITOR, 16, 16, false).tooltip(ModTexts.OPEN_EDITOR);
                this.secondaryEditButton = editButton;
                buttons.add(editButton);
                TexturedButton snbtButton = GuapiHelper.texturedButton(ModTextures.SNBT_EDITOR, 16, 16, false).tooltip(ModTexts.OPEN_SNBT_EDITOR);
                this.secondarySnbtButton = snbtButton;
                buttons.add(snbtButton);
                TexturedButton clearButton = GuapiHelper.texturedButton(ModTextures.REMOVE, 16, 16, false).tooltip(ModTexts.REMOVE);
                this.secondaryClearButton = clearButton;
                buttons.add(clearButton);
            }));
            root.add(createItemSection(ModTexts.TRADE_OUTPUT.copy(), () -> {
                ItemViewBuilder builder = GuapiHelper.itemView().prefWidth(ROW_HEIGHT).prefHeight(ROW_HEIGHT).drawDecorations();
                this.resultItemView = builder;
                return builder;
            }, () -> {
                Label nameLabel = GuapiHelper.label().prefWidth(0);
                this.resultItemNameLabel = nameLabel;
                return nameLabel;
            }, buttons -> {
                TexturedButton vaultButton = GuapiHelper.texturedButton(ModTextures.PASTE, 16, 16, false).tooltip(ModTexts.LOAD_VAULT);
                this.resultVaultButton = vaultButton;
                buttons.add(vaultButton);
                TexturedButton chooseButton = GuapiHelper.texturedButton(ModTextures.SEARCH, 16, 16, false).tooltip(ModTexts.CHOOSE_ITEM);
                this.resultChooseButton = chooseButton;
                buttons.add(chooseButton);
                TexturedButton editButton = GuapiHelper.texturedButton(ModTextures.EDITOR, 16, 16, false).tooltip(ModTexts.OPEN_EDITOR);
                this.resultEditButton = editButton;
                buttons.add(editButton);
                TexturedButton snbtButton = GuapiHelper.texturedButton(ModTextures.SNBT_EDITOR, 16, 16, false).tooltip(ModTexts.OPEN_SNBT_EDITOR);
                this.resultSnbtButton = snbtButton;
                buttons.add(snbtButton);
                TexturedButton clearButton = GuapiHelper.texturedButton(ModTextures.REMOVE, 16, 16, false).tooltip(ModTexts.REMOVE);
                this.resultClearButton = clearButton;
                buttons.add(clearButton);
            }));
            ((VBoxBuilder) root.spacing(SECTION_SPACING)).padding(5);
        });
    }

    private Node createItemSection(MutableComponent title, Supplier<ItemView> itemViewFactory, Supplier<Label> nameLabelFactory, Consumer<HBoxBuilder> buttonsBuilder) {
        return GuapiHelper.vBox((Consumer<VBoxBuilder>) section -> {
            section.add(GuapiHelper.label((Component) title).prefWidth(0));
            section.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) row -> {
                row.add((Node) itemViewFactory.get());
                Label nameLabel = (Label) nameLabelFactory.get();
                nameLabel.setPrefWidth(NAME_LABEL_WIDTH);
                nameLabel.setMaxWidth(NAME_LABEL_WIDTH);
                row.add(nameLabel, 0);
                row.add(GuapiHelper.hBox(), 1);
                row.add(((HBoxBuilder) GuapiHelper.hBox((Consumer<HBoxBuilder>) buttonRow -> {
                    buttonsBuilder.accept(buttonRow);
                    ((HBoxBuilder) buttonRow.spacing(2)).align(Align.CENTER_RIGHT);
                }).prefWidth(BUTTON_GROUP_WIDTH)).padding(0, 0, 0, 8));
                ((HBoxBuilder) row.spacing(5)).align(GuapiHelper.CENTER_LEFT);
                row.prefHeight(ROW_HEIGHT);
            }));
            section.spacing(5);
        });
    }

    public Label getTradeTitleLabel() {
        return this.tradeTitleLabel;
    }

    public ItemView getPrimaryItemView() {
        return this.primaryItemView;
    }

    public Label getPrimaryItemNameLabel() {
        return this.primaryItemNameLabel;
    }

    public TexturedButton getPrimaryChooseButton() {
        return this.primaryChooseButton;
    }

    public TexturedButton getPrimaryEditButton() {
        return this.primaryEditButton;
    }

    public TexturedButton getPrimarySnbtButton() {
        return this.primarySnbtButton;
    }

    public TexturedButton getPrimaryClearButton() {
        return this.primaryClearButton;
    }

    public TexturedButton getPrimaryVaultButton() {
        return this.primaryVaultButton;
    }

    public ItemView getSecondaryItemView() {
        return this.secondaryItemView;
    }

    public Label getSecondaryItemNameLabel() {
        return this.secondaryItemNameLabel;
    }

    public TexturedButton getSecondaryChooseButton() {
        return this.secondaryChooseButton;
    }

    public TexturedButton getSecondaryEditButton() {
        return this.secondaryEditButton;
    }

    public TexturedButton getSecondarySnbtButton() {
        return this.secondarySnbtButton;
    }

    public TexturedButton getSecondaryClearButton() {
        return this.secondaryClearButton;
    }

    public TexturedButton getSecondaryVaultButton() {
        return this.secondaryVaultButton;
    }

    public ItemView getResultItemView() {
        return this.resultItemView;
    }

    public Label getResultItemNameLabel() {
        return this.resultItemNameLabel;
    }

    public TexturedButton getResultChooseButton() {
        return this.resultChooseButton;
    }

    public TexturedButton getResultEditButton() {
        return this.resultEditButton;
    }

    public TexturedButton getResultSnbtButton() {
        return this.resultSnbtButton;
    }

    public TexturedButton getResultClearButton() {
        return this.resultClearButton;
    }

    public TexturedButton getResultVaultButton() {
        return this.resultVaultButton;
    }
}
