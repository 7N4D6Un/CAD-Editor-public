package com.github.rinorsi.cadeditor.client.screen.view;


import com.github.franckyi.databindings.api.ObservableList;
import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.event.MouseButtonEvent;
import com.github.franckyi.guapi.api.node.HBox;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.TexturedButton;
import com.github.franckyi.guapi.api.node.TexturedToggleButton;
import com.github.franckyi.guapi.api.node.Toggle;
import com.github.franckyi.guapi.api.node.TreeView;
import com.github.franckyi.guapi.api.node.VBox;
import com.github.franckyi.guapi.api.node.builder.HBoxBuilder;
import com.github.franckyi.guapi.api.node.builder.TexturedButtonBuilder;
import com.github.franckyi.guapi.api.node.builder.TexturedToggleButtonBuilder;
import com.github.franckyi.guapi.api.node.builder.TreeViewBuilder;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.model.NBTTagModel;
import com.github.rinorsi.cadeditor.client.screen.mvc.NBTTagMVC;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.function.Consumer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;


public class NBTEditorView extends ScreenView {
    private VBox main;
    private TreeView<NBTTagModel> tagTree;
    private HBox addButtons;
    private TexturedButton addByteButton;
    private TexturedButton addShortButton;
    private TexturedButton addIntButton;
    private TexturedButton addLongButton;
    private TexturedButton addFloatButton;
    private TexturedButton addDoubleButton;
    private TexturedButton addByteArrayButton;
    private TexturedButton addStringButton;
    private TexturedButton addListButton;
    private TexturedButton addCompoundButton;
    private TexturedButton addIntArrayButton;
    private TexturedButton addLongArrayButton;
    private TexturedButton moveUpButton;
    private TexturedButton moveDownButton;
    private TexturedButton deleteButton;
    private TexturedButton cutButton;
    private TexturedButton copyButton;
    private TexturedButton pasteButton;
    private TexturedToggleButton addButton;
    private final ObservableList<ButtonType> visibleButtons = ObservableList.create();
    private Consumer<ButtonType> onButtonClick = b -> {
    };

    @Override 
    public void build() {
        super.build();
        this.addButtons = GuapiHelper.hBox((Consumer<HBoxBuilder>) addBox -> {
            addBox.add(this.addByteButton = createAddTagButton(ButtonType.BYTE, "blue", "Byte"));
            addBox.add(this.addShortButton = createAddTagButton(ButtonType.SHORT, "green", "Short"));
            addBox.add(this.addIntButton = createAddTagButton(ButtonType.INT, "aqua", "Int"));
            addBox.add(this.addLongButton = createAddTagButton(ButtonType.LONG, "red", "Long"));
            addBox.add(this.addFloatButton = createAddTagButton(ButtonType.FLOAT, "light_purple", "Float"));
            addBox.add(this.addDoubleButton = createAddTagButton(ButtonType.DOUBLE, "yellow", "Double"));
            addBox.add(this.addByteArrayButton = createAddTagButton(ButtonType.BYTE_ARRAY, "blue", "Byte Array"));
            addBox.add(this.addStringButton = createAddTagButton(ButtonType.STRING, "gray", "String"));
            addBox.add(this.addListButton = createAddTagButton(ButtonType.LIST, "green", "List"));
            addBox.add(this.addCompoundButton = createAddTagButton(ButtonType.COMPOUND, "light_purple", "Compound"));
            addBox.add(this.addIntArrayButton = createAddTagButton(ButtonType.INT_ARRAY, "aqua", "Int Array"));
            addBox.add(this.addLongArrayButton = createAddTagButton(ButtonType.LONG_ARRAY, "red", "Long Array"));
            addBox.spacing(2);
        });
        getEnabledButtons().addListener(this::updateEnabledButtons);
        this.addButton.activeProperty().addListener(value -> updateVisibleButtons(value));
    }

    private void updateVisibleButtons(boolean showButtons) {
        if (showButtons) {
            this.main.getChildren().add(1, this.addButtons);
        } else {
            this.main.getChildren().remove(this.addButtons);
        }
    }

