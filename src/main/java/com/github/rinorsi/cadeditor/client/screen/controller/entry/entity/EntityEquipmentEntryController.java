package com.github.rinorsi.cadeditor.client.screen.controller.entry.entity;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.Vault;
import com.github.rinorsi.cadeditor.client.context.ItemEditorContext;
import com.github.rinorsi.cadeditor.client.screen.controller.entry.EntryController;
import com.github.rinorsi.cadeditor.client.screen.model.category.entity.EntityEquipmentCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.entity.EntityEquipmentEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.VaultItemListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.entity.EntityEquipmentEntryView;
import com.github.rinorsi.cadeditor.common.EditorType;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;


public class EntityEquipmentEntryController extends EntryController<EntityEquipmentEntryModel, EntityEquipmentEntryView> {
    public EntityEquipmentEntryController(EntityEquipmentEntryModel model, EntityEquipmentEntryView view) {
        super(model, view);
    }

    
    @Override 
    public void bind() {
        super.bind();
        ((EntityEquipmentEntryView) this.view).setListButtonsVisible(false);
        ((EntityEquipmentEntryView) this.view).disableDeleteButton();
        ((EntityEquipmentEntryView) this.view).getUpButton().setVisible(false);
        ((EntityEquipmentEntryView) this.view).getDownButton().setVisible(false);
        ((EntityEquipmentEntryView) this.view).getChooseItemButton().onAction(() -> {
            openItemSelection(null);
        });
        ((EntityEquipmentEntryView) this.view).getLoadVaultButton().onAction(this::openVaultSelection);
        ((EntityEquipmentEntryView) this.view).getChooseItemButton().setDisable(false);
        ((EntityEquipmentEntryView) this.view).getSlotLabel().setLabel(((EntityEquipmentEntryModel) this.model).getSlotLabel());
        ((EntityEquipmentEntryView) this.view).getItemView().itemProperty().bind(((EntityEquipmentEntryModel) this.model).itemStackProperty());
        ((EntityEquipmentEntryModel) this.model).itemStackProperty().addListener(stack -> {
            updateItemName();
        });
        updateItemName();
        ((EntityEquipmentEntryView) this.view).getDropChanceField().setValidator(((EntityEquipmentEntryModel) this.model)::isDropChanceTextValid);
        ((EntityEquipmentEntryView) this.view).getDropChanceField().setText(((EntityEquipmentEntryModel) this.model).formatDropChance());
        ((EntityEquipmentEntryView) this.view).getDropChanceField().textProperty().addListener(value -> {
            if (!((EntityEquipmentEntryView) this.view).getDropChanceField().isValid()) {
                ((EntityEquipmentEntryModel) this.model).setValid(false);
            } else if (!Objects.equals(value, ((EntityEquipmentEntryModel) this.model).formatDropChance())) {
                ((EntityEquipmentEntryModel) this.model).setDropChanceFromText(value);
            }
        });
        ((EntityEquipmentEntryModel) this.model).dropChanceProperty().addListener(newValue -> {
            String formatted = ((EntityEquipmentEntryModel) this.model).formatDropChance();
            if (!Objects.equals(formatted, ((EntityEquipmentEntryView) this.view).getDropChanceField().getText())) {
                ((EntityEquipmentEntryView) this.view).getDropChanceField().setText(formatted);
            }
        });
        ((EntityEquipmentEntryView) this.view).getDropChanceField().validProperty().addListener(((EntityEquipmentEntryModel) this.model)::setValid);
        ((EntityEquipmentEntryView) this.view).getDropChanceField().onKeyPress(event -> {
            if (event.isConsumed()) {
                return;
            }
            int key = event.getKeyCode();
            if (key != 265 && key != 264) {
                return;
            }
            String text = ((EntityEquipmentEntryView) this.view).getDropChanceField().getText();
            if (!((EntityEquipmentEntryModel) this.model).isDropChanceTextValid(text)) {
                return;
            }
            float current = Float.parseFloat(text);
            float step = 0.05f;
            if (event.isShiftKeyDown()) {
                step = 0.1f;
            } else if (event.isControlKeyDown()) {
                step = 0.01f;
            }
            if (key == 264) {
                step = -step;
            }
            float next = Math.max(0.0f, Math.min(EntityEquipmentCategoryModel.MAX_DROP_CHANCE, current + step));
            if (Math.abs(next - current) < 1.0E-4f) {
                return;
            }
            ((EntityEquipmentEntryModel) this.model).setDropChance(next);
            event.consume();
        });
        ((EntityEquipmentEntryView) this.view).getOpenEditorButton().onAction(() -> {
            openEditor(EditorType.STANDARD);
        });
        ((EntityEquipmentEntryView) this.view).getOpenSnbtEditorButton().onAction(() -> {
            openEditor(EditorType.SNBT);
        });
        ((EntityEquipmentEntryView) this.view).getClearButton().onAction(() -> {
            ((EntityEquipmentEntryModel) this.model).setItemStack(ItemStack.EMPTY);
        });
    }

    
    private void updateItemName() {
        ItemStack stack = ((EntityEquipmentEntryModel) this.model).getItemStack();
        ((EntityEquipmentEntryView) this.view).getItemNameLabel().setLabel(stack.isEmpty() ? Component.literal("-").withStyle(style -> style.withColor(10526880)) : stack.getHoverName().copy());
        boolean empty = stack.isEmpty();
        ((EntityEquipmentEntryView) this.view).getOpenSnbtEditorButton().setDisable(empty);
    }

    private void openEditor(EditorType type) {
        ensureItemStack(() -> {
            openEditorNow(type);
        });
    }

    
    private void openEditorNow(EditorType type) {
        ItemStack initial = ((EntityEquipmentEntryModel) this.model).getItemStack().copy();
        ItemEditorContext context = new ItemEditorContext(initial, null, false, ctx -> {
            ItemStack result = ctx.getItemStack().copy();
            ((EntityEquipmentEntryModel) this.model).setItemStack(result);
        });
        ModScreenHandler.openEditor(type, context);
    }

    
    private void ensureItemStack(Runnable onReady) {
        if (!((EntityEquipmentEntryModel) this.model).getItemStack().isEmpty()) {
            onReady.run();
        } else {
            openItemSelection(onReady);
        }
    }

    private void openItemSelection(Runnable afterSelection) {
        ModScreenHandler.openListSelectionScreen(ModTexts.ITEM, "equipment", ClientCache.getItemSelectionItems(), selection -> {
            if (selection == null || selection.isEmpty()) {
                return;
            }
            try {
                Identifier id = Identifier.parse(selection);
                BuiltInRegistries.ITEM.getOptional(id).ifPresent(item -> {
                    ((EntityEquipmentEntryModel) this.model).setItemStack(new ItemStack(item));
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
                Identifier id = Identifier.fromNamespaceAndPath("cadeditor", "equipment_vault_item_" + i);
                elements.add(new VaultItemListSelectionElementModel(id, stack));
                stacksById.put(id.toString(), stack.copy());
            }
        }
        if (elements.isEmpty()) {
            return;
        }
        ModScreenHandler.openListSelectionScreen(ModTexts.VAULT, "vault_item_equipment", elements, selectedId -> {
            ItemStack chosen = (ItemStack) stacksById.get(selectedId);
            if (chosen == null) {
                return;
            }
            ((EntityEquipmentEntryModel) this.model).setItemStack(chosen.copy());
        });
    }
}
