package com.github.rinorsi.cadeditor.client.screen.controller;

import com.github.franckyi.guapi.api.Guapi;
import com.github.franckyi.guapi.api.mvc.AbstractController;
import com.github.rinorsi.cadeditor.client.screen.model.NBTEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.NBTTagModel;
import com.github.rinorsi.cadeditor.client.screen.view.NBTEditorView;
import com.github.rinorsi.cadeditor.common.EditorType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;


public class NBTEditorController extends AbstractController<NBTEditorModel, NBTEditorView> implements EditorController<NBTEditorModel, NBTEditorView> {
    public NBTEditorController(NBTEditorModel model, NBTEditorView view) {
        super(model, view);
    }

    
    @Override
    public void bind() {
        EditorController.super.bind();
        ((NBTEditorView) this.view).addOpenEditorButton(() -> {
            ((NBTEditorModel) this.model).changeEditor(EditorType.STANDARD);
        });
        ((NBTEditorView) this.view).addOpenSNBTEditorButton(() -> {
            ((NBTEditorModel) this.model).changeEditor(EditorType.SNBT);
        });
        ((NBTEditorView) this.view).getTagTree().rootItemProperty().bind(((NBTEditorModel) this.model).rootTagProperty());
        ((NBTEditorView) this.view).getTagTree().focusedElementProperty().addListener(this::updateEnabledButtons);
        ((NBTEditorView) this.view).setOnButtonClick(this::onButtonClick);
        ((NBTEditorView) this.view).getDoneButton().onAction(((NBTEditorModel) this.model)::update);
        ((NBTEditorView) this.view).getCancelButton().onAction(Guapi.getScreenHandler()::hideScene);
    }

    
    private void onButtonClick(NBTEditorView.ButtonType target) {
        NBTTagModel tag = ((NBTEditorView) this.view).getTagTree().getFocusedElement();
        NBTTagModel parent = tag.getParent();
        switch (target) {
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case BYTE_ARRAY:
            case STRING:
            case LIST:
            case COMPOUND:
            case INT_ARRAY:
            case LONG_ARRAY:
                addChildTag(tag, createEmptyTag(target.getType()));
                break;
            case MOVE_UP: {
                int index = parent.getChildren().indexOf(tag);
                Collections.swap(parent.getChildren(), index, index - 1);
                updateEnabledButtons(tag);
                break;
            }
            case MOVE_DOWN: {
                int index = parent.getChildren().indexOf(tag);
                Collections.swap(parent.getChildren(), index, index + 1);
                updateEnabledButtons(tag);
                break;
            }
            case ADD:
                switch (tag.getTagType()) {
                    case 7:
                        addChildTag(tag, (byte) 1, "0");
                        break;
                    case 9:
                        if (!tag.getChildren().isEmpty()) {
                            addChildTag(tag, createEmptyTag(tag.getChildren().get(0).getTagType()));
                        }
                        break;
                    case 11:
                        addChildTag(tag, (byte) 3, "0");
                        break;
                    case 12:
                        addChildTag(tag, (byte) 4, "0");
                        break;
                }
                break;
            case CUT:
                ((NBTEditorModel) this.model).setClipboardTag(tag);
                removeTag(parent, tag);
                break;
            case DELETE:
                removeTag(parent, tag);
                break;
            case COPY:
                ((NBTEditorModel) this.model).setClipboardTag(tag.createClipboardTag());
                updateEnabledButtons(tag);
                break;
            case PASTE:
                NBTTagModel clipboardTag = ((NBTEditorModel) this.model).getClipboardTag();
                if (clipboardTag.canBuild()) {
                    addChildTag(tag, clipboardTag.build(), clipboardTag.getName());
                } else {
                    addChildTag(tag, clipboardTag.getTagType(), clipboardTag.getValue());
                }
                break;
        }
    }

    
    


    
    private void updateEnabledButtons(NBTTagModel newVal) {
        ((NBTEditorView) this.view).getAddTagButton().setActive(false);
        List<NBTEditorView.ButtonType> buttons = new ArrayList<>();
        NBTTagModel clipboardTag = ((NBTEditorModel) this.model).getClipboardTag();
        if (newVal != null) {
            switch (newVal.getTagType()) {
                case 7:
                    if (clipboardTag != null && !clipboardTag.canBuild() && clipboardTag.getTagType() == 1) {
                        buttons.add(NBTEditorView.ButtonType.PASTE);
                    }
                    buttons.add(NBTEditorView.ButtonType.ADD);
                    break;
                case 9:
                    if (newVal.getChildren().isEmpty()) {
                        buttons.add(NBTEditorView.ButtonType.ADD);
                        if (clipboardTag != null && clipboardTag.canBuild()) {
                            buttons.add(NBTEditorView.ButtonType.PASTE);
                        }
                    } else {
                        buttons.add(NBTEditorView.ButtonType.ADD);
                        if (clipboardTag != null && clipboardTag.canBuild() && clipboardTag.getTagType() == newVal.getChildren().get(0).getTagType()) {
                            buttons.add(NBTEditorView.ButtonType.PASTE);
                        }
                    }
                    break;
                case 10:
                    buttons.add(NBTEditorView.ButtonType.ADD);
                    if (clipboardTag != null && clipboardTag.canBuild()) {
                        buttons.add(NBTEditorView.ButtonType.PASTE);
                    }
                    break;
                case 11:
                    if (clipboardTag != null && !clipboardTag.canBuild() && clipboardTag.getTagType() == 3) {
                        buttons.add(NBTEditorView.ButtonType.PASTE);
                    }
                    buttons.add(NBTEditorView.ButtonType.ADD);
                    break;
                case 12:
                    if (clipboardTag != null && !clipboardTag.canBuild() && clipboardTag.getTagType() == 4) {
                        buttons.add(NBTEditorView.ButtonType.PASTE);
                    }
                    buttons.add(NBTEditorView.ButtonType.ADD);
                    break;
            }
            if (newVal.getParent() != null) {
                NBTTagModel parent = newVal.getParent();
                if (parent.getChildren().indexOf(newVal) != 0) {
                    buttons.add(NBTEditorView.ButtonType.MOVE_UP);
                }
                if (parent.getChildren().indexOf(newVal) != parent.getChildren().size() - 1) {
                    buttons.add(NBTEditorView.ButtonType.MOVE_DOWN);
                }
                buttons.add(NBTEditorView.ButtonType.DELETE);
                buttons.add(NBTEditorView.ButtonType.CUT);
            }
            buttons.add(NBTEditorView.ButtonType.COPY);
        }
        ((NBTEditorView) this.view).getEnabledButtons().setAll(buttons);
    }