    @Override 
    protected MutableComponent getHeaderLabelText() {
        return ModTexts.editorTitle(ModTexts.gui("raw_data_tree"));
    }

    @Override 
    protected Node createMain() {
        VBox vBox = (VBox) super.createMain();
        this.main = vBox;
        return vBox;
    }

    
    @Override 
    protected Node createButtonBar() {
        Node res = super.createButtonBar();
        this.buttonBarLeft.getChildren().addAll(GuapiHelper.hBox((Consumer<HBoxBuilder>) base -> {
            base.add(this.moveUpButton = (TexturedButton) createButtonFromType(ButtonType.MOVE_UP).disable());
            base.add(this.moveDownButton = (TexturedButton) createButtonFromType(ButtonType.MOVE_DOWN).disable());
            base.add(this.addButton = (TexturedToggleButton) createToggleButtonFromType(ButtonType.ADD).disable());
            base.add(this.deleteButton = (TexturedButton) createButtonFromType(ButtonType.DELETE).disable());
            base.spacing(2);
        }), GuapiHelper.hBox((Consumer<HBoxBuilder>) copyBox -> {
            copyBox.add(this.cutButton = (TexturedButton) createButtonFromType(ButtonType.CUT).disable());
            copyBox.add(this.copyButton = (TexturedButton) createButtonFromType(ButtonType.COPY).disable());
            copyBox.add(this.pasteButton = (TexturedButton) createButtonFromType(ButtonType.PASTE).disable());
            copyBox.spacing(2);
        }), GuapiHelper.hBox((Consumer<HBoxBuilder>) hBoxBuilder -> {
            hBoxBuilder.add(createButton(ModTextures.COLLAPSE, ModTexts.COLLAPSE).action(this::collapseAll));
            hBoxBuilder.add(createButton(ModTextures.EXPAND, ModTexts.EXPAND).action(this::expandAll));
            hBoxBuilder.spacing(2);
        }), createButton(ModTextures.SCROLL_FOCUSED, ModTexts.SCROLL_FOCUSED).action(() -> {
            this.tagTree.setScrollTo(getTagTree().getFocusedElement());
        }));
        return res;
    }

    @Override 
    protected Node createEditor() {
        TreeView<NBTTagModel> treeView = GuapiHelper.<NBTTagModel>treeView(NBTTagModel.class).showRoot().itemHeight(20).childrenFocusable().padding(5).renderer(item -> GuapiHelper.mvc(NBTTagMVC.INSTANCE, item));
        this.tagTree = treeView;
        return treeView;
    }

    private void expandAll() {
        ((NBTTagModel) getTagTree().getRoot()).flattened().forEach(tagModel -> {
            tagModel.setExpanded(true);
        });
        ((NBTTagModel) getTagTree().getRoot()).setChildrenChanged(true);
    }

    private void collapseAll() {
        ((NBTTagModel) getTagTree().getRoot()).flattened().forEach(tagModel -> {
            tagModel.setExpanded(false);
        });
        ((NBTTagModel) getTagTree().getRoot()).setChildrenChanged(true);
    }

    public TreeView<NBTTagModel> getTagTree() {
        return this.tagTree;
    }

    public ObservableList<ButtonType> getEnabledButtons() {
        return this.visibleButtons;
    }

    public void setOnButtonClick(Consumer<ButtonType> action) {
        this.onButtonClick = action;
    }

    public Toggle getAddTagButton() {
        return this.addButton;
    }

    private TexturedButtonBuilder createButtonFromType(ButtonType type) {
        return createButtonFromType(type, type.getText());
    }

    private TexturedButtonBuilder createButtonFromType(ButtonType type, MutableComponent tooltipText) {
        return (TexturedButtonBuilder) createButton(type.getTextureId(), tooltipText).action(() -> {
            this.onButtonClick.accept(type);
        });
    }

