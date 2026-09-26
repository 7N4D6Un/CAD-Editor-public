package com.github.rinorsi.cadeditor.client.screen.controller;

import com.github.franckyi.guapi.api.Color;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.Vault;
import com.github.rinorsi.cadeditor.client.screen.model.EntityEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.StandardEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.TextFormatDialogModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.ColorSelectionScreenModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.StringSuggestionListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.VaultEntityListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.VaultItemListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.view.StandardEditorView;
import com.github.rinorsi.cadeditor.client.util.texteditor.TextEditorActionHandler;
import com.github.rinorsi.cadeditor.client.util.texteditor.TextTokens;
import com.github.rinorsi.cadeditor.common.EditorType;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Consumer;

public class StandardEditorController extends CategoryEntryScreenController<StandardEditorModel, StandardEditorView> implements EditorController<StandardEditorModel, StandardEditorView> {
    public StandardEditorController(StandardEditorModel model, StandardEditorView view) {
        super(model, view);
    }

    @Override
    public void bind() {
        super.bind();
        EditorController.super.bind();
        view.addOpenNBTEditorButton(() -> model.changeEditor(EditorType.NBT));
        view.addOpenSNBTEditorButton(() -> model.changeEditor(EditorType.SNBT));
        if (model.getContext().canSaveToVault()) {
            if (model instanceof ItemEditorModel itemModel) {
                view.addLoadVaultButton(() -> openItemVaultSelection(itemModel));
            } else if (model instanceof EntityEditorModel entityModel) {
                view.addLoadVaultButton(() -> openEntityVaultSelection(entityModel));
            }
        }
        if (model.getContext().getTag() == null) {
            view.getOpenNBTEditorButton().setDisable(true);
            view.getOpenSNBTEditorButton().setDisable(true);
        }
        view.getHeaderLabel().setLabel(model.getContext().getTargetName());
        view.getTextEditorButtons().visibleProperty().bind(model.activeTextEditorProperty().notNull());
        view.setTextEditorSupplier(model::getActiveTextEditor);
        model.activeTextEditorProperty().addListener(editor -> view.updateTextEditorToolbar(editor));
        view.updateTextEditorToolbar(model.getActiveTextEditor());
        view.getChooseCustomColorButton().onAction(e -> {
            e.consume();
            if (model.getActiveTextEditor() != null
                    && model.getActiveTextEditor().supportsColorFormatting()
                    && model.getActiveTextEditor().supportsCustomColorPicker()) {
                ModScreenHandler.openColorSelectionScreen(ColorSelectionScreenModel.Target.TEXT, Color.fromHex(model.getTextEditorCustomColor()), this::updateCustomColor);
            }
        });
        model.textEditorCustomColor().addListener(value -> {
            view.getCustomColorButton().setBackgroundColor(Color.fromHex(value));
            boolean show = value != null
                    && model.getActiveTextEditor() != null
                    && model.getActiveTextEditor().supportsColorFormatting()
                    && model.getActiveTextEditor().supportsCustomColorPicker();
            view.getCustomColorButton().setVisible(show);
        });
        view.getCustomColorButton().onAction(e -> {
            e.consume();
            model.getActiveTextEditor().addColorFormatting(model.getTextEditorCustomColor());
        });
        view.getHeadButton().onAction(e -> {
            e.consume();
            openHeadDialog();
        });
        view.getSpriteButton().onAction(e -> {
            e.consume();
            openSpriteDialog();
        });
        view.getTranslationButton().onAction(e -> {
            e.consume();
            openTranslationDialog();
        });
        view.getFontButton().onAction(e -> {
            e.consume();
            openFontSelection();
        });
        view.getGradientButton().onAction(e -> {
            e.consume();
            openGradientDialog();
        });
        view.getShadowColorButton().onAction(e -> {
            e.consume();
            openShadowColorDialog();
        });
    }