    private void removeTag(NBTTagModel parent, NBTTagModel tag) {
        parent.getChildren().remove(tag);
        ((NBTEditorView) this.view).getTagTree().setFocusedElement(null);
    }

    private void addChildTag(NBTTagModel parent, Tag newTag) {
        addChildTag(parent, newTag, "");
    }

    private void addChildTag(NBTTagModel parent, Tag newTag, String name) {
        addChildTag(parent, new NBTTagModel(parent.getContext(), newTag, parent, parent.getTagType() != 9 ? name : null, null));
    }

    private void addChildTag(NBTTagModel parent, byte target, String value) {
        addChildTag(parent, new NBTTagModel(parent.getContext(), target, parent, value));
    }

    private void addChildTag(NBTTagModel parent, NBTTagModel tag) {
        parent.getChildren().add(tag);
        parent.setExpanded(true);
        ((NBTEditorView) this.view).getTagTree().setScrollTo(tag);
        ((NBTEditorView) this.view).getTagTree().setFocusedElement(tag);
    }

    private Tag createEmptyTag(byte type) {
        return switch (type) {
            case 1 -> ByteTag.ZERO;
            case 2 -> ShortTag.valueOf((short) 0);
            case 3 -> IntTag.valueOf(0);
            case 4 -> LongTag.valueOf(0L);
            case 5 -> FloatTag.ZERO;
            case 6 -> DoubleTag.ZERO;
            case 7 -> new ByteArrayTag(new byte[0]);
            case 8 -> StringTag.valueOf("");
            case 9 -> new ListTag();
            case 10 -> new CompoundTag();
            case 11 -> new IntArrayTag(new int[0]);
            case 12 -> new LongArrayTag(new long[0]);
            default -> null;
        };
    }
}
