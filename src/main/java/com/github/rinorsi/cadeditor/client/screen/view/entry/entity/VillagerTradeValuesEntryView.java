package com.github.rinorsi.cadeditor.client.screen.view.entry.entity;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.CheckBox;
import com.github.franckyi.guapi.api.node.Label;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.TextField;
import com.github.franckyi.guapi.api.node.builder.HBoxBuilder;
import com.github.franckyi.guapi.api.node.builder.VBoxBuilder;
import com.github.franckyi.guapi.api.util.Align;
import com.github.rinorsi.cadeditor.client.screen.view.entry.EntryView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public class VillagerTradeValuesEntryView extends EntryView {
    private static final int SECTION_SPACING = 3;

    public static final int ENTRY_HEIGHT = 210;
    private static final int LABEL_WIDTH = 110;
    private static final int FIELD_WIDTH = 150;
    private static final int ROW_HEIGHT = 24;
    private Label tradeTitleLabel;
    private TextField maxUsesField;
    private TextField usesField;
    private TextField demandField;
    private TextField specialPriceField;
    private TextField priceMultiplierField;
    private CheckBox rewardExpBox;
    private TextField xpField;

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
            Label label = (Label) GuapiHelper.label().prefWidth(0);
            this.tradeTitleLabel = label;
            root.add(label);
            root.add(createNumberRow(ModTexts.TRADE_MAX_USES.copy(), () -> {
                TextField textField = (TextField) GuapiHelper.textField().prefWidth(FIELD_WIDTH);
                this.maxUsesField = textField;
                return textField;
            }));
            root.add(createNumberRow(ModTexts.TRADE_USES.copy(), () -> {
                TextField textField = (TextField) GuapiHelper.textField().prefWidth(FIELD_WIDTH);
                this.usesField = textField;
                return textField;
            }));
            root.add(createNumberRow(ModTexts.TRADE_DEMAND.copy(), () -> {
                TextField textField = (TextField) GuapiHelper.textField().prefWidth(FIELD_WIDTH);
                this.demandField = textField;
                return textField;
            }));
            root.add(createNumberRow(ModTexts.TRADE_SPECIAL_PRICE.copy(), () -> {
                TextField textField = (TextField) GuapiHelper.textField().prefWidth(FIELD_WIDTH);
                this.specialPriceField = textField;
                return textField;
            }));
            root.add(createNumberRow(ModTexts.TRADE_PRICE_MULTIPLIER.copy(), () -> {
                TextField textField = (TextField) GuapiHelper.textField().prefWidth(FIELD_WIDTH);
                this.priceMultiplierField = textField;
                return textField;
            }));
            root.add(createToggleRow(ModTexts.TRADE_REWARD_EXP.copy(), () -> {
                CheckBox checkBox = (CheckBox) GuapiHelper.checkBox("");
                this.rewardExpBox = checkBox;
                return checkBox;
            }));
            root.add(createNumberRow(ModTexts.TRADE_XP.copy(), () -> {
                TextField textField = (TextField) GuapiHelper.textField().prefWidth(FIELD_WIDTH);
                this.xpField = textField;
                return textField;
            }));
            ((VBoxBuilder) root.spacing(SECTION_SPACING)).padding(5);
        });
    }

    private Node createNumberRow(MutableComponent labelText, Supplier<TextField> fieldFactory) {
        return GuapiHelper.hBox((Consumer<HBoxBuilder>) row -> {
            row.add(GuapiHelper.label((Component) labelText).prefWidth(LABEL_WIDTH));
            row.add((Node) fieldFactory.get());
            ((HBoxBuilder) row.spacing(5)).align(GuapiHelper.CENTER_LEFT);
            row.prefHeight(ROW_HEIGHT);
            row.setMinHeight(ROW_HEIGHT);
        });
    }

    private Node createToggleRow(MutableComponent labelText, Supplier<Node> controlFactory) {
        return GuapiHelper.hBox((Consumer<HBoxBuilder>) row -> {
            row.add(GuapiHelper.label((Component) labelText).prefWidth(LABEL_WIDTH));
            row.add((Node) controlFactory.get());
            ((HBoxBuilder) row.spacing(5)).align(GuapiHelper.CENTER_LEFT);
            row.prefHeight(ROW_HEIGHT);
            row.setMinHeight(ROW_HEIGHT);
        });
    }

    public Label getTradeTitleLabel() {
        return this.tradeTitleLabel;
    }

    public TextField getMaxUsesField() {
        return this.maxUsesField;
    }

    public TextField getUsesField() {
        return this.usesField;
    }

    public TextField getDemandField() {
        return this.demandField;
    }

    public TextField getSpecialPriceField() {
        return this.specialPriceField;
    }

    public TextField getPriceMultiplierField() {
        return this.priceMultiplierField;
    }

    public CheckBox getRewardExpBox() {
        return this.rewardExpBox;
    }

    public TextField getXpField() {
        return this.xpField;
    }
}
