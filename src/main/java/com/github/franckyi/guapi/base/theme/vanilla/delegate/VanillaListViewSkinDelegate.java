package com.github.franckyi.guapi.base.theme.vanilla.delegate;

import com.github.franckyi.guapi.api.node.ListView;
import com.github.franckyi.guapi.api.node.Node;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class VanillaListViewSkinDelegate<E> extends AbstractVanillaListNodeSkinDelegate<ListView<E>, E, VanillaListViewSkinDelegate.NodeEntry<E>> {
    public VanillaListViewSkinDelegate(ListView<E> node) {
        super(node);
        node.getItems().addListener(this::shouldRefreshList);
    }

    @Override
    protected void createList() {
        for (E item : this.node.getItems()) {
            Node view = this.node.getRenderer().getView(item);
            view.setParent(this.node);
            NodeEntry<E> entry = new NodeEntry<>(this, item, view);
            addEntry(entry, this.node.getItemHeight(item));
            if (item == this.node.getFocusedElement()) {
                setFocused(entry);
            }
        }
    }

    protected static class NodeEntry<E> extends AbstractVanillaListNodeSkinDelegate.NodeEntry<ListView<E>, E, NodeEntry<E>> {
        public NodeEntry(VanillaListViewSkinDelegate<E> list, E item, Node node) {
            super(list, item, node);
        }

        public void extractContent(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = getContentX();
            int y = getContentY();
            int entryWidth = getContentWidth();
            int entryHeight = getContentHeight();
            getNode().setX(x);
            getNode().setY(y);
            getNode().setParentPrefWidth(entryWidth);
            getNode().setParentPrefHeight(entryHeight);
            extractBackground(guiGraphicsExtractor, x, y, entryWidth, entryHeight);
            while (getNode().preRender(guiGraphicsExtractor, mouseX, mouseY, tickDelta)) {
            }
            getNode().render(guiGraphicsExtractor, mouseX, mouseY, tickDelta);
        }
    }
}
