package com.github.rinorsi.cadeditor.client;


import com.github.franckyi.databindings.api.IntegerProperty;
import com.github.franckyi.guapi.api.Guapi;
import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.Scene;
import com.github.rinorsi.cadeditor.client.context.BlockEditorContext;
import com.github.rinorsi.cadeditor.client.context.EditorContext;
import com.github.rinorsi.cadeditor.client.context.EntityEditorContext;
import com.github.rinorsi.cadeditor.client.context.ItemEditorContext;
import com.github.rinorsi.cadeditor.client.screen.model.BlockEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.ConfigEditorScreenModel;
import com.github.rinorsi.cadeditor.client.screen.model.EntityEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.NBTEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.SNBTEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.TextFormatDialogModel;
import com.github.rinorsi.cadeditor.client.screen.model.VaultScreenModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.ColorSelectionScreenModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.ListSelectionFilter;
import com.github.rinorsi.cadeditor.client.screen.model.selection.ListSelectionScreenModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ItemListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.SelectableItemListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.SelectableSpriteListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.SelectableTagListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.SpriteListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.TagListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.mvc.ColorSelectionScreenMVC;
import com.github.rinorsi.cadeditor.client.screen.mvc.ConfigEditorMVC;
import com.github.rinorsi.cadeditor.client.screen.mvc.ListSelectionScreenMVC;
import com.github.rinorsi.cadeditor.client.screen.mvc.NBTEditorMVC;
import com.github.rinorsi.cadeditor.client.screen.mvc.SNBTEditorMVC;
import com.github.rinorsi.cadeditor.client.screen.mvc.StandardEditorMVC;
import com.github.rinorsi.cadeditor.client.screen.mvc.TextFormatDialogMVC;
import com.github.rinorsi.cadeditor.client.screen.mvc.VaultScreenMVC;
import com.github.rinorsi.cadeditor.client.util.ScreenScalingManager;
import com.github.rinorsi.cadeditor.common.EditorType;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ModScreenHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void openSettingsScreen() {
        openScaledScreen(GuapiHelper.mvc(ConfigEditorMVC.INSTANCE, new ConfigEditorScreenModel()));
    }

    public static void openListSelectionScreen(MutableComponent title, String attributeName, List<? extends ListSelectionElementModel> items, Consumer<String> action) {
        openListSelectionScreen(title, attributeName, items, action, false, null, Set.of(), Collections.emptyList(), null);
    }

    public static void openListSelectionScreen(MutableComponent title, String attributeName, List<? extends ListSelectionElementModel> items, Consumer<String> action, boolean multiSelect, Consumer<List<Identifier>> multiAction, Set<Identifier> initiallySelected) {
        openListSelectionScreen(title, attributeName, items, action, multiSelect, multiAction, initiallySelected, Collections.emptyList(), null);
    }

    public static void openListSelectionScreen(MutableComponent title, String attributeName, List<? extends ListSelectionElementModel> items, Consumer<String> action, List<ListSelectionFilter> filters, String initialFilterId) {
        openListSelectionScreen(title, attributeName, items, action, false, null, Set.of(), filters, initialFilterId);
    }

    public static void openListSelectionScreen(MutableComponent title, String attributeName, List<? extends ListSelectionElementModel> items, Consumer<String> action, boolean multiSelect, Consumer<List<Identifier>> multiAction, Set<Identifier> initiallySelected, List<ListSelectionFilter> filters, String initialFilterId) {
        List<? extends ListSelectionElementModel> singleSelectionItems;
        if (multiSelect) {
            singleSelectionItems = items;
        } else {
            singleSelectionItems = toSingleSelectionItems(items);
        }
        List<? extends ListSelectionElementModel> renderedItems = singleSelectionItems;
        openScaledScreen(GuapiHelper.mvc(ListSelectionScreenMVC.INSTANCE, new ListSelectionScreenModel(title, attributeName, renderedItems, action, multiSelect, multiAction, initiallySelected, filters == null ? Collections.emptyList() : filters, initialFilterId)));
    }

    private static List<ListSelectionElementModel> toSingleSelectionItems(List<? extends ListSelectionElementModel> items) {
        return (List) items.stream().map(ModScreenHandler::toSingleSelectionItem).collect(Collectors.toList());
    }

    private static ListSelectionElementModel toSingleSelectionItem(ListSelectionElementModel item) {
        if (item instanceof SelectableItemListSelectionElementModel) {
            SelectableItemListSelectionElementModel selectable = (SelectableItemListSelectionElementModel) item;
            return new ItemListSelectionElementModel(selectable.getName(), selectable.getId(), (Supplier<ItemStack>) () -> {
                return selectable.getItem().copy();
            });
        }
        if (item instanceof SelectableTagListSelectionElementModel) {
            return new TagListSelectionElementModel(((SelectableTagListSelectionElementModel) item).getId());
        }
        if (item instanceof SelectableSpriteListSelectionElementModel selectable) {
            return new SpriteListSelectionElementModel(selectable.getName(), selectable.getId(), selectable.getSpriteFactory());
        }
        return item;
    }

    public static void openColorSelectionScreen(ColorSelectionScreenModel.Target target, int color, Consumer<String> action) {
        openScaledScreen(GuapiHelper.mvc(ColorSelectionScreenMVC.INSTANCE, new ColorSelectionScreenModel(target, action, color)));
    }

    public static void openTextFormatDialog(TextFormatDialogModel model) {
        openScaledScreen(GuapiHelper.mvc(TextFormatDialogMVC.INSTANCE, model));
    }

    public static void openVault() {
        openScaledScreen(GuapiHelper.mvc(VaultScreenMVC.INSTANCE, new VaultScreenModel()));
    }

    private static void openScaledScreen(Node root) {
        openScaledScreen(root, false);
    }

    private static void openScaledScreen(Node root, boolean replace) {
        Consumer<Scene> action;
        if (replace) {
            action = Guapi.getScreenHandler()::replaceScene;
        } else {
            action = Guapi.getScreenHandler()::showScene;
        }
        try {
            action.accept(GuapiHelper.scene(root, true, true).show(scene -> {
                ScreenScalingManager.get().setBaseScale(ClientConfiguration.INSTANCE.getEditorScale());
                scene.widthProperty().addListener(ScreenScalingManager.get()::refresh);
                scene.heightProperty().addListener(ScreenScalingManager.get()::refresh);
            }).hide(scene -> {
                int previousScale = ClientConfiguration.INSTANCE.getEditorScale();
                ClientConfiguration.INSTANCE.setEditorScale(ScreenScalingManager.get().getScaleAndReset());
                if (ClientConfiguration.INSTANCE.getEditorScale() != previousScale) {
                    ClientConfiguration.save();
                }
            }));
        } catch (Exception e) {
            LOGGER.error("打开界面时出错", e);
            ClientUtil.showMessage(ModTexts.Messages.ERROR_GENERIC);
        }
    }

    public static void openEditor(EditorType editorType, EditorContext<?> context) {
        openEditor(editorType, context, false);
    }

    public static void openEditor(EditorType editorType, EditorContext<?> context, boolean replace) {
        Node nodeMvc;
        EditorType resolvedEditorType = editorType;
        if (resolvedEditorType != EditorType.STANDARD && context.getTag() == null) {
            if (context instanceof BlockEditorContext blockContext && !blockContext.getBlockState().hasBlockEntity()) {
                resolvedEditorType = EditorType.STANDARD;
            } else {
                ClientUtil.showMessage(ModTexts.Messages.NO_DATA);
                return;
            }
        }
        switch (resolvedEditorType) {
            case STANDARD:
                if (context instanceof ItemEditorContext ctx) {
                    nodeMvc = GuapiHelper.mvc(StandardEditorMVC.INSTANCE, new ItemEditorModel(ctx));
                } else if (context instanceof BlockEditorContext blockCtx) {
                    nodeMvc = GuapiHelper.mvc(StandardEditorMVC.INSTANCE, new BlockEditorModel(blockCtx));
                } else if (context instanceof EntityEditorContext entityCtx) {
                    nodeMvc = GuapiHelper.mvc(StandardEditorMVC.INSTANCE, new EntityEditorModel(entityCtx));
                } else {
                    throw new IllegalStateException("context should be an instance of ItemEditorContext, BlockEditorContext or EntityEditorContext");
                }
                break;
            case NBT:
                nodeMvc = GuapiHelper.mvc(NBTEditorMVC.INSTANCE, new NBTEditorModel(context));
                break;
            case SNBT:
                nodeMvc = GuapiHelper.mvc(SNBTEditorMVC.INSTANCE, new SNBTEditorModel(context));
                break;
            default:
                throw new AssertionError("Unexpected editor type: " + resolvedEditorType);
        }
        openScaledScreen(nodeMvc, replace);
    }
}