package com.github.franckyi.guapi.base.theme.vanilla.delegate;

import com.github.franckyi.guapi.api.Color;
import com.github.franckyi.guapi.api.RenderHelper;
import com.github.franckyi.guapi.api.event.MouseDragEvent;
import com.github.franckyi.guapi.api.event.MouseEvent;
import com.github.franckyi.guapi.api.event.MouseScrollEvent;
import com.github.franckyi.guapi.api.node.ListNode;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.util.ScreenEventType;
import com.github.franckyi.guapi.base.theme.vanilla.delegate.AbstractVanillaListNodeSkinDelegate.NodeEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractVanillaListNodeSkinDelegate<N extends ListNode<E>, E, T extends NodeEntry<N, E, T>> extends AbstractSelectionList<T> implements VanillaWidgetSkinDelegate {
    protected final N node;
    protected boolean shouldRefreshSize;
    protected boolean shouldRefreshList;
    protected boolean shouldScrollTo;
    protected boolean shouldChangeFocus;

    protected abstract void createList();

    public AbstractVanillaListNodeSkinDelegate(N node) {
        super(Minecraft.getInstance(), 0, 0, 0, node.getItemHeight());
        this.shouldRefreshSize = true;
        this.shouldRefreshList = true;
        this.shouldScrollTo = false;
        this.shouldChangeFocus = false;
        this.node = node;
        Runnable rs = this::shouldRefreshSize;
        node.xProperty().addListener(rs);
        node.yProperty().addListener(rs);
        node.baseXProperty().addListener(rs);
        node.baseYProperty().addListener(rs);
        node.widthProperty().addListener(rs);
        node.heightProperty().addListener(rs);
        node.fullWidthProperty().addListener(rs);
        node.fullHeightProperty().addListener(rs);
        node.rootProperty().addListener(this::shouldRefreshList);
        node.scrollToProperty().addListener(this::shouldScrollTo);
        node.focusedElementProperty().addListener(this::shouldChangeFocus);
    }

    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    public N getNode() {
        return this.node;
    }

    public int getRowWidth() {
        return (this.node.getWidth() - this.node.getPadding().getHorizontal()) - 6;
    }

    protected int scrollBarX() {
        return this.node.getRight() - 6;
    }

    public int getRowLeft() {
        return this.node.getLeft() + this.node.getPadding().getLeft();
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (getEntryAtPosition(event.x(), event.y()) == null && (event.x() < scrollBarX() || event.x() > this.node.getRight())) {
            setFocused((GuiEventListener) null);
        }
        return super.mouseClicked(event, isDoubleClick);
    }

    @SuppressWarnings("unchecked")
    public void setFocused(@Nullable GuiEventListener listener) {
        super.setFocused(listener);
        if (listener == null) {
            this.node.setFocusedElement(null);
        } else if (listener instanceof NodeEntry<?, ?, ?> entry) {
            this.node.setFocusedElement((E) entry.getItem());
        }
    }

    @Override
    public boolean preRender(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        boolean res = false;
        if (this.shouldRefreshSize) {
            refreshSize();
            res = true;
        }
        if (this.shouldRefreshList) {
            refreshList();
            res = true;
        }
        if (this.shouldScrollTo) {
            scrollTo();
            res = true;
        }
        if (this.shouldChangeFocus) {
            changeFocus();
            res = true;
        }
        return res;
    }

    @Override
    public void postRender(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float f) {
        for (T entry : children()) {
            entry.getNode().postRender(guiGraphicsExtractor, mouseX, mouseY, f);
        }
    }

    protected void shouldRefreshSize() {
        this.shouldRefreshSize = true;
    }

    protected void shouldRefreshList() {
        this.shouldRefreshList = true;
    }

    protected void shouldScrollTo() {
        this.shouldScrollTo = true;
    }

    protected void shouldChangeFocus() {
        this.shouldChangeFocus = true;
    }

    protected void refreshSize() {
        setSize(this.node.getWidth(), this.node.getHeight());
        setPosition(this.node.getX(), this.node.getY());
        setScrollAmount(scrollAmount());
        this.shouldRefreshSize = false;
    }

    public int getRight() {
        return this.node.getRight();
    }

    public int getBottom() {
        return this.node.getBottom();
    }

    protected void refreshList() {
        clearEntries();
        createList();
        setScrollAmount(scrollAmount());
        this.shouldRefreshList = false;
    }

    protected void scrollTo() {
        for (T nodeEntry : children()) {
            if (nodeEntry.getItem() == this.node.getScrollTo()) {
                centerScrollOn(nodeEntry);
                break;
            }
        }
        this.shouldScrollTo = false;
    }

    protected void changeFocus() {
        for (T nodeEntry : children()) {
            if (nodeEntry.getItem() == this.node.getFocusedElement()) {
                super.setFocused(nodeEntry);
                break;
            }
        }
        this.shouldChangeFocus = false;
    }

    @Override
    public void doTick() {
        for (T entry : children()) {
            entry.getNode().doTick();
        }
    }

    @Override
    public void mouseClicked(com.github.franckyi.guapi.api.event.MouseButtonEvent event) {
        VanillaWidgetSkinDelegate.super.mouseClicked(event);
        handleMouseEvent(ScreenEventType.MOUSE_CLICKED, event);
    }

    @Override
    public void mouseReleased(com.github.franckyi.guapi.api.event.MouseButtonEvent event) {
        VanillaWidgetSkinDelegate.super.mouseReleased(event);
        handleMouseEvent(ScreenEventType.MOUSE_RELEASED, event);
    }

    @Override
    public void mouseDragged(MouseDragEvent event) {
        VanillaWidgetSkinDelegate.super.mouseDragged(event);
        handleMouseEvent(ScreenEventType.MOUSE_DRAGGED, event);
    }

    @Override
    public void mouseScrolled(MouseScrollEvent event) {
        VanillaWidgetSkinDelegate.super.mouseScrolled(event);
        handleMouseEvent(ScreenEventType.MOUSE_SCOLLED, event);
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        VanillaWidgetSkinDelegate.super.mouseMoved(event);
        handleMouseEvent(ScreenEventType.MOUSE_MOVED, event);
    }

    protected <EE extends MouseEvent> void handleMouseEvent(ScreenEventType<EE> target, EE event) {
        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();
        if (mouseX >= this.node.getLeft() && mouseX <= this.node.getRight() && mouseY >= this.node.getTop() && mouseY <= this.node.getBottom() && getEntryAtPosition(mouseX, mouseY) instanceof NodeEntry<N, E, T> nodeEntry && nodeEntry.getNode() != null) {
            nodeEntry.getNode().handleEvent(target, event);
        }
    }

    protected static abstract class NodeEntry<N extends ListNode<E>, E, T extends NodeEntry<N, E, T>> extends AbstractSelectionList.Entry<T> {
        private final AbstractVanillaListNodeSkinDelegate<N, E, T> list;
        private final E item;
        private Node node;

        public NodeEntry(AbstractVanillaListNodeSkinDelegate<N, E, T> list, E item) {
            this(list, item, null);
        }

        public NodeEntry(AbstractVanillaListNodeSkinDelegate<N, E, T> list, E item, Node node) {
            this.list = list;
            this.item = item;
            this.node = node;
        }

        public AbstractVanillaListNodeSkinDelegate<N, E, T> getList() {
            return this.list;
        }

        public E getItem() {
            return this.item;
        }

        public Node getNode() {
            return this.node;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        protected void extractBackground(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, int entryWidth, int entryHeight) {
            if (getList().getFocused() == this) {
                RenderHelper.fillRectangle(guiGraphicsExtractor, x - 2, y - 2, x + entryWidth + 3, y + entryHeight + 2, Color.fromRGBA(255, 255, 255, 79));
            }
        }

        public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
            return this.list.node.isChildrenFocusable();
        }
    }
}
