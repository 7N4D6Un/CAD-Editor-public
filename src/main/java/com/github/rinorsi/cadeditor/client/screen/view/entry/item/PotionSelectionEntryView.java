package com.github.rinorsi.cadeditor.client.screen.view.entry.item;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.Box;
import com.github.franckyi.guapi.api.node.ItemView;
import com.github.franckyi.guapi.api.node.TexturedButton;
import com.github.franckyi.guapi.api.node.VBox;
import com.github.franckyi.guapi.api.node.builder.HBoxBuilder;
import com.github.franckyi.guapi.api.node.builder.LabelBuilder;
import com.github.franckyi.guapi.api.node.builder.VBoxBuilder;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.view.entry.SelectionEntryView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;


public class PotionSelectionEntryView extends SelectionEntryView {
    private VBox root;
    private TexturedButton resetPotionButton;
    private TexturedButton resetColorButton;
    private TexturedButton chooseColorButton;
    private ItemView potionView;
    private TexturedButton removeColorButton;
    private final TexturedButton dummyUpButton = GuapiHelper.texturedButton(null, false);
    private final TexturedButton dummyDownButton = GuapiHelper.texturedButton(null, false);
    private final TexturedButton dummyDeleteButton = GuapiHelper.texturedButton(null, false);

    @Override 
    public void build() {
        this.root = GuapiHelper.vBox((Consumer<VBoxBuilder>) root -> {
            root.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) potion -> {
                potion.add(createContent(), 1);
                potion.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) right -> {
                    right.add(createLabeledContent(), 1);
                    right.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) buttons -> {
                        buttons.add(this.resetPotionButton = (TexturedButton) GuapiHelper.texturedButton(ModTextures.RESET, 16, 16, false).tooltip(ModTexts.RESET));
                        buttons.spacing(2);
                    }));
                    ((HBoxBuilder) right.spacing(5)).align(GuapiHelper.CENTER_RIGHT);
                }), 2);
                ((HBoxBuilder) ((HBoxBuilder) potion.fillHeight()).spacing(5)).align(GuapiHelper.CENTER);
            }), 1);
            root.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) color -> {
                color.add(((LabelBuilder) GuapiHelper.label((Component) ModTexts.POTION_COLOR).padding(GuapiHelper.right(5))).textAlign(GuapiHelper.CENTER_RIGHT), 1);
                color.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) right -> {
                    right.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) content -> {
                        content.add(this.chooseColorButton = (TexturedButton) GuapiHelper.texturedButton(ModTextures.COLOR_CUSTOM, 16, 16, false).tooltip(ModTexts.choose(ModTexts.CUSTOM_COLOR)));
                        content.add(this.removeColorButton = (TexturedButton) GuapiHelper.texturedButton(ModTextures.REMOVE, 16, 16, false).tooltip(ModTexts.REMOVE_CUSTOM_COLOR));
                        content.add(this.potionView = GuapiHelper.itemView());
                        content.spacing(5);
                    }), 1);
                    right.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) buttons -> {
                        buttons.add(this.resetColorButton = (TexturedButton) GuapiHelper.texturedButton(ModTextures.RESET, 16, 16, false).tooltip(ModTexts.RESET));
                        buttons.spacing(2);
                    }));
                    ((HBoxBuilder) right.spacing(5)).align(GuapiHelper.CENTER_RIGHT);
                }), 2);
                ((HBoxBuilder) ((HBoxBuilder) color.fillHeight()).spacing(5)).align(GuapiHelper.CENTER);
            }), 1);
            ((VBoxBuilder) root.spacing(5)).fillWidth();
        });
    }

    @Override 
    public void setListButtonsVisible(boolean visible) {
    }

    @Override 
    public Box getRoot() {
        return this.root;
    }

    @Override 
    public TexturedButton getUpButton() {
        return this.dummyUpButton;
    }

    @Override 
    public TexturedButton getDownButton() {
        return this.dummyDownButton;
    }

    @Override 
    public TexturedButton getDeleteButton() {
        return this.dummyDeleteButton;
    }

    @Override 
    public TexturedButton getResetButton() {
        return this.resetPotionButton;
    }

    public TexturedButton getRemoveColorButton() {
        return this.removeColorButton;
    }

    public TexturedButton getChooseColorButton() {
        return this.chooseColorButton;
    }

    public TexturedButton getResetColorButton() {
        return this.resetColorButton;
    }

    public ItemView getPotionView() {
        return this.potionView;
    }
}