    private TexturedToggleButtonBuilder createToggleButtonFromType(ButtonType type) {
        return (TexturedToggleButtonBuilder) ((TexturedToggleButtonBuilder) GuapiHelper.texturedToggleButton(type.getTextureId(), 16, 16, false).tooltip(type.getText())).action(() -> {
            this.onButtonClick.accept(type);
        });
    }

    private TexturedButtonBuilder createAddTagButton(ButtonType type, String color, String with) {
        return createButtonFromType(type, ModTexts.addTag(color, with));
    }

    private void updateEnabledButtons() {
        for (ButtonType type : ButtonType.CAN_DISABLE) {
            getButton(type).setDisable(!getEnabledButtons().contains(type));
        }
    }

    
    private TexturedButton getButton(ButtonType buttonType) {
        return switch (buttonType.ordinal()) {
            case 0 -> this.addByteButton;
            case 1 -> this.addShortButton;
            case 2 -> this.addIntButton;
            case 3 -> this.addLongButton;
            case 4 -> this.addFloatButton;
            case 5 -> this.addDoubleButton;
            case 6 -> this.addByteArrayButton;
            case 7 -> this.addStringButton;
            case 8 -> this.addListButton;
            case 9 -> this.addCompoundButton;
            case 10 -> this.addIntArrayButton;
            case 11 -> this.addLongArrayButton;
            case 12 -> this.moveUpButton;
            case 13 -> this.moveDownButton;
            case 14 -> this.addButton;
            case 15 -> this.deleteButton;
            case 16 -> this.cutButton;
            case 17 -> this.copyButton;
            case 18 -> this.pasteButton;
            default -> throw new AssertionError("Unexpected button type: " + buttonType);
        };
    }

    
    public enum ButtonType {
        BYTE(ModTextures.BYTE_TAG_ADD, 1),
        SHORT(ModTextures.SHORT_TAG_ADD, 2),
        INT(ModTextures.INT_TAG_ADD, 3),
        LONG(ModTextures.LONG_TAG_ADD, 4),
        FLOAT(ModTextures.FLOAT_TAG_ADD, 5),
        DOUBLE(ModTextures.DOUBLE_TAG_ADD, 6),
        BYTE_ARRAY(ModTextures.BYTE_ARRAY_TAG_ADD, 7),
        STRING(ModTextures.STRING_TAG_ADD, 8),
        LIST(ModTextures.LIST_TAG_ADD, 9),
        COMPOUND(ModTextures.COMPOUND_TAG_ADD, 10),
        INT_ARRAY(ModTextures.INT_ARRAY_TAG_ADD, 11),
        LONG_ARRAY(ModTextures.LONG_ARRAY_TAG_ADD, 12),
        MOVE_UP(ModTextures.MOVE_UP, ModTexts.MOVE_UP),
        MOVE_DOWN(ModTextures.MOVE_DOWN, ModTexts.MOVE_DOWN),
        ADD(ModTextures.ADD, ModTexts.ADD),
        DELETE(ModTextures.REMOVE, ModTexts.REMOVE),
        CUT(ModTextures.CUT, ModTexts.CUT),
        COPY(ModTextures.COPY, ModTexts.COPY),
        PASTE(ModTextures.PASTE, ModTexts.PASTE);

        public static final ButtonType[] CAN_DISABLE = {MOVE_UP, MOVE_DOWN, ADD, DELETE, CUT, COPY, PASTE};
        private final Identifier textureId;
        private final byte type;
        private final MutableComponent text;

        ButtonType(Identifier textureId, int type, MutableComponent text) {
            this.textureId = textureId;
            this.type = (byte) type;
            this.text = text;
        }

        ButtonType(Identifier textureId, int type) {
            this(textureId, type, null);
        }

        ButtonType(Identifier textureId, MutableComponent text) {
            this(textureId, -1, text);
        }

        public byte getType() {
            return this.type;
        }

        public Identifier getTextureId() {
            return this.textureId;
        }

        public MutableComponent getText() {
            return this.text;
        }
    }
}
