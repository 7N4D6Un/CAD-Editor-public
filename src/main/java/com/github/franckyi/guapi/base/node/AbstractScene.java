package com.github.franckyi.guapi.base.node;

import com.github.franckyi.databindings.api.BooleanProperty;
import com.github.franckyi.databindings.api.IntegerProperty;
import com.github.franckyi.databindings.api.ObjectProperty;
import com.github.franckyi.databindings.api.ObservableBooleanValue;
import com.github.franckyi.databindings.api.ObservableObjectValue;
import com.github.franckyi.databindings.api.ObservableValue;
import com.github.franckyi.guapi.api.Guapi;
import com.github.franckyi.guapi.api.event.MouseButtonEvent;
import com.github.franckyi.guapi.api.event.ScreenEvent;
import com.github.franckyi.guapi.api.event.ScreenEventListener;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.Scene;
import com.github.franckyi.guapi.api.node.ScreenEventHandler;
import com.github.franckyi.guapi.api.util.Insets;
import com.github.franckyi.guapi.api.util.ScreenEventType;
import com.github.franckyi.guapi.base.event.ScreenEventHandlerDelegate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class AbstractScene implements Scene {
    protected final ObjectProperty<Node> focusedProperty;
    protected final ObjectProperty<Node> hoveredProperty;
    protected final ScreenEventHandler eventHandlerDelegate;
    protected final List<Consumer<Scene>> onShowListeners;
    protected final List<Consumer<Scene>> onHideListeners;
    private final ObjectProperty<Node> rootProperty;
    private final BooleanProperty fullScreenProperty;
    private final IntegerProperty widthProperty;
    private final IntegerProperty heightProperty;
    private final ObjectProperty<Insets> paddingProperty;
    private final BooleanProperty texturedBackgroundProperty;
    private final BooleanProperty closeOnEscProperty;
    private final ObservableObjectValue<Node> focusedPropertyReadOnly;
    private final ObservableObjectValue<Node> hoveredPropertyReadOnly;
    private final ObservableValue<Scene> sceneProperty;
    private final ObservableBooleanValue disabledProperty;
    protected boolean shouldUpdateChildrenPos;

    protected AbstractScene() {
        this(null);
    }

    protected AbstractScene(Node root) {
        this(root, false);
    }

    protected AbstractScene(Node root, boolean fullScreen) {
        this(root, fullScreen, false);
    }

    protected AbstractScene(Node root, boolean fullScreen, boolean texturedBackground) {
        this.focusedProperty = ObjectProperty.create();
        this.hoveredProperty = ObjectProperty.create();
        this.eventHandlerDelegate = new ScreenEventHandlerDelegate();
        this.onShowListeners = new ArrayList<>();
        this.onHideListeners = new ArrayList<>();
        this.rootProperty = ObjectProperty.create();
        this.fullScreenProperty = BooleanProperty.create();
        this.widthProperty = IntegerProperty.create(Node.INFINITE_SIZE);
        this.heightProperty = IntegerProperty.create(Node.INFINITE_SIZE);
        this.paddingProperty = ObjectProperty.create(Insets.NONE);
        this.texturedBackgroundProperty = BooleanProperty.create();
        this.closeOnEscProperty = BooleanProperty.create(true);
        this.focusedPropertyReadOnly = ObservableObjectValue.readOnly(this.focusedProperty);
        this.hoveredPropertyReadOnly = ObservableObjectValue.readOnly(this.hoveredProperty);
        this.sceneProperty = ObservableValue.unmodifiable(this);
        this.disabledProperty = ObservableBooleanValue.FALSE;
        rootProperty().addListener((oldVal, newVal) -> {
            if (oldVal != null && oldVal.getParent() == this) {
                oldVal.setParent(null);
                bindFullScreen(oldVal, false);
            }
            if (newVal != null) {
                if (newVal.getParent() != null) {
                    Guapi.getDefaultLogger().error(Guapi.LOG_MARKER, "Can't set Node \"" + newVal + "\" as Scene root: node already has a Parent \"" + String.valueOf(newVal.getParent()) + "\"");
                } else {
                    newVal.setParent(this);
                    bindFullScreen(newVal, isFullScreen());
                }
            }
        });
        fullScreenProperty().addListener(fullScreenValue -> {
            if (rootProperty().hasValue()) {
                bindFullScreen(getRoot(), fullScreenValue.booleanValue());
            }
        });
        addListener(ScreenEventType.KEY_PRESSED, e -> {
            if (e.getKeyCode() == 256 && isCloseOnEsc()) {
                Guapi.getScreenHandler().hideScene();
                e.consume();
            }
        });
        setRoot(root);
        setFullScreen(fullScreen);
        setTexturedBackground(texturedBackground);
        paddingProperty().addListener(this::shouldUpdateChildren);
    }

    @Override
    public ObjectProperty<Node> rootProperty() {
        return this.rootProperty;
    }

    @Override
    public BooleanProperty fullScreenProperty() {
        return this.fullScreenProperty;
    }

    @Override
    public IntegerProperty widthProperty() {
        return this.widthProperty;
    }

    @Override
    public IntegerProperty heightProperty() {
        return this.heightProperty;
    }

    @Override
    public ObjectProperty<Insets> paddingProperty() {
        return this.paddingProperty;
    }

    @Override
    public BooleanProperty texturedBackgroundProperty() {
        return this.texturedBackgroundProperty;
    }

    @Override
    public BooleanProperty closeOnEscProperty() {
        return this.closeOnEscProperty;
    }

    @Override
    public ObservableObjectValue<Node> focusedProperty() {
        return this.focusedPropertyReadOnly;
    }

    @Override
    public void askFocus(Node node) {
        setFocused(node);
    }

    protected void setFocused(Node value) {
        this.focusedProperty.setValue(value);
    }

    @Override
    public ObservableObjectValue<Node> hoveredProperty() {
        return this.hoveredPropertyReadOnly;
    }

    protected void setHovered(Node value) {
        this.hoveredProperty.setValue(value);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        if (this.shouldUpdateChildrenPos) {
            updateChildrenPos();
            this.shouldUpdateChildrenPos = false;
        }
        if (rootProperty().hasValue()) {
            while (getRoot().preRender(guiGraphicsExtractor, mouseX, mouseY, delta)) {
            }
            getRoot().render(guiGraphicsExtractor, mouseX, mouseY, delta);
            getRoot().postRender(guiGraphicsExtractor, mouseX, mouseY, delta);
        }
    }

    @Override
    public void shouldComputeSize() {
    }

    @Override
    public void shouldUpdateChildren() {
        this.shouldUpdateChildrenPos = true;
    }

    @Override
    public void tick() {
        if (rootProperty().hasValue()) {
            getRoot().doTick();
        }
    }

    @Override
    public void show() {
        this.onShowListeners.forEach(listener -> {
            listener.accept(this);
        });
    }

    @Override
    public void onShow(Consumer<Scene> listener) {
        this.onShowListeners.add(listener);
    }

    @Override
    public void hide() {
        this.onHideListeners.forEach(listener -> {
            listener.accept(this);
        });
    }

    @Override
    public void onHide(Consumer<Scene> listener) {
        this.onHideListeners.add(listener);
    }

    @Override
    public <E extends ScreenEvent> void handleEvent(ScreenEventType<E> target, E event) {
        if (getRoot() != null) {
            target.ifMouseEvent(event, (t, e) -> {
                getRoot().handleEvent(target, event);
                if (!e.isConsumed()) {
                    if (e instanceof MouseButtonEvent) {
                        MouseButtonEvent be = (MouseButtonEvent) e;
                        if (target == ScreenEventType.MOUSE_CLICKED && be.getButton() == 0) {
                            if (e.getTarget() != null && !e.getTarget().isDisabled() && e.getTarget().isVisible()) {
                                e.getTarget().handleEvent(ScreenEventType.ACTION, be);
                                if (!e.isConsumed()) {
                                    setFocused(e.getTarget());
                                    return;
                                }
                                return;
                            }
                            setFocused(e.getTarget());
                            return;
                        }
                        return;
                    }
                    if (target == ScreenEventType.MOUSE_MOVED) {
                        setHovered(e.getTarget());
                    }
                }
            }, () -> {
                if (getFocused() != null && !getFocused().isDisabled()) {
                    getFocused().handleEvent(target, event);
                }
            });
        }
        this.eventHandlerDelegate.handleEvent(target, event);
    }

    @Override
    public <E extends ScreenEvent> void addListener(ScreenEventType<E> target, ScreenEventListener<E> listener) {
        this.eventHandlerDelegate.addListener(target, listener);
    }

    @Override
    public <E extends ScreenEvent> void removeListener(ScreenEventType<E> target, ScreenEventListener<E> listener) {
        this.eventHandlerDelegate.removeListener(target, listener);
    }

    @Override
    public ObservableValue<Scene> sceneProperty() {
        return this.sceneProperty;
    }

    @Override
    public ObservableBooleanValue disabledProperty() {
        return this.disabledProperty;
    }

    @Override
    public int getMaxChildrenWidth() {
        return getWidth() - getPadding().getHorizontal();
    }

    @Override
    public int getMaxChildrenHeight() {
        return getHeight() - getPadding().getVertical();
    }

    protected void updateChildrenPos() {
        if (rootProperty().hasValue()) {
            getRoot().setX(getPadding().getLeft());
            getRoot().setY(getPadding().getTop());
        }
    }

    protected void bindFullScreen(Node root, boolean bind) {
        if (bind) {
            root.prefWidthProperty().bind(widthProperty());
            root.prefHeightProperty().bind(heightProperty());
        } else {
            root.prefWidthProperty().unbind();
            root.prefHeightProperty().unbind();
        }
    }
}
