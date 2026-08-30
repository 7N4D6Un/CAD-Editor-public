package com.github.rinorsi.cadeditor.fabric;

import com.github.franckyi.guapi.api.Guapi;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;


public final class ModMenuIntegration implements ModMenuApi {
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ModScreenHandler.openSettingsScreen();
            return Guapi.getScreenHandler().getGuapiScreen();
        };
    }
}
