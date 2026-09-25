package com.github.rinorsi.cadeditor.client.screen.controller;

import com.github.franckyi.guapi.api.mvc.Controller;
import com.github.rinorsi.cadeditor.client.context.ItemEditorContext;
import com.github.rinorsi.cadeditor.client.screen.model.EditorModel;
import com.github.rinorsi.cadeditor.client.screen.view.ScreenView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public interface EditorController<M extends EditorModel, V extends ScreenView> extends Controller<M, V> {
    @Override
    default void bind() {
        getModel().validProperty().addListener(this::updateDoneButton);
        getView().getSaveButton().setVisible(true);
        getView().getSaveButton().onAction(getModel()::save);
        getView().addCopyCommandButton(getModel().getContext().getCommandTooltip(), getModel().getContext().getCommandName());
        getView().getCopyCommandButton().setActive(getModel().getContext().isCopyCommand());
        getView().getCopyCommandButton().activeProperty().addListener(value -> getModel().getContext().setCopyCommand(value));
        getView().getCopyCommandButton().activeProperty().addListener(this::updateDoneButton);
        if (!(getModel().getContext() instanceof ItemEditorContext)) {
            getView().addApplyVanillaCommandButton(ModTexts.APPLY_VANILLA_COMMAND, ModTexts.APPLY_VANILLA_COMMAND_ACTIVE, ModTexts.APPLY_VANILLA_COMMAND_WARNING);
            getView().getApplyVanillaCommandButton().setActive(getModel().getContext().isApplyVanillaCommand());
            getView().getApplyVanillaCommandButton().activeProperty().addListener(value -> getModel().getContext().setApplyVanillaCommand(value));
            getView().getApplyVanillaCommandButton().activeProperty().addListener(this::updateDoneButton);
        }
        if (getModel().getContext().canSaveToVault()) {
            getView().addSaveVaultButton(getModel().getContext().getTargetName());
            getView().getSaveVaultButton().setActive(getModel().getContext().isSaveToVault());
            getView().getSaveVaultButton().activeProperty().addListener(value -> getModel().getContext().setSaveToVault(value));
            getView().getSaveVaultButton().activeProperty().addListener(this::updateDoneButton);
        }
        updateDoneButton();
    }

    default void updateDoneButton() {
        MutableComponent label;
        if (getModel().getContext().hasPermission()) {
            getView().getDoneButton().setDisable(!getModel().isValid());
            getView().getSaveButton().setDisable(!getModel().isValid());
            if (getModel().isValid()) {
                getView().getDoneButton().getTooltip().clear();
                getView().getSaveButton().getTooltip().clear();
                return;
            } else {
                getView().getDoneButton().getTooltip().setAll(ModTexts.FIX_ERRORS);
                getView().getSaveButton().getTooltip().setAll(ModTexts.FIX_ERRORS);
                return;
            }
        }
        boolean disable = true;
        Component tooltip = null;
        if (getModel().getContext().isSaveToVault() || getModel().getContext().isCopyCommand() || getModel().getContext().isApplyVanillaCommand()) {
            label = getModel().getContext().isSaveToVault() ? ModTexts.SAVE_VAULT_GREEN
                    : getModel().getContext().isCopyCommand() ? ModTexts.COPY_COMMAND_GREEN : ModTexts.APPLY_VANILLA_COMMAND_GREEN;
            if (getModel().isValid()) {
                disable = false;
            } else {
                tooltip = ModTexts.FIX_ERRORS;
            }
        } else {
            label = ModTexts.DONE;
            tooltip = getModel().getContext().getErrorTooltip();
        }
        getView().getDoneButton().setDisable(disable);
        getView().getDoneButton().setLabel(label);
        getView().getSaveButton().setDisable(true);
        if (tooltip == null) {
            getView().getSaveButton().getTooltip().clear();
        } else {
            getView().getSaveButton().getTooltip().setAll(tooltip);
        }
        if (tooltip == null) {
            getView().getDoneButton().getTooltip().clear();
        } else {
            getView().getDoneButton().getTooltip().setAll(tooltip);
        }
    }
}
