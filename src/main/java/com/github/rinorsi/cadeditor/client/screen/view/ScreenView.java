package com.github.rinorsi.cadeditor.client.screen.view;

import com.github.franckyi.databindings.api.ObservableList;
import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.mvc.View;
import com.github.franckyi.guapi.api.node.Button;
import com.github.franckyi.guapi.api.node.HBox;
import com.github.franckyi.guapi.api.node.Label;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.TexturedButton;
import com.github.franckyi.guapi.api.node.TexturedToggleButton;
import com.github.franckyi.guapi.api.node.VBox;
import com.github.franckyi.guapi.api.node.builder.ButtonBuilder;
import com.github.franckyi.guapi.api.node.builder.HBoxBuilder;
import com.github.franckyi.guapi.api.node.builder.LabelBuilder;
import com.github.franckyi.guapi.api.node.builder.TexturedButtonBuilder;
import com.github.franckyi.guapi.api.node.builder.TexturedToggleButtonBuilder;
import com.github.franckyi.guapi.api.node.builder.VBoxBuilder;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.util.ScreenScalingManager;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;


public abstract class ScreenView implements View {
    private VBox root;
    private Label headerLabel;
    private TexturedToggleButton copyCommandButton;
    private TexturedToggleButton saveVaultButton;
    private TexturedButton loadVaultButton;
    private TexturedButton openEditorButton;
    private TexturedButton openNBTEditorButton;
    private TexturedButton openSNBTEditorButton;
    private TexturedButton zoomResetButton;
    private TexturedButton zoomOutButton;
    private TexturedButton zoomInButton;
    private Button cancelButton;
    private Button saveButton;
    private Button doneButton;
    private Label zoomLabel;
    protected HBox buttonBar;
    protected HBox buttonBarLeft;
    protected HBox editorButtons;
    protected HBox buttonBarCenter;
    protected HBox buttonBarRight;

    protected abstract Node createEditor();

    @Override 
    public void build() {
        this.root = GuapiHelper.vBox((Consumer<VBoxBuilder>) root -> {
            ((VBoxBuilder) ((VBoxBuilder) ((VBoxBuilder) root.spacing(5)).align(GuapiHelper.CENTER)).padding(5)).fillWidth();
            root.add(createHeader());
            root.add(createMain(), 1);
            root.add(createFooter());
        });
        ScreenScalingManager.get().scaleProperty().addListener(this::onZoomUpdated);
        this.zoomResetButton.disableProperty().bind(ScreenScalingManager.get().canScaleBeResetProperty().not());
        onZoomUpdated();
    }

    protected Node createHeader() {
        return GuapiHelper.hBox((Consumer<HBoxBuilder>) hBoxBuilder -> {
            hBoxBuilder.add(GuapiHelper.hBox().prefWidth(16));
            Label label = (Label) GuapiHelper.label((Component) getHeaderLabelText()).textAlign(GuapiHelper.CENTER).prefHeight(20);
            this.headerLabel = label;
            hBoxBuilder.add(label, 1);
            hBoxBuilder.add(createButton(ModTextures.SETTINGS, ModTexts.SETTINGS).action(ModScreenHandler::openSettingsScreen));
            hBoxBuilder.align(GuapiHelper.CENTER);
        });
    }

    protected MutableComponent getHeaderLabelText() {
        return GuapiHelper.EMPTY_TEXT;
    }

    protected Node createMain() {
        return GuapiHelper.vBox((Consumer<VBoxBuilder>) main -> {
            main.add(createButtonBar());
            main.add(createEditor(), 1);
            ((VBoxBuilder) main.spacing(2)).fillWidth();
        });
    }

    protected Node createButtonBar() {
        this.buttonBar = GuapiHelper.hBox((Consumer<HBoxBuilder>) buttons -> {
            this.editorButtons = (HBox) GuapiHelper.hBox().spacing(2);
            this.buttonBarLeft = (HBox) ((HBoxBuilder) GuapiHelper.hBox(this.editorButtons).align(GuapiHelper.CENTER_LEFT)).spacing(10);
            buttons.add(this.buttonBarLeft);
            this.buttonBarCenter = (HBox) ((HBoxBuilder) GuapiHelper.hBox().align(GuapiHelper.CENTER)).spacing(10);
            buttons.add(this.buttonBarCenter, 1);
            this.buttonBarRight = (HBox) ((HBoxBuilder) GuapiHelper.hBox().align(GuapiHelper.CENTER_RIGHT)).spacing(10);
            buttons.add(this.buttonBarRight);
            this.buttonBarRight.getChildren().add(GuapiHelper.hBox((Consumer<HBoxBuilder>) zoom -> {
                zoom.add(this.zoomResetButton = (TexturedButton) createButton(ModTextures.ZOOM_RESET, ModTexts.ZOOM_RESET).action(ScreenScalingManager.get()::restoreScale));
                zoom.add(this.zoomOutButton = (TexturedButton) createButton(ModTextures.ZOOM_OUT, ModTexts.ZOOM_OUT).action(ScreenScalingManager.get()::scaleDown));
                zoom.add(this.zoomLabel = (Label) ((LabelBuilder) GuapiHelper.label().prefWidth(25)).textAlign(GuapiHelper.CENTER).padding(0, 3));
                zoom.add(this.zoomInButton = (TexturedButton) createButton(ModTextures.ZOOM_IN, ModTexts.ZOOM_IN).action(ScreenScalingManager.get()::scaleUp));
                ((HBoxBuilder) zoom.spacing(2)).align(GuapiHelper.CENTER);
            }));
            ((HBoxBuilder) buttons.spacing(20)).prefHeight(16);
        });
        return this.buttonBar;
    }

