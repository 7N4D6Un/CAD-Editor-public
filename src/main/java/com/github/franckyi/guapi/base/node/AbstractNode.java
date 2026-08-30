package com.github.franckyi.guapi.base.node;

import com.github.franckyi.databindings.api.BooleanProperty;
import com.github.franckyi.databindings.api.IntegerProperty;
import com.github.franckyi.databindings.api.ObjectProperty;
import com.github.franckyi.databindings.api.ObservableBooleanValue;
import com.github.franckyi.databindings.api.ObservableIntegerValue;
import com.github.franckyi.databindings.api.ObservableList;
import com.github.franckyi.databindings.api.ObservableObjectValue;
import com.github.franckyi.databindings.api.ObservableValue;
import com.github.franckyi.guapi.api.Guapi;
import com.github.franckyi.guapi.api.event.ScreenEvent;
import com.github.franckyi.guapi.api.event.ScreenEventListener;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.Parent;
import com.github.franckyi.guapi.api.node.Scene;
import com.github.franckyi.guapi.api.node.ScreenEventHandler;
import com.github.franckyi.guapi.api.theme.Skin;
import com.github.franckyi.guapi.api.util.Insets;
import com.github.franckyi.guapi.api.util.ScreenEventType;
import com.github.franckyi.guapi.base.event.ScreenEventHandlerDelegate;
import java.util.function.Function;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public abstract class AbstractNode implements Node {
    protected Skin<? super Node> skin;
    protected final IntegerProperty xProperty = IntegerProperty.create();
    protected final IntegerProperty yProperty = IntegerProperty.create();
    protected final IntegerProperty widthProperty = IntegerProperty.create();
    protected final IntegerProperty heightProperty = IntegerProperty.create();
    private final IntegerProperty minWidthProperty = IntegerProperty.create();
    private final IntegerProperty minHeightProperty = IntegerProperty.create();
    private final IntegerProperty prefWidthProperty = IntegerProperty.create(-1);
    private final IntegerProperty prefHeightProperty = IntegerProperty.create(-1);
    private final IntegerProperty maxWidthProperty = IntegerProperty.create(Node.INFINITE_SIZE);
    private final IntegerProperty maxHeightProperty = IntegerProperty.create(Node.INFINITE_SIZE);
    private final IntegerProperty parentPrefWidthProperty = IntegerProperty.create(-1);
    private final IntegerProperty parentPrefHeightProperty = IntegerProperty.create(-1);
    protected final IntegerProperty computedWidthProperty = IntegerProperty.create();
    private final ObservableIntegerValue computedWidthPropertyReadOnly = ObservableIntegerValue.readOnly(this.computedWidthProperty);
    protected final IntegerProperty computedHeightProperty = IntegerProperty.create();
    private final ObservableIntegerValue computedHeightPropertyReadOnly = ObservableIntegerValue.readOnly(this.computedHeightProperty);
    private final IntegerProperty backgroundColorProperty = IntegerProperty.create(DEFAULT_BACKGROUND_COLOR);
    private final ObjectProperty<Insets> paddingProperty = ObjectProperty.create(Insets.NONE);
    private final ObservableList<Component> tooltip = ObservableList.create();
    protected final ObjectProperty<Parent> parentProperty = ObjectProperty.create();
    protected final ObjectProperty<Scene> sceneProperty = ObjectProperty.create();
    private final ObservableObjectValue<Scene> scenePropertyReadOnly = ObservableObjectValue.readOnly(this.sceneProperty);
    private final BooleanProperty visibleProperty = BooleanProperty.create(true);
    private final BooleanProperty disableProperty = BooleanProperty.create();
    private final ObservableBooleanValue disabledProperty = disableProperty().or(parentProperty().mapToObservableBoolean(value -> value.disabledProperty(), false));
    private final ObservableBooleanValue rootProperty = sceneProperty().mapToObservable((Function<Scene, ObservableValue<Node>>) value -> value.rootProperty(), (Node) null).is(this);
    private final ObservableBooleanValue focusedProperty = sceneProperty().mapToObservable((Function<Scene, ObservableValue<Node>>) value -> value.focusedProperty(), (Node) null).is(this);
    private final ObservableBooleanValue hoveredProperty = sceneProperty().mapToObservable((Function<Scene, ObservableValue<Node>>) value -> value.hoveredProperty(), (Node) null).is(this);
    protected final ScreenEventHandler eventHandlerDelegate = new ScreenEventHandlerDelegate();
    protected boolean shouldComputeSize = true;
    protected boolean shouldUpdateSize = true;

    protected abstract Class<?> getType();

    protected AbstractNode() {
        minWidthProperty().addListener(this::shouldUpdateSize);
        minHeightProperty().addListener(this::shouldUpdateSize);
        prefWidthProperty().addListener(this::shouldUpdateSize);
        prefHeightProperty().addListener(this::shouldUpdateSize);
        maxWidthProperty().addListener(this::shouldUpdateSize);
        maxHeightProperty().addListener(this::shouldUpdateSize);
        parentPrefWidthProperty().addListener(this::shouldUpdateSize);
        parentPrefHeightProperty().addListener(this::shouldUpdateSize);
        computedWidthProperty().addListener(this::shouldUpdateSize);
        computedHeightProperty().addListener(this::shouldUpdateSize);
        widthProperty().addListener(this::updateParentWidth);
        heightProperty().addListener(this::updateParentHeight);
        paddingProperty().addListener(this::shouldUpdateSize);
        parentProperty().addListener(this::updateScene);
    }

    @Override
    public IntegerProperty xProperty() {
        return this.xProperty;
    }

    @Override
    public IntegerProperty yProperty() {
        return this.yProperty;
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
    public IntegerProperty minWidthProperty() {
        return this.minWidthProperty;
    }

    @Override
    public IntegerProperty minHeightProperty() {
        return this.minHeightProperty;
    }

    @Override
    public IntegerProperty prefWidthProperty() {
        return this.prefWidthProperty;
    }

    @Override
    public IntegerProperty prefHeightProperty() {
        return this.prefHeightProperty;
    }

    @Override
    public IntegerProperty maxWidthProperty() {
        return this.maxWidthProperty;
    }

    @Override
    public IntegerProperty maxHeightProperty() {
        return this.maxHeightProperty;
    }

    @Override
    public IntegerProperty parentPrefWidthProperty() {
        return this.parentPrefWidthProperty;
    }

    @Override
    public IntegerProperty parentPrefHeightProperty() {
        return this.parentPrefHeightProperty;
    }

    @Override
    public ObservableIntegerValue computedWidthProperty() {
        return this.computedWidthPropertyReadOnly;
    }

    protected void setComputedWidth(int value) {
        this.computedWidthProperty.setValue(value);
    }

    @Override
    public ObservableIntegerValue computedHeightProperty() {
        return this.computedHeightPropertyReadOnly;
    }

    protected void setComputedHeight(int value) {
        this.computedHeightProperty.setValue(value);
    }

    @Override
    public IntegerProperty backgroundColorProperty() {
        return this.backgroundColorProperty;
    }

    @Override
    public ObjectProperty<Insets> paddingProperty() {
        return this.paddingProperty;
    }

    @Override
    public ObservableList<Component> getTooltip() {
        return this.tooltip;
    }

    @Override
    public ObjectProperty<Parent> parentProperty() {
        return this.parentProperty;
    }

    @Override
    public ObservableObjectValue<Scene> sceneProperty() {
        return this.scenePropertyReadOnly;
    }

    @Override
    public BooleanProperty visibleProperty() {
        return this.visibleProperty;
    }

    @Override
    public BooleanProperty disableProperty() {
        return this.disableProperty;
    }

    @Override
    public ObservableBooleanValue disabledProperty() {
        return this.disabledProperty;
    }

    @Override
    public ObservableBooleanValue rootProperty() {
        return this.rootProperty;
    }

    @Override
    public ObservableBooleanValue focusedProperty() {
        return this.focusedProperty;
    }

    @Override
    public ObservableBooleanValue hoveredProperty() {
        return this.hoveredProperty;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected <N extends Node> Skin<? super N> getSkin() {
        if (this.skin == null) {
            this.skin = Guapi.getTheme().supplySkin((N) this, (Class) getType());
        }
        return (Skin<? super N>) this.skin;
    }

    protected void resetSkin() {
        this.skin = null;
    }

    @Override
    public <E extends ScreenEvent> void handleEvent(ScreenEventType<E> target, E event) {
        target.ifMouseEvent(event, this::handleMouseEvent, () -> {
            notifyEvent(target, event);
        });
    }

    protected <E extends ScreenEvent> void notifyEvent(ScreenEventType<E> target, E event) {
        target.onEvent(this, event);
        this.eventHandlerDelegate.handleEvent(target, event);
        getSkin().onEvent(target, event);
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
    public boolean preRender(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        boolean res = checkRender();
        if (isVisible()) {
            res |= getSkin().preRender(this, guiGraphicsExtractor, mouseX, mouseY, delta);
        }
        return res;
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        if (isVisible()) {
            getSkin().render(this, guiGraphicsExtractor, mouseX, mouseY, delta);
        }
    }

    @Override
    public void postRender(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        if (isVisible()) {
            getSkin().postRender(this, guiGraphicsExtractor, mouseX, mouseY, delta);
        }
    }

    public void doTick() {
        getSkin().doTick();
    }

    @Override
    public boolean checkRender() {
        boolean res = false;
        if (this.shouldComputeSize) {
            computeSize();
            res = true;
        }
        if (this.shouldUpdateSize) {
            updateSize();
            res = true;
        }
        return res;
    }

    @Override
    public void shouldComputeSize() {
        this.shouldComputeSize = true;
    }

    protected void computeSize() {
        this.shouldComputeSize = false;
        computeWidth();
        computeHeight();
    }

    private void computeWidth() {
        setComputedWidth(getSkin().computeWidth(this) + getPadding().getHorizontal());
    }

    private void computeHeight() {
        setComputedHeight(getSkin().computeHeight(this) + getPadding().getVertical());
    }

    protected void shouldUpdateSize() {
        this.shouldUpdateSize = true;
    }

    protected void updateSize() {
        this.shouldUpdateSize = false;
        updateWidth();
        updateHeight();
    }

    private void updateWidth() {
        int width = getPrefWidth();
        if (width == -1) {
            if (getParentPrefWidth() != -1) {
                width = getParentPrefWidth();
            } else {
                width = getComputedWidth();
            }
        }
        int clampedWidth = Math.max(Math.min(width, getMaxWidth()), getMinWidth());
        if (parentProperty().hasValue()) {
            clampedWidth = Math.min(clampedWidth, getParent().getMaxChildrenWidth());
        }
        setWidth(clampedWidth);
    }

    private void updateHeight() {
        int height = getPrefHeight();
        if (height == -1) {
            if (getParentPrefHeight() != -1) {
                height = getParentPrefHeight();
            } else {
                height = getComputedHeight();
            }
        }
        int clampedHeight = Math.max(Math.min(height, getMaxHeight()), getMinHeight());
        if (parentProperty().hasValue()) {
            clampedHeight = Math.min(clampedHeight, getParent().getMaxChildrenHeight());
        }
        setHeight(clampedHeight);
    }

    private void updateParentWidth() {
        if (getParent() != null) {
            getParent().shouldComputeSize();
            getParent().shouldUpdateChildren();
        }
    }

    private void updateParentHeight() {
        if (getParent() != null) {
            getParent().shouldComputeSize();
            getParent().shouldUpdateChildren();
        }
    }

    private void updateScene(Parent newVal) {
        if (this.sceneProperty.isBound()) {
            this.sceneProperty.unbind();
        }
        if (newVal != null) {
            this.sceneProperty.bind(newVal.sceneProperty());
        } else {
            this.sceneProperty.setValue(null);
        }
    }
}
