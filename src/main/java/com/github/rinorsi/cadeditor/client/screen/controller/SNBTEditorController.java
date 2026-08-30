package com.github.rinorsi.cadeditor.client.screen.controller;

import com.github.franckyi.guapi.api.Guapi;
import com.github.franckyi.guapi.api.mvc.AbstractController;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.SNBTEditorModel;
import com.github.rinorsi.cadeditor.client.screen.view.SNBTEditorView;
import com.github.rinorsi.cadeditor.client.util.SnbtHelper;
import com.github.rinorsi.cadeditor.common.EditorType;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.SnbtPrinterTagVisitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SNBTEditorController extends AbstractController<SNBTEditorModel, SNBTEditorView> implements EditorController<SNBTEditorModel, SNBTEditorView> {
    private static final Logger LOGGER = LogManager.getLogger();

    public SNBTEditorController(SNBTEditorModel model, SNBTEditorView view) {
        super(model, view);
    }

    
    @Override
    public void bind() {
        EditorController.super.bind();
        ((SNBTEditorView) this.view).addOpenEditorButton(() -> {
            attemptEditorChange(EditorType.STANDARD);
        });
        ((SNBTEditorView) this.view).addOpenNBTEditorButton(() -> {
            attemptEditorChange(EditorType.NBT);
        });
        ((SNBTEditorView) this.view).getTextArea().textProperty().bindBidirectional(((SNBTEditorModel) this.model).valueProperty());
        ((SNBTEditorView) this.view).getTextArea().setValidator(s -> {
            try {
                return SnbtHelper.parse(s) != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        });
        ((SNBTEditorView) this.view).getPreviewTree().rootItemProperty().bind(((SNBTEditorModel) this.model).previewRootProperty());
        ((SNBTEditorView) this.view).getPreviewTree().visibleProperty().bind(((SNBTEditorModel) this.model).validProperty());
        ((SNBTEditorView) this.view).getPreviewStatus().visibleProperty().bind(((SNBTEditorModel) this.model).validProperty().not());
        ((SNBTEditorView) this.view).getPreviewToggle().disableProperty().bind(((SNBTEditorModel) this.model).previewRootProperty().mapToBoolean(value -> Objects.isNull(value)));
        ((SNBTEditorModel) this.model).previewRootProperty().addListener(root -> {
            if (root == null && ((SNBTEditorView) this.view).getPreviewToggle().isActive()) {
                ((SNBTEditorView) this.view).getPreviewToggle().setActive(false);
                ((SNBTEditorView) this.view).refreshPreviewPane();
            }
        });
        ((SNBTEditorView) this.view).getFormatButton().disableProperty().bind(((SNBTEditorView) this.view).getTextArea().validProperty().not());
        ((SNBTEditorView) this.view).getFormatButton().onAction(this::format);
        ((SNBTEditorView) this.view).getDoneButton().onAction(() -> {
            if (ensureValidInput()) {
                saveAndUpdate();
            }
        });
        ((SNBTEditorView) this.view).getCancelButton().onAction(Guapi.getScreenHandler()::hideScene);
    }

    
    
    private void saveAndUpdate() {
        try {
            String textFromArea = getCurrentTextFromTextArea();
            CompoundTag parsed = SnbtHelper.parse(textFromArea);
            ((SNBTEditorModel) this.model).getContext().setTag(parsed);
            ((SNBTEditorModel) this.model).getContext().update();
            Guapi.getScreenHandler().hideScene();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getCurrentTextFromTextArea() {
        try {
            return ((SNBTEditorView) this.view).getTextArea().getTextFromTextBox();
        } catch (Exception e) {
            return ((SNBTEditorView) this.view).getTextArea().getText();
        }
    }

    
    private void format() {
        String text = ((SNBTEditorView) this.view).getTextArea().getText();
        try {
            CompoundTag parsed = SnbtHelper.parse(text);
            String pretty = new SnbtPrinterTagVisitor("  ", 0, new ArrayList<>()).visit(parsed);
            if (pretty.equals(text)) {
                ((SNBTEditorModel) this.model).setValue(new SnbtPrinterTagVisitor("", 0, new ArrayList<>()).visit(parsed));
            } else {
                ((SNBTEditorModel) this.model).setValue(pretty);
            }
        } catch (CommandSyntaxException e) {
            LOGGER.error("格式化 NBT 标签失败", e);
        }
    }

    
    private boolean ensureValidInput() {
        String textFromArea;
        try {
            textFromArea = ((SNBTEditorView) this.view).getTextArea().getTextFromTextBox();
        } catch (Exception e) {
            textFromArea = ((SNBTEditorView) this.view).getTextArea().getText();
        }
        ((SNBTEditorModel) this.model).setPendingText(textFromArea);
        if (((SNBTEditorModel) this.model).validProperty().getValue()) {
            return true;
        }
        ((SNBTEditorView) this.view).getTextArea().setValidationForced(true);
        ClientUtil.showMessage(ModTexts.Messages.snbtInvalidCannotApply());
        return false;
    }

    
    private void attemptEditorChange(EditorType target) {
        if (ensureValidInput()) {
            ((SNBTEditorModel) this.model).changeEditor(target);
        }
    }
}
