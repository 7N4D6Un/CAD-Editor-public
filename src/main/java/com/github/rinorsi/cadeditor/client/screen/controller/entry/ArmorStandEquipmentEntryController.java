package com.github.rinorsi.cadeditor.client.screen.controller.entry;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.Vault;
import com.github.rinorsi.cadeditor.client.context.ItemEditorContext;
import com.github.rinorsi.cadeditor.client.screen.model.entry.ArmorStandEquipmentEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.VaultItemListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.entity.EntityEquipmentEntryView;
import com.github.rinorsi.cadeditor.common.EditorType;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArmorStandEquipmentEntryController extends EntryController<ArmorStandEquipmentEntryModel, EntityEquipmentEntryView> {
    public ArmorStandEquipmentEntryController(ArmorStandEquipmentEntryModel model, EntityEquipmentEntryView view) {
        super(model, view);
    }

    @Override
    public void bind() {
        super.bind();
        view.setListButtonsVisible(false);
        view.disableDeleteButton();
        view.getUpButton().setVisible(false);
        view.getDownButton().setVisible(false);
        view.setDropChanceBoxVisible(false);
        view.getSlotLabel().setLabel(model.getSlot().label());
        view.getItemView().itemProperty().bind(model.itemStackProperty());
        model.itemStackProperty().addListener(stack -> updateItemName());
        updateItemName();
        view.getChooseItemButton().onAction(() -> openItemSelection(null));
        view.getLoadVaultButton().onAction(this::openVaultSelection);
        view.getOpenEditorButton().onAction(() -> openEditor(EditorType.STANDARD));
        view.getOpenSnbtEditorButton().onAction(() -> openEditor(EditorType.SNBT));
        view.getClearButton().onAction(() -> model.setItemStack(ItemStack.EMPTY));
    }

    private void updateItemName() {
        ItemStack stack = model.getItemStack();
        view.getItemNameLabel().setLabel(stack.isEmpty()
                ? Component.literal("-").withStyle(style -> style.withColor(10526880))
                : stack.getHoverName().copy());
        view.getOpenSnbtEditorButton().setDisable(stack.isEmpty());
    }

    private void openEditor(EditorType type) {
        if (!model.getItemStack().isEmpty()) {
            openEditorNow(type);
        } else {
            openItemSelection(() -> openEditorNow(type));
        }
    }

    private void openEditorNow(EditorType type) {
        ItemStack initial = model.getItemStack().copy();
        ItemEditorContext context = new ItemEditorContext(initial, null, false, ctx ->
                model.setItemStack(ctx.getItemStack().copy()));
        ModScreenHandler.openEditor(type, context);
    }

    private void openItemSelection(Runnable afterSelection) {
        ModScreenHandler.openListSelectionScreen(ModTexts.ITEM, "armor_stand_equipment", ClientCache.getItemSelectionItems(), selection -> {
            if (selection == null || selection.isEmpty()) {
                return;
            }
            try {
                Identifier id = Identifier.parse(selection);
                BuiltInRegistries.ITEM.getOptional(id).ifPresent(item -> {
                    model.setItemStack(new ItemStack(item));
                    if (afterSelection != null) {
                        afterSelection.run();
                    }
                });
            } catch (Exception e) {
            }
        });
    }

    private void openVaultSelection() {
        List<VaultItemListSelectionElementModel> elements = new ArrayList<>();
        Map<String, ItemStack> stacksById = new LinkedHashMap<>();
        List<CompoundTag> storedItems = Vault.getInstance().getItems();
        for (int i = 0; i < storedItems.size(); i++) {
            ItemStack stack = ClientUtil.parseItemStack(ClientUtil.registryAccess(), storedItems.get(i));
            if (!stack.isEmpty()) {
                Identifier id = Identifier.fromNamespaceAndPath("cadeditor", "armor_stand_equipment_vault_item_" + i);
                elements.add(new VaultItemListSelectionElementModel(id, stack));
                stacksById.put(id.toString(), stack.copy());
            }
        }
        if (elements.isEmpty()) {
            return;
        }
        ModScreenHandler.openListSelectionScreen(ModTexts.VAULT, "vault_item_armor_stand_equipment", elements, selectedId -> {
            ItemStack chosen = stacksById.get(selectedId);
            if (chosen == null) {
                return;
            }
            model.setItemStack(chosen.copy());
        });
    }
}
