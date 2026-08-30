package com.github.rinorsi.cadeditor.client.screen.controller.entry.vault;

import com.github.franckyi.guapi.api.Guapi;
import com.github.rinorsi.cadeditor.client.ClientContext;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.context.ItemEditorContext;
import com.github.rinorsi.cadeditor.client.logic.ClientVaultActionLogic;
import com.github.rinorsi.cadeditor.client.screen.controller.entry.EntryController;
import com.github.rinorsi.cadeditor.client.screen.model.entry.vault.VaultItemEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.vault.VaultItemEntryView;
import com.github.rinorsi.cadeditor.common.EditorType;
import net.minecraft.client.Minecraft;


public class VaultItemEntryController extends EntryController<VaultItemEntryModel, VaultItemEntryView> {
    public VaultItemEntryController(VaultItemEntryModel model, VaultItemEntryView view) {
        super(model, view);
    }

    
    @Override 
    public void bind() {
        super.bind();
        ((VaultItemEntryView) this.view).getItemView().itemProperty().bind(((VaultItemEntryModel) this.model).itemStackProperty());
        ((VaultItemEntryView) this.view).getLabel().labelProperty().bind(((VaultItemEntryModel) this.model).itemStackProperty().map(value -> value.getHoverName()));
        ((VaultItemEntryView) this.view).getButtonBox().getChildren().remove(((VaultItemEntryView) this.view).getResetButton());
        ((VaultItemEntryView) this.view).getGiveItemButton().setDisable(!ClientContext.vaultGiveEnabled());
        ((VaultItemEntryView) this.view).getGiveItemButton().onAction(() -> {
            ClientVaultActionLogic.giveToSelectedHotbar(((VaultItemEntryModel) this.model).getItemStack());
            if (Minecraft.getInstance().player.isCreative() || ClientContext.isModInstalledOnServer()) {
                Guapi.getScreenHandler().hideScene();
            }
        });
        ((VaultItemEntryView) this.view).getOpenEditorButton().onAction(() -> {
            openEditor(EditorType.STANDARD);
        });
        ((VaultItemEntryView) this.view).getOpenNBTEditorButton().onAction(() -> {
            openEditor(EditorType.NBT);
        });
        ((VaultItemEntryView) this.view).getOpenSNBTEditorButton().onAction(() -> {
            openEditor(EditorType.SNBT);
        });
    }

    
    private void openEditor(EditorType editorType) {
        ModScreenHandler.openEditor(editorType, new ItemEditorContext(((VaultItemEntryModel) this.model).getItemStack(), null, false, context -> {
            ((VaultItemEntryModel) this.model).setItemStack(context.getItemStack());
        }));
    }
}