    protected Node createFooter() {
        return GuapiHelper.vBox((Consumer<VBoxBuilder>) box -> {
            ((VBoxBuilder) box.spacing(4)).align(GuapiHelper.CENTER);
            box.add(GuapiHelper.label((Component) ModTexts.gui("cade_footer_short")).textAlign(GuapiHelper.CENTER).prefHeight(12));
            box.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) footer -> {
                ((HBoxBuilder) footer.spacing(20)).align(GuapiHelper.CENTER);
                footer.add(this.cancelButton = (Button) GuapiHelper.button((Component) ModTexts.CANCEL).prefWidth(90));
                footer.add(this.saveButton = (Button) ((ButtonBuilder) GuapiHelper.button((Component) ModTexts.SAVE_EDIT).prefWidth(90)).visible(false));
                footer.add(this.doneButton = (Button) GuapiHelper.button((Component) ModTexts.DONE).prefWidth(90));
            }));
        });
    }

    protected TexturedButtonBuilder createButton(Identifier id, String tooltipText) {
        return createButton(id, GuapiHelper.translated(tooltipText));
    }

    
    protected TexturedButtonBuilder createButton(Identifier id, MutableComponent tooltipText) {
        return (TexturedButtonBuilder) GuapiHelper.texturedButton(id, 16, 16, false).tooltip(tooltipText);
    }

    protected void onZoomUpdated() {
        this.zoomOutButton.setDisable(!ScreenScalingManager.get().canScaleDown());
        this.zoomLabel.setLabel(GuapiHelper.text(ScreenScalingManager.get().getScale() == 0 ? "Auto" : Integer.toString(ScreenScalingManager.get().getScale())));
        this.zoomInButton.setDisable(!ScreenScalingManager.get().canScaleUp());
    }

    @Override 
    public VBox getRoot() {
        return this.root;
    }

    public Label getHeaderLabel() {
        return this.headerLabel;
    }

    public TexturedToggleButton getCopyCommandButton() {
        return this.copyCommandButton;
    }

    public TexturedToggleButton getSaveVaultButton() {
        return this.saveVaultButton;
    }

    public TexturedButton getLoadVaultButton() {
        return this.loadVaultButton;
    }

    public TexturedButton getOpenEditorButton() {
        return this.openEditorButton;
    }

    public TexturedButton getOpenNBTEditorButton() {
        return this.openNBTEditorButton;
    }

    public TexturedButton getOpenSNBTEditorButton() {
        return this.openSNBTEditorButton;
    }

    public Button getCancelButton() {
        return this.cancelButton;
    }

    public Button getDoneButton() {
        return this.doneButton;
    }

    public Button getSaveButton() {
        return this.saveButton;
    }

    public void addSaveVaultButton(MutableComponent arg) {
        ObservableList<Node> children = this.editorButtons.getChildren();
        this.saveVaultButton = (TexturedToggleButton) ((TexturedToggleButtonBuilder) GuapiHelper.texturedToggleButton(ModTextures.SAVE, 16, 16, false).tooltip(ModTexts.SAVE_VAULT)).action(() -> {
            this.saveVaultButton.getTooltip().setAll(this.saveVaultButton.isActive() ? Arrays.asList(ModTexts.savedVault(arg)) : Collections.singletonList(ModTexts.SAVE_VAULT));
        });
        children.add(this.saveVaultButton);
    }

    public void addLoadVaultButton(Runnable action) {
        ObservableList<Node> children = this.editorButtons.getChildren();
        this.loadVaultButton = (TexturedButton) ((TexturedButtonBuilder) GuapiHelper.texturedButton(ModTextures.PASTE, 16, 16, false).tooltip(ModTexts.LOAD_VAULT)).action(action);
        children.add(this.loadVaultButton);
    }

    public void addOpenEditorButton(Runnable action) {
        ObservableList<Node> children = this.editorButtons.getChildren();
        this.openEditorButton = (TexturedButton) ((TexturedButtonBuilder) GuapiHelper.texturedButton(ModTextures.EDITOR, 16, 16, false).tooltip(ModTexts.OPEN_EDITOR)).action(action);
        children.add(this.openEditorButton);
    }

    public void addOpenNBTEditorButton(Runnable action) {
        ObservableList<Node> children = this.editorButtons.getChildren();
        this.openNBTEditorButton = (TexturedButton) ((TexturedButtonBuilder) GuapiHelper.texturedButton(ModTextures.NBT_EDITOR, 16, 16, false).tooltip(ModTexts.OPEN_NBT_EDITOR)).action(action);
        children.add(this.openNBTEditorButton);
    }

    public void addOpenSNBTEditorButton(Runnable action) {
        ObservableList<Node> children = this.editorButtons.getChildren();
        this.openSNBTEditorButton = (TexturedButton) ((TexturedButtonBuilder) GuapiHelper.texturedButton(ModTextures.SNBT_EDITOR, 16, 16, false).tooltip(ModTexts.OPEN_SNBT_EDITOR)).action(action);
        children.add(this.openSNBTEditorButton);
    }

    
    public void addCopyCommandButton(MutableComponent copyText, String copiedText) {
        ObservableList<Node> children = this.editorButtons.getChildren();
        this.copyCommandButton = (TexturedToggleButton) ((TexturedToggleButtonBuilder) GuapiHelper.texturedToggleButton(ModTextures.COPY_COMMAND, 16, 16, false).tooltip(copyText)).action(() -> {
            this.copyCommandButton.getTooltip().setAll(this.copyCommandButton.isActive() ? Arrays.asList(ModTexts.commandCopied(copiedText)) : Collections.singletonList(copyText));
        });
        children.add(this.copyCommandButton);
    }
}
