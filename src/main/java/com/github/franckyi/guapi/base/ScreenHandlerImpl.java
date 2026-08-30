package com.github.franckyi.guapi.base;

import com.github.franckyi.databindings.api.IntegerProperty;
import com.github.franckyi.databindings.api.ObjectProperty;
import com.github.franckyi.guapi.api.Guapi;
import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.ScreenHandler;
import com.github.franckyi.guapi.api.event.ScreenEvent;
import com.github.franckyi.guapi.api.node.Scene;
import com.github.franckyi.guapi.api.util.ScreenEventType;
import com.github.franckyi.guapi.base.event.KeyEventImpl;
import com.github.franckyi.guapi.base.event.MouseButtonEventImpl;
import com.github.franckyi.guapi.base.event.MouseDragEventImpl;
import com.github.franckyi.guapi.base.event.MouseEventImpl;
import com.github.franckyi.guapi.base.event.MouseScrollEventImpl;
import com.github.franckyi.guapi.base.event.TypeEventImpl;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.NotNull;

public final class ScreenHandlerImpl implements ScreenHandler {
    public static final ScreenHandler INSTANCE = new ScreenHandlerImpl();
    private final Deque<Scene> scenes = new ArrayDeque<>();
    private final ObjectProperty<Scene> currentSceneProperty = ObjectProperty.create();
    private final IntegerProperty widthProperty = IntegerProperty.create();
    private final IntegerProperty heightProperty = IntegerProperty.create();
    private Screen screen;
    private Screen oldScreen;

    private ScreenHandlerImpl() {
        currentSceneProperty().addListener((oldVal, newVal) -> {
            if (newVal == null) {
                if (oldVal != null) {
                    oldVal.widthProperty().unbind();
                    oldVal.heightProperty().unbind();
                    oldVal.hide();
                }
                closeScreen();
                return;
            }
            if (oldVal == null) {
                openScreen();
            } else {
                oldVal.hide();
            }
            newVal.widthProperty().unbind();
            newVal.widthProperty().bind(this.widthProperty);
            newVal.heightProperty().unbind();
            newVal.heightProperty().bind(this.heightProperty);
            newVal.show();
        });
    }

    @Override
    public void showScene(Scene scene) {
        checkScreen();
        this.scenes.push(scene);
        setCurrentScene(scene);
    }

    @Override
    public void replaceScene(Scene scene) {
        checkScreen();
        if (!this.scenes.isEmpty()) {
            this.scenes.pop();
        }
        showScene(scene);
    }

    @Override
    public void hideScene() {
        if (!this.scenes.isEmpty()) {
            this.scenes.pop();
            setCurrentScene(this.scenes.peek());
        }
    }

    @Override
    public Screen getGuapiScreen() {
        return getOrCreateScreen();
    }

    private void checkScreen() {
        Screen currentScreen = this.screen;
        if (currentScreen == null || Minecraft.getInstance().gui.screen() != currentScreen) {
            this.scenes.clear();
            setCurrentScene(null);
        }
    }

    private Scene getCurrentScene() {
        return currentSceneProperty().getValue();
    }

    private ObjectProperty<Scene> currentSceneProperty() {
        return this.currentSceneProperty;
    }

    private void setCurrentScene(Scene value) {
        currentSceneProperty().setValue(value);
    }

    private void openScreen() {
        Screen current = Minecraft.getInstance().gui.screen();
        this.oldScreen = current instanceof GenericMessageScreen ? null : current;
        Minecraft.getInstance().gui.setScreen(getOrCreateScreen());
    }

    private void closeScreen() {
        if (Minecraft.getInstance().gui.screen() != this.screen) {
            return;
        }
        if (this.oldScreen != null) {
            Minecraft.getInstance().gui.setScreen(this.oldScreen);
        } else {
            Minecraft.getInstance().gui.setScreen(null);
        }
        this.oldScreen = null;
    }

    private Screen getOrCreateScreen() {
        if (this.screen == null) {
            this.screen = new GuapiScreen();
        }
        return this.screen;
    }

    private void render(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        try {
            getCurrentScene().render(guiGraphicsExtractor, mouseX, mouseY, delta);
        } catch (Exception e) {
            Guapi.getExceptionHandler().handleRenderException(e, guiGraphicsExtractor, mouseX, mouseY, delta, getCurrentScene());
            Guapi.getDefaultLogger().error(Guapi.LOG_MARKER, "Error while rendering GUAPI Scene", e);
            hideScene();
        }
    }

