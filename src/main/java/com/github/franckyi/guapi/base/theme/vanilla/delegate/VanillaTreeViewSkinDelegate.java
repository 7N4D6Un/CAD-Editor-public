package com.github.franckyi.guapi.base.theme.vanilla.delegate;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.TexturedButton;
import com.github.franckyi.guapi.api.node.TreeView;
import com.github.franckyi.guapi.api.node.TreeView.TreeItem;
import com.github.franckyi.guapi.api.node.builder.HBoxBuilder;
import com.github.franckyi.guapi.api.node.builder.TexturedButtonBuilder;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class VanillaTreeViewSkinDelegate<E extends TreeView.TreeItem<E>> extends AbstractVanillaListNodeSkinDelegate<TreeView<E>, E, VanillaTreeViewSkinDelegate.NodeEntry<E>> {
    public VanillaTreeViewSkinDelegate(TreeView<E> node) {
        super(node);
        node.rootItemProperty().addListener(this::shouldRefreshList);
        node.showRootProperty().addListener(this::shouldRefreshList);
        node.rootItemProperty().mapToObservableBoolean(value -> value.childrenChangedProperty(), false).addListener(newVal -> {
            if (newVal.booleanValue()) {
                shouldRefreshList();
                node.getRoot().setChildrenChanged(false);
            }
        });
    }

    @Override
    protected void createList() {
        if (this.node.rootItemProperty().hasValue()) {
            if (this.node.isShowRoot()) {
                addChild(this.node.getRoot(), 0);
                return;
            }
            for (E child : this.node.getRoot().getChildren()) {
                addChild(child, 0);
            }
        }
    }

    private void addChild(E item, int increment) {
        NodeEntry<E> entry = new NodeEntry<>(this, item, this.node.getRenderer().getView(item), increment);
        addEntry(entry);
        if (item == this.node.getFocusedElement()) {
            setFocused(entry);
        }
        int childIncrement = increment + 1;
        if (item.isExpanded()) {
            for (E child : item.getChildren()) {
                addChild(child, childIncrement);
            }
        }
    }

    protected static class NodeEntry<E extends TreeView.TreeItem<E>> extends AbstractVanillaListNodeSkinDelegate.NodeEntry<TreeView<E>, E, NodeEntry<E>> {
        private static final Identifier TREE_VIEW_WIDGETS = Identifier.fromNamespaceAndPath("cadeditor", "textures/gui/tree_view_widgets.png");
        private TexturedButton button;
        private final int increment;

        public NodeEntry(VanillaTreeViewSkinDelegate<E> list, E item, Node node, int increment) {
            super(list, item);
            setNode(GuapiHelper.hBox((Consumer<HBoxBuilder>) hBoxBuilder -> {
                if (item.getChildren().isEmpty()) {
                    hBoxBuilder.add(GuapiHelper.hBox().prefSize(16, 16));
                } else {
                    TexturedButton texturedButton = (TexturedButton) ((TexturedButtonBuilder) GuapiHelper.texturedButton(TREE_VIEW_WIDGETS, 32, 32, false).prefSize(16, 16)).action(() -> {
                        if (!item.isExpanded()) {
                            chainExpand(item);
                        } else {
                            item.setExpanded(false);
                        }
                        list.shouldRefreshList();
                    });
                    this.button = texturedButton;
                    hBoxBuilder.add(texturedButton);
                    this.button.imageXProperty().bind(item.expandedProperty().mapToInt(value -> value.booleanValue() ? 16 : 0));
                }
                ((HBoxBuilder) ((HBoxBuilder) ((HBoxBuilder) hBoxBuilder.add(node)).align(GuapiHelper.CENTER_LEFT)).spacing(5)).setParent(list.node);
            }));
            this.increment = increment;
        }

        private void chainExpand(E item) {
            item.setExpanded(true);
            if (item.getChildren().size() == 1) {
                chainExpand(item.getChildren().get(0));
            }
        }

        public void extractContent(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = getContentX();
            int y = getContentY();
            int entryWidth = getContentWidth();
            int entryHeight = getContentHeight();
            int incr = this.increment * getList().node.getChildrenIncrement();
            getNode().setX(x + incr);
            getNode().setY(y);
            getNode().setParentPrefWidth(entryWidth - incr);
            getNode().setParentPrefHeight(entryHeight);
            if (this.button != null) {
                this.button.setImageY(this.button.inBounds((double) mouseX, (double) mouseY) ? 16 : 0);
            }
            extractBackground(guiGraphicsExtractor, x, y, entryWidth, entryHeight);
            while (getNode().preRender(guiGraphicsExtractor, mouseX, mouseY, tickDelta)) {
            }
            getNode().render(guiGraphicsExtractor, mouseX, mouseY, tickDelta);
        }
    }
}
