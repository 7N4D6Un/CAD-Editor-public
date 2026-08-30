package com.github.rinorsi.cadeditor.client.screen.model.category.config;

import com.github.franckyi.databindings.api.ObservableList;
import com.github.franckyi.guapi.api.util.DebugMode;
import com.github.rinorsi.cadeditor.client.ClientConfiguration;
import com.github.rinorsi.cadeditor.client.ClientInit;
import com.github.rinorsi.cadeditor.client.screen.model.ConfigEditorScreenModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.ActionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.util.texteditor.SyntaxHighlightingPreset;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static com.github.rinorsi.cadeditor.client.ClientInit.THEMES;

public class ClientConfigCategoryModel extends ConfigCategoryModel {
    private EnumEntryModel<String> guapiThemeEntry;
    private EnumEntryModel<DebugMode> guapiDebugModeEntry;
    private EnumEntryModel<SyntaxHighlightingPreset> syntaxHighlightingPresetEntry;
    private IntegerEntryModel selectionScreenMaxItemsEntry;
    private BooleanEntryModel allowEditorInContainerScreensEntry;

    public ClientConfigCategoryModel(ConfigEditorScreenModel editor) {
        super(ModTexts.CLIENT, editor);
    }

    @Override
    protected void setupEntries() {
        ObservableList<EntryModel> entries = getEntries();
        MutableComponent themeLabel = ModTexts.THEME;
        EnumEntryModel<String> themeEntryModel = new EnumEntryModel<>(this, themeLabel, THEMES,
                resolveTheme(ClientConfiguration.INSTANCE.getGuapiTheme()), ClientConfiguration.INSTANCE::setGuapiTheme)
                .withTextFactory(theme -> Component.literal(switch (theme) {
                    case "monochrome" -> "Monochrome";
                    default -> "Vanilla";
                })).withWeight(2);
        this.guapiThemeEntry = themeEntryModel;
        MutableComponent debugModeLabel = ModTexts.DEBUG_MODE;
        DebugMode[] debugModeArrValues = DebugMode.values();
        DebugMode guapiDebugMode = ClientConfiguration.INSTANCE.getGuapiDebugMode();
        EnumEntryModel<DebugMode> enumEntryModel = new EnumEntryModel<>(this, debugModeLabel, debugModeArrValues, guapiDebugMode, ClientConfiguration.INSTANCE::setGuapiDebugMode).withTextFactory(DebugMode::toComponent).withWeight(2);
        this.guapiDebugModeEntry = enumEntryModel;
        MutableComponent syntaxHighlightingPresetLabel = ModTexts.SYNTAX_HIGHLIGHTING_PRESET;
        SyntaxHighlightingPreset[] syntaxHighlightingPresetArrValues = SyntaxHighlightingPreset.values();
        SyntaxHighlightingPreset syntaxHighlightingPreset = ClientConfiguration.INSTANCE.getSyntaxHighlightingPreset();
        EnumEntryModel<SyntaxHighlightingPreset> syntaxHighlightingPresetEntryModel = new EnumEntryModel<>(this, syntaxHighlightingPresetLabel, syntaxHighlightingPresetArrValues, syntaxHighlightingPreset, ClientConfiguration.INSTANCE::setSyntaxHighlightingPreset).withTextFactory(SyntaxHighlightingPreset::toComponent).withWeight(2);
        this.syntaxHighlightingPresetEntry = syntaxHighlightingPresetEntryModel;
        MutableComponent selectionScreenMaxItemsLabel = ModTexts.SELECTION_SCREEN_MAX_ITEMS;
        int selectionScreenMaxItems = ClientConfiguration.INSTANCE.getSelectionScreenMaxItems();
        IntegerEntryModel integerEntryModel = new IntegerEntryModel(this, selectionScreenMaxItemsLabel, selectionScreenMaxItems, ClientConfiguration.INSTANCE::setSelectionScreenMaxItems).withWeight(2);
        this.selectionScreenMaxItemsEntry = integerEntryModel;
        MutableComponent allowEditorInContainerScreensLabel = ModTexts.gui("allow_editor_in_container_screens");
        boolean isAllowEditorInContainerScreens = ClientConfiguration.INSTANCE.isAllowEditorInContainerScreens();
        BooleanEntryModel booleanEntryModel = new BooleanEntryModel(this, allowEditorInContainerScreensLabel, isAllowEditorInContainerScreens, ClientConfiguration.INSTANCE::setAllowEditorInContainerScreens).withWeight(2);
        this.allowEditorInContainerScreensEntry = booleanEntryModel;
        entries.addAll(themeEntryModel, enumEntryModel, syntaxHighlightingPresetEntryModel, integerEntryModel, booleanEntryModel, new ActionEntryModel(this, ModTexts.RELOAD_CONFIG, this::reload));
        ClientConfiguration defaults = ClientConfiguration.defaults();
        themeEntryModel.setFactoryDefault(resolveTheme(defaults.getGuapiTheme()));
        enumEntryModel.setFactoryDefault(defaults.getGuapiDebugMode());
        syntaxHighlightingPresetEntryModel.setFactoryDefault(defaults.getSyntaxHighlightingPreset());
        integerEntryModel.setFactoryDefault(defaults.getSelectionScreenMaxItems());
        booleanEntryModel.setFactoryDefault(defaults.isAllowEditorInContainerScreens());
    }

    private void reload() {
        ClientConfiguration.load();
        syncEntries();
    }

    private static String resolveTheme(String id) {
        for (String theme : THEMES) {
            if (theme.equalsIgnoreCase(id)) {
                return theme;
            }
        }
        return THEMES[0];
    }

    public void syncEntries() {
        this.guapiThemeEntry.setValue(resolveTheme(ClientConfiguration.INSTANCE.getGuapiTheme()));
        this.guapiDebugModeEntry.setValue(ClientConfiguration.INSTANCE.getGuapiDebugMode());
        this.syntaxHighlightingPresetEntry.setValue(ClientConfiguration.INSTANCE.getSyntaxHighlightingPreset());
        this.selectionScreenMaxItemsEntry.setValue(ClientConfiguration.INSTANCE.getSelectionScreenMaxItems());
        this.allowEditorInContainerScreensEntry.setValue(ClientConfiguration.INSTANCE.isAllowEditorInContainerScreens());
        this.guapiThemeEntry.markClean();
        this.guapiDebugModeEntry.markClean();
        this.syntaxHighlightingPresetEntry.markClean();
        this.selectionScreenMaxItemsEntry.markClean();
        this.allowEditorInContainerScreensEntry.markClean();
    }

    @Override 
    public void apply() {
        super.apply();
        ClientConfiguration.save();
        ClientInit.syncGuapiConfig();
    }
}
