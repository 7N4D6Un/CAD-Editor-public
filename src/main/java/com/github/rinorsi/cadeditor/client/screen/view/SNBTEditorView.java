package com.github.rinorsi.cadeditor.client.screen.view;

import com.github.franckyi.databindings.api.ObservableList;
import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.Label;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.TexturedButton;
import com.github.franckyi.guapi.api.node.TexturedToggleButton;
import com.github.franckyi.guapi.api.node.TreeView;
import com.github.franckyi.guapi.api.node.VBox;
import com.github.franckyi.guapi.api.node.builder.LabelBuilder;
import com.github.franckyi.guapi.api.node.builder.TexturedToggleButtonBuilder;
import com.github.franckyi.guapi.api.node.builder.TreeViewBuilder;
import com.github.franckyi.guapi.api.node.builder.VBoxBuilder;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.model.SNBTPreviewNode;
import com.github.rinorsi.cadeditor.client.screen.widget.SyntaxHighlightingTextArea;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public class SNBTEditorView extends ScreenView {
    private SyntaxHighlightingTextArea textArea;
    private TexturedButton formatButton;
    private TexturedToggleButton previewToggle;
    private VBox editorContainer;
    private VBox previewPane;
    private TreeView<SNBTPreviewNode> previewTree;
    private Label previewStatus;

    @Override 
    protected MutableComponent getHeaderLabelText() {
        return ModTexts.editorTitle(ModTexts.gui("raw_data_text"));
    }

    @Override 
    protected Node createButtonBar() {
        Node res = super.createButtonBar();
        ObservableList<Node> children = this.buttonBarLeft.getChildren();
        this.formatButton = createButton(ModTextures.FORMAT, ModTexts.FORMAT);
        this.previewToggle = (TexturedToggleButton) ((TexturedToggleButtonBuilder) GuapiHelper.texturedToggleButton(ModTextures.LIST_TAG, 16, 16, false).tooltip(ModTexts.SNBT_PREVIEW_TOGGLE)).action(this::togglePreview);
        children.addAll(GuapiHelper.hBox(2, this.formatButton, this.previewToggle));
        return res;
    }

    @Override 
    protected Node createEditor() {
        this.textArea = new SyntaxHighlightingTextArea();
        this.textArea.minHeight(200);
        this.textArea.prefHeight(Node.INFINITE_SIZE);
        this.textArea.maxHeight(Node.INFINITE_SIZE);
        this.previewPane = GuapiHelper.vBox((Consumer<VBoxBuilder>) preview -> {
            ((VBoxBuilder) preview.spacing(4)).fillWidth();
            TreeView<SNBTPreviewNode> treeView = (TreeView) ((TreeViewBuilder) ((TreeViewBuilder) ((TreeViewBuilder) GuapiHelper.treeView(SNBTPreviewNode.class).showRoot().itemHeight(18)).childrenFocusable()).padding(4)).renderer(node -> {
                SNBTPreviewNode n = (SNBTPreviewNode) node;
                return GuapiHelper.label((Component) GuapiHelper.text(n.getLabel()));
            });
            this.previewTree = treeView;
            preview.add(treeView, 1);
            Label label = (Label) ((LabelBuilder) GuapiHelper.label((Component) ModTexts.SNBT_PREVIEW_INVALID).textAlign(GuapiHelper.CENTER).visible(false)).prefHeight(14);
            this.previewStatus = label;
            preview.add(label);
        });
        return this.editorContainer = GuapiHelper.vBox((Consumer<VBoxBuilder>) container -> {
            container.fillWidth();
            container.add(this.textArea, 1);
        });
    }

    public SyntaxHighlightingTextArea getTextArea() {
        return this.textArea;
    }

    public TexturedButton getFormatButton() {
        return this.formatButton;
    }

    public TexturedToggleButton getPreviewToggle() {
        return this.previewToggle;
    }

    public TreeView<SNBTPreviewNode> getPreviewTree() {
        return this.previewTree;
    }

    public Label getPreviewStatus() {
        return this.previewStatus;
    }

    private void togglePreview() {
        boolean show = this.previewToggle.isActive();
        this.editorContainer.getChildren().clear();
        if (show) {
            this.editorContainer.getChildren().add(this.previewPane);
            this.editorContainer.setWeight(this.previewPane, 1);
        } else {
            this.editorContainer.getChildren().add(this.textArea);
            this.editorContainer.setWeight(this.textArea, 1);
        }
    }

    @Override 
    public void build() {
        super.build();
        togglePreview();
    }

    public void refreshPreviewPane() {
        togglePreview();
    }
}
