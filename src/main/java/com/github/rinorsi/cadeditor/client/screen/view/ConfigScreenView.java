package com.github.rinorsi.cadeditor.client.screen.view;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.Node;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.network.chat.Component;


public class ConfigScreenView extends CategoryEntryScreenView {
    @Override 
    public void build() {
        super.build();
        getCancelButton().setLabel(ModTexts.CLOSE);
        getCancelButton().setPrefWidth(150);
        getDoneButton().setLabel(ModTexts.SAVE);
        getDoneButton().setPrefWidth(150);
    }

    
    @Override 
    protected Node createHeader() {
        return GuapiHelper.label((Component) ModTexts.title(ModTexts.SETTINGS.copy())).textAlign(GuapiHelper.CENTER).prefHeight(20);
    }
}