    private void openHeadDialog() {
        openTextFormatDialog(ModTexts.TEXT_FORMAT_HEAD_TITLE, List.of(
                TextFormatDialogModel.DialogField.text("player_name", ModTexts.DIALOG_PLAYER_NAME, "", List.of(
                        new TextFormatDialogModel.DialogAction(ModTexts.DIALOG_FILL_OWN_DATA, field -> {
                            LocalPlayer player = Minecraft.getInstance().player;
                            if (player != null) {
                                field.setText(player.getName().getString());
                            }
                        }))),
                TextFormatDialogModel.DialogField.text("player_uuid", ModTexts.DIALOG_UUID, "", List.of(
                        new TextFormatDialogModel.DialogAction(ModTexts.DIALOG_FILL_OWN_DATA, field -> {
                            LocalPlayer player = Minecraft.getInstance().player;
                            if (player != null) {
                                field.setText(player.getUUID().toString());
                            }
                        }))),
                TextFormatDialogModel.DialogField.text("texture_value", ModTexts.DIALOG_TEXTURE_VALUE, ""),
                TextFormatDialogModel.DialogField.text("texture_signature", ModTexts.DIALOG_TEXTURE_SIGNATURE, ""),
                TextFormatDialogModel.DialogField.checkbox("include_hat", ModTexts.DIALOG_INCLUDE_HAT, true)
        ), values -> {
            String textureValue = values.get("texture_value");
            String playerName = values.get("player_name");
            String playerUuid = values.get("player_uuid");
            boolean hat = Boolean.parseBoolean(values.get("include_hat"));
            TextEditorActionHandler editor = model.getActiveTextEditor();
            if (editor == null) {
                return;
            }
            if (textureValue != null && !textureValue.isBlank()) {
                String signature = values.get("texture_signature");
                editor.insertToken(TextTokens.buildHeadTextureToken(textureValue.trim(), signature == null ? null : signature.trim(), hat));
                return;
            }
            if (playerUuid != null && !playerUuid.isBlank()) {
                try {
                    UUID.fromString(playerUuid.trim());
                } catch (IllegalArgumentException e) {
                    ClientUtil.showMessage(ModTexts.TEXT_FORMAT_INVALID);
                    return;
                }
                editor.insertToken(TextTokens.buildHeadUuidToken(playerUuid.trim(), hat));
                return;
            }
            if (playerName != null && !playerName.isBlank()) {
                editor.insertToken(TextTokens.buildHeadToken(playerName.trim(), hat));
                return;
            }
            ClientUtil.showMessage(ModTexts.TEXT_FORMAT_INVALID);
        });
    }

    private void openSpriteDialog() {
        openTextFormatDialog(ModTexts.TEXT_FORMAT_SPRITE_TITLE, List.of(
                TextFormatDialogModel.DialogField.text("atlas_id", ModTexts.DIALOG_ATLAS_ID, "minecraft:blocks"),
                TextFormatDialogModel.DialogField.text("sprite_id", ModTexts.DIALOG_SPRITE_ID, "minecraft:block/stone")
        ), values -> {
            String atlas = values.get("atlas_id");
            String sprite = values.get("sprite_id");
            TextEditorActionHandler editor = model.getActiveTextEditor();
            if (editor == null) {
                return;
            }
            if (atlas == null || atlas.isBlank() || sprite == null || sprite.isBlank()
                    || Identifier.tryParse(atlas.trim()) == null || Identifier.tryParse(sprite.trim()) == null) {
                ClientUtil.showMessage(ModTexts.TEXT_FORMAT_INVALID);
                return;
            }
            editor.insertToken(TextTokens.buildSpriteToken(atlas.trim(), sprite.trim()));
        });
    }

    private void openTranslationDialog() {
        openTextFormatDialog(ModTexts.TEXT_FORMAT_TRANSLATION_TITLE, List.of(
                TextFormatDialogModel.DialogField.text("key", ModTexts.DIALOG_TRANSLATION_KEY, ""),
                TextFormatDialogModel.DialogField.text("fallback", ModTexts.DIALOG_FALLBACK_TEXT, "")
        ), values -> {
            String key = values.get("key");
            String fallback = values.get("fallback");
            TextEditorActionHandler editor = model.getActiveTextEditor();
            if (editor == null) {
                return;
            }
            if (key == null || key.isBlank()) {
                ClientUtil.showMessage(ModTexts.TEXT_FORMAT_INVALID);
                return;
            }
            editor.insertToken(TextTokens.buildTranslationToken(key.trim(), fallback == null || fallback.isBlank() ? null : fallback));
        });
    }

    private void openGradientDialog() {
        openTextFormatDialog(ModTexts.TEXT_FORMAT_GRADIENT_TITLE, List.of(
                TextFormatDialogModel.DialogField.color("start_color", ModTexts.DIALOG_GRADIENT_START, ""),
                TextFormatDialogModel.DialogField.color("middle_color", ModTexts.DIALOG_GRADIENT_MIDDLE, ""),
                TextFormatDialogModel.DialogField.color("end_color", ModTexts.DIALOG_GRADIENT_END, ""),
                TextFormatDialogModel.DialogField.checkbox("shadow", ModTexts.DIALOG_APPLY_TO_SHADOW, false)
        ), values -> {
            Integer start = parseHexColor(values.get("start_color"));
            Integer middle = parseHexColor(values.get("middle_color"));
            Integer end = parseHexColor(values.get("end_color"));
            boolean shadow = Boolean.parseBoolean(values.get("shadow"));
            TextEditorActionHandler editor = model.getActiveTextEditor();
            if (editor == null) {
                return;
            }
            if (start == null || end == null) {
                ClientUtil.showMessage(ModTexts.TEXT_FORMAT_INVALID);
                return;
            }
            List<Integer> stops = new ArrayList<>();
            stops.add(start);
            if (middle != null) {
                stops.add(middle);
            }
            stops.add(end);
            editor.applyGradient(stops, shadow);
        });
    }

