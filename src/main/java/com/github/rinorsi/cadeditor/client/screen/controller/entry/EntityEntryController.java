package com.github.rinorsi.cadeditor.client.screen.controller.entry;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.Vault;
import com.github.rinorsi.cadeditor.client.context.EntityEditorContext;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntityEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.EntityListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.VaultEntityListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.EntityEntryView;
import com.github.rinorsi.cadeditor.common.EditorType;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;


public class EntityEntryController extends ValueEntryController<EntityEntryModel, EntityEntryView> {
    private static final MutableComponent EMPTY_ENTITY_LABEL = GuapiHelper.text("-").withStyle(ChatFormatting.DARK_GRAY);

    public EntityEntryController(EntityEntryModel model, EntityEntryView view) {
        super(model, view);
    }

    
    @Override 
    public void bind() {
        super.bind();
        ((EntityEntryView) this.view).getEntityField().setPlaceholder(ModTexts.ENTITY);
        ((EntityEntryView) this.view).getEntityField().setValidator(value -> value != null && !value.isEmpty() && ClientUtil.parseResourceLocation(value) != null);
        ((EntityEntryView) this.view).getEntityField().getSuggestions().setAll(ClientCache.getEntitySuggestions());
        ((EntityEntryView) this.view).getEntityField().textProperty().addListener(this::onEntityChanged);
        ((EntityEntryModel) this.model).entityIdProperty().addListener(newValue -> {
            String text = ((EntityEntryView) this.view).getEntityField().getText();
            if (!Objects.equals(text, newValue)) {
                ((EntityEntryView) this.view).getEntityField().setText(newValue);
            }
        });
        ((EntityEntryModel) this.model).entityTypeProperty().addListener(type -> {
            updateEntityPreview();
        });
        String initial = ((EntityEntryModel) this.model).getEntityId();
        ((EntityEntryView) this.view).getEntityField().setText(initial == null ? "" : initial);
        ((EntityEntryView) this.view).getEntityField().validProperty().addListener(valid -> {
            if (!valid.booleanValue()) {
                ((EntityEntryModel) this.model).setValid(false);
                ((EntityEntryView) this.view).getEntityField().getTooltip().setAll(ModTexts.Messages.errorNoTargetFound(ModTexts.ENTITY));
            } else {
                ((EntityEntryView) this.view).getEntityField().getTooltip().clear();
            }
            updateEntityPreview();
        });
        ((EntityEntryView) this.view).getSelectEntityButton().getTooltip().add(ModTexts.choose(ModTexts.ENTITY));
        ((EntityEntryView) this.view).getSelectEntityButton().onAction(this::openSelectionScreen);
        ((EntityEntryView) this.view).getPasteFromVaultButton().onAction(this::openVaultSelection);
        ((EntityEntryView) this.view).getOpenEditorButton().onAction(() -> {
            openEditor(EditorType.STANDARD);
        });
        ((EntityEntryView) this.view).getOpenNbtEditorButton().onAction(() -> {
            openEditor(EditorType.NBT);
        });
        ((EntityEntryView) this.view).getOpenSnbtEditorButton().onAction(() -> {
            openEditor(EditorType.SNBT);
        });
        updateEntityPreview();
    }

    
    private void onEntityChanged(String value) {
        if (((EntityEntryView) this.view).getEntityField().isValid()) {
            ((EntityEntryModel) this.model).setEntityId(value);
        } else {
            ((EntityEntryModel) this.model).setValid(false);
        }
    }

    
    private void updateEntityPreview() {
        EntityType<?> entityType = ((EntityEntryModel) this.model).getEntityType();
        if (entityType != null) {
            ((EntityEntryView) this.view).getEntityNameLabel().setLabel(entityType.getDescription().copy().withStyle(ChatFormatting.GRAY));
            ((EntityEntryView) this.view).getEntityIconView().setItem(createIcon(entityType));
        } else {
            ((EntityEntryView) this.view).getEntityNameLabel().setLabel(EMPTY_ENTITY_LABEL);
            ((EntityEntryView) this.view).getEntityIconView().setItem(ItemStack.EMPTY);
        }
    }

    private ItemStack createIcon(EntityType<?> type) {
        return (ItemStack) SpawnEggItem.byId(type).map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    
    private void openSelectionScreen() {
        String current = ((EntityEntryView) this.view).getEntityField().getText();
        Identifier location = ClientUtil.parseResourceLocation(current);
        String normalized = location != null ? location.toString() : current;
        MutableComponent entityLabel = ModTexts.ENTITY;
        List<EntityListSelectionElementModel> entitySelectionItems = ClientCache.getEntitySelectionItems();
        ModScreenHandler.openListSelectionScreen(entityLabel, normalized, entitySelectionItems, ((EntityEntryModel) this.model)::setEntityId);
    }

    
    private void openEditor(EditorType editorType) {
        ModScreenHandler.openEditor(editorType, new EntityEditorContext(((EntityEntryModel) this.model).copyValue(), null, false, context -> {
            ((EntityEntryModel) this.model).setValue(context.getTag());
        }));
    }

    private void openVaultSelection() {
        List<VaultEntityListSelectionElementModel> elements = new ArrayList<>();
        Map<String, CompoundTag> entitiesById = new LinkedHashMap<>();
        List<CompoundTag> storedEntities = Vault.getInstance().getEntities();
        for (int i = 0; i < storedEntities.size(); i++) {
            CompoundTag tag = storedEntities.get(i);
            if (tag != null && !tag.isEmpty()) {
                Identifier id = Identifier.fromNamespaceAndPath("cadeditor", "entity_entry_vault_" + i);
                elements.add(new VaultEntityListSelectionElementModel(id, tag));
                entitiesById.put(id.toString(), tag.copy());
            }
        }
        if (elements.isEmpty()) {
            return;
        }
        ModScreenHandler.openListSelectionScreen(ModTexts.VAULT, "vault_entity_entry", elements, selectedId -> {
            CompoundTag chosen = (CompoundTag) entitiesById.get(selectedId);
            if (chosen == null) {
                return;
            }
            ((EntityEntryModel) this.model).setValue(chosen.copy());
        });
    }
}