    private void tick() {
        if (currentSceneProperty().hasValue()) {
            try {
                getCurrentScene().tick();
            } catch (Exception e) {
                Guapi.getExceptionHandler().handleTickException(e, getCurrentScene());
                Guapi.getDefaultLogger().error(Guapi.LOG_MARKER, "Error while ticking GUAPI Scene", e);
                hideScene();
            }
        }
    }

    private void updateSize(int width, int height) {
        this.widthProperty.setValue(width);
        this.heightProperty.setValue(height);
    }

    private boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        return handleEvent(ScreenEventType.MOUSE_CLICKED, new MouseButtonEventImpl(event.x(), event.y(), event.button()));
    }

    private boolean mouseReleased(MouseButtonEvent event) {
        return handleEvent(ScreenEventType.MOUSE_RELEASED, new MouseButtonEventImpl(event.x(), event.y(), event.button()));
    }

    private boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        return handleEvent(ScreenEventType.MOUSE_DRAGGED, new MouseDragEventImpl(event.x(), event.y(), event.button(), deltaX, deltaY));
    }

    private boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        return handleEvent(ScreenEventType.MOUSE_SCOLLED, new MouseScrollEventImpl(mouseX, mouseY, deltaX, deltaY));
    }

    private boolean keyPressed(KeyEvent event) {
        return handleEvent(ScreenEventType.KEY_PRESSED, new KeyEventImpl(event.key(), event.scancode(), event.modifiers()));
    }

    private boolean keyReleased(KeyEvent event) {
        return handleEvent(ScreenEventType.KEY_RELEASED, new KeyEventImpl(event.key(), event.scancode(), event.modifiers()));
    }

    private boolean charTyped(CharacterEvent event) {
        return handleEvent(ScreenEventType.CHAR_TYPED, new TypeEventImpl((char) event.codepoint(), 0));
    }

    private void mouseMoved(double mouseX, double mouseY) {
        handleEvent(ScreenEventType.MOUSE_MOVED, new MouseEventImpl(mouseX, mouseY));
    }

    private <E extends ScreenEvent> boolean handleEvent(ScreenEventType<E> type, E event) {
        if (!currentSceneProperty().hasValue()) {
            return false;
        }
        try {
            getCurrentScene().handleEvent(type, event);
        } catch (Exception e) {
            Guapi.getExceptionHandler().handleEventException(e, type, event, getCurrentScene());
            Guapi.getDefaultLogger().error(Guapi.LOG_MARKER, "Error while handling " + type.getName() + " event on GUAPI Scene", e);
            hideScene();
        }
        return event.isConsumed();
    }

    private final class GuapiScreen extends Screen {
        private GuapiScreen() {
            super(GuapiHelper.EMPTY_TEXT);
        }

        public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTicks) {
            if (ScreenHandlerImpl.this.currentSceneProperty().hasValue()) {
                if (ScreenHandlerImpl.this.getCurrentScene().isTexturedBackground()) {
                    extractTransparentBackground(guiGraphicsExtractor);
                } else {
                    extractBackground(guiGraphicsExtractor, mouseX, mouseY, partialTicks);
                }
                ScreenHandlerImpl.this.render(guiGraphicsExtractor, mouseX, mouseY, partialTicks);
            }
        }

        public void tick() {
            ScreenHandlerImpl.this.tick();
        }

        protected void init() {
            ScreenHandlerImpl.this.updateSize(this.width, this.height);
        }

        public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
            return ScreenHandlerImpl.this.mouseClicked(event, isDoubleClick);
        }

        public boolean mouseReleased(MouseButtonEvent event) {
            return ScreenHandlerImpl.this.mouseReleased(event);
        }

        public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
            return ScreenHandlerImpl.this.mouseDragged(event, deltaX, deltaY);
        }

        public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
            return ScreenHandlerImpl.this.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
        }

        public boolean keyPressed(KeyEvent event) {
            return ScreenHandlerImpl.this.keyPressed(event);
        }

        public boolean keyReleased(KeyEvent event) {
            return ScreenHandlerImpl.this.keyReleased(event);
        }

        public boolean charTyped(CharacterEvent event) {
            return ScreenHandlerImpl.this.charTyped(event);
        }

        public void mouseMoved(double mouseX, double mouseY) {
            ScreenHandlerImpl.this.mouseMoved(mouseX, mouseY);
        }
    }
}