    private void openShadowColorDialog() {
        openTextFormatDialog(ModTexts.TEXT_FORMAT_SHADOW_COLOR_TITLE, List.of(
                TextFormatDialogModel.DialogField.color("shadow_color", ModTexts.DIALOG_SHADOW_COLOR, "")
        ), values -> {
            Integer color = parseHexColor(values.get("shadow_color"));
            TextEditorActionHandler editor = model.getActiveTextEditor();
            if (editor == null) {
                return;
            }
            if (color == null) {
                ClientUtil.showMessage(ModTexts.TEXT_FORMAT_INVALID);
                return;
            }
            editor.addShadowColorFormatting((color & 0xFFFFFF) | 0xFF000000);
        });
    }

    private void openFontSelection() {
        List<ListSelectionElementModel> items = new ArrayList<>();
        FileToIdConverter converter = FileToIdConverter.json("font");
        TreeSet<String> fonts = new TreeSet<>();
        converter.listMatchingResources(Minecraft.getInstance().getResourceManager()).keySet().stream()
                .map(converter::fileToId)
                .filter(id -> !id.getNamespace().equals("minecraft") || !id.getPath().startsWith("include/"))
                .map(Identifier::toString)
                .forEach(fonts::add);
        fonts.add("minecraft:default");
        fonts.forEach(font -> items.add(new StringSuggestionListSelectionElementModel(font)));
        ModScreenHandler.openListSelectionScreen(ModTexts.TEXT_FORMAT_FONT_TITLE, null, items, font -> {
            TextEditorActionHandler editor = model.getActiveTextEditor();
            if (editor != null) {
                editor.addFontFormatting(font);
            }
        });
    }

    private void openTextFormatDialog(MutableComponent title, List<TextFormatDialogModel.DialogField> fields, Consumer<Map<String, String>> onApply) {
        ModScreenHandler.openTextFormatDialog(new TextFormatDialogModel(title, fields, onApply));
    }

    private static Integer parseHexColor(String value) {
        if (value == null || value.length() != 7 || value.charAt(0) != '#') {
            return null;
        }
        try {
            return (int) (Long.parseLong(value.substring(1), 16) & 0xFFFFFF);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void updateCustomColor(String hex) {
        if (model.getActiveTextEditor() == null
                || !model.getActiveTextEditor().supportsColorFormatting()
                || !model.getActiveTextEditor().supportsCustomColorPicker()) {
            return;
        }
        model.setTextEditorCustomColor(hex);
        model.getActiveTextEditor().addColorFormatting(hex);
    }

    private void openItemVaultSelection(ItemEditorModel itemModel) {
        List<VaultItemListSelectionElementModel> elements = new ArrayList<>();
        Map<String, ItemStack> stacksById = new LinkedHashMap<>();
        List<CompoundTag> storedItems = Vault.getInstance().getItems();
        for (int i = 0; i < storedItems.size(); i++) {
            ItemStack stack = ClientUtil.parseItemStack(ClientUtil.registryAccess(), storedItems.get(i));
            if (stack.isEmpty()) {
                continue;
            }
            Identifier id = Identifier.fromNamespaceAndPath("cadeditor", "editor_vault_item_" + i);
            elements.add(new VaultItemListSelectionElementModel(id, stack));
            stacksById.put(id.toString(), stack.copy());
        }
        if (elements.isEmpty()) {
            return;
        }
        ModScreenHandler.openListSelectionScreen(ModTexts.VAULT, "vault_item_editor", elements, selectedId -> {
            ItemStack chosen = stacksById.get(selectedId);
            if (chosen == null) {
                return;
            }
            itemModel.handleStackReplaced(chosen.copy());
        });
    }

    private void openEntityVaultSelection(EntityEditorModel entityModel) {
        List<VaultEntityListSelectionElementModel> elements = new ArrayList<>();
        Map<String, CompoundTag> entitiesById = new LinkedHashMap<>();
        List<CompoundTag> storedEntities = Vault.getInstance().getEntities();
        for (int i = 0; i < storedEntities.size(); i++) {
            CompoundTag tag = storedEntities.get(i);
            if (tag == null || tag.isEmpty()) {
                continue;
            }
            Identifier id = Identifier.fromNamespaceAndPath("cadeditor", "editor_vault_entity_" + i);
            elements.add(new VaultEntityListSelectionElementModel(id, tag));
            entitiesById.put(id.toString(), tag.copy());
        }
        if (elements.isEmpty()) {
            return;
        }
        ModScreenHandler.openListSelectionScreen(ModTexts.VAULT, "vault_entity_editor", elements, selectedId -> {
            CompoundTag chosen = entitiesById.get(selectedId);
            if (chosen == null) {
                return;
            }
            entityModel.handleEntityReplaced(chosen.copy());
        });
    }

    @Override
    public void updateDoneButton() {
        EditorController.super.updateDoneButton();
    }
}
