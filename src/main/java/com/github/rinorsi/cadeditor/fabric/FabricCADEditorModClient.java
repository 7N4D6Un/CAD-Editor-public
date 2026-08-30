package com.github.rinorsi.cadeditor.fabric;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientContext;
import com.github.rinorsi.cadeditor.client.ClientEventHandler;
import com.github.rinorsi.cadeditor.client.ClientInit;
import com.github.rinorsi.cadeditor.client.KeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;


public final class FabricCADEditorModClient implements ClientModInitializer {
    public void onInitializeClient() {
        PlatformUtilImpl.registerClientReceivers();
        ClientInit.init();
        ClientInit.setup();
        KeyMappingHelper.registerKeyMapping(KeyBindings.getEditorKey());
        KeyMappingHelper.registerKeyMapping(KeyBindings.getNBTEditorKey());
        KeyMappingHelper.registerKeyMapping(KeyBindings.getSNBTEditorKey());
        KeyMappingHelper.registerKeyMapping(KeyBindings.getVaultKey());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.gui.screen() == null) {
                ClientEventHandler.onKeyInput();
            }
        });
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            ClientCache.invalidate();
            ClientContext.setModInstalledOnServer(false);
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientCache.invalidate();
            ClientContext.setModInstalledOnServer(false);
        });
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            registerContainerScreenKeyHandler(screen);
        });
    }

    private void registerContainerScreenKeyHandler(Screen screen) {
        if (screen instanceof AbstractContainerScreen) {
            AbstractContainerScreen<?> container = (AbstractContainerScreen) screen;
            ScreenKeyboardEvents.allowKeyPress(screen).register((current, keyEvent) -> {
                return !ClientEventHandler.onScreenEvent(container, keyEvent.key(), keyEvent.scancode(), keyEvent.modifiers());
            });
        }
    }
}
