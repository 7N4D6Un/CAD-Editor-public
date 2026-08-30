package com.github.rinorsi.cadeditor.client.screen.controller.entry.item;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.util.Predicates;
import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.screen.controller.entry.SelectionEntryController;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemEnchantmentsCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.EnchantmentEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.item.EnchantmentEntryView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;


public class EnchantmentEntryController extends SelectionEntryController<EnchantmentEntryModel, EnchantmentEntryView> {
    public EnchantmentEntryController(EnchantmentEntryModel model, EnchantmentEntryView view) {
        super(model, view);
    }

    
    @Override 
    public void bind() {
        super.bind();
        ((EnchantmentEntryView) this.view).getPlusButton().onAction(() -> {
            ((EnchantmentEntryModel) this.model).levelProperty().incr();
        });
        ((EnchantmentEntryView) this.view).getMinusButton().onAction(() -> {
            ((EnchantmentEntryModel) this.model).levelProperty().decr();
        });
        ((EnchantmentEntryView) this.view).getLevelField().setValidator(Predicates.range(0, 999999999));
        ((EnchantmentEntryView) this.view).getLevelField().textProperty().addListener(value -> {
            if (((EnchantmentEntryView) this.view).getLevelField().isValid()) {
                int level = Integer.parseInt(value);
                ((EnchantmentEntryModel) this.model).setLevel(level);
                ((EnchantmentEntryView) this.view).getPlusButton().setDisable(level == 999999999);
                ((EnchantmentEntryView) this.view).getMinusButton().setDisable(level == 0);
                return;
            }
            ((EnchantmentEntryView) this.view).getPlusButton().setDisable(true);
            ((EnchantmentEntryView) this.view).getMinusButton().setDisable(true);
        });
        ((EnchantmentEntryView) this.view).getLevelField().setText(Integer.toString(((EnchantmentEntryModel) this.model).getLevel()));
        ((EnchantmentEntryModel) this.model).levelProperty().addListener(newValue -> {
            ((EnchantmentEntryView) this.view).getLevelField().setText(String.valueOf(newValue));
        });
        ((EnchantmentEntryModel) this.model).validProperty().bind(((EnchantmentEntryView) this.view).getLevelField().validProperty());
        ((EnchantmentEntryModel) this.model).valueProperty().addListener(this::updatePreview);
        updatePreview(((EnchantmentEntryModel) this.model).getValue());
    }

    
    @Override 
    protected void openSelectionScreen() {
        ItemEnchantmentsCategoryModel category = (ItemEnchantmentsCategoryModel) ((EnchantmentEntryModel) this.model).getCategory();
        Set<Identifier> selected = new HashSet<>(category.getExistingEnchantmentIds());
        Identifier currentId = parseResourceLocation(((EnchantmentEntryModel) this.model).getValue());
        if (currentId != null) {
            selected.add(currentId);
        }
        List<? extends ListSelectionElementModel> items = ((EnchantmentEntryModel) this.model).getSelectionItems();
        MutableComponent selectionScreenTitle = ((EnchantmentEntryModel) this.model).getSelectionScreenTitle();
        String value = ((EnchantmentEntryModel) this.model).getValue().contains(":") ? ((EnchantmentEntryModel) this.model).getValue() : "minecraft:" + ((EnchantmentEntryModel) this.model).getValue();
        ModScreenHandler.openListSelectionScreen(selectionScreenTitle, value, items, ((EnchantmentEntryModel) this.model)::setValue, true, ids -> {
            category.syncSelection(new HashSet(ids), (EnchantmentEntryModel) this.model);
        }, selected);
    }

    private void updatePreview(String value) {
        Identifier id = parseResourceLocation(value);
        if (id == null) {
            ((EnchantmentEntryView) this.view).setPreviewVisible(false);
        } else {
            ClientCache.findEnchantmentSelectionItem(id).ifPresentOrElse(item -> {
                ((EnchantmentEntryView) this.view).getPreviewItemView().setItem(item.getItem());
                ((EnchantmentEntryView) this.view).getPreviewLabel().setLabel(GuapiHelper.translated(item.getName()).withStyle(ChatFormatting.GRAY));
                ((EnchantmentEntryView) this.view).setPreviewVisible(true);
            }, () -> {
                ((EnchantmentEntryView) this.view).setPreviewVisible(false);
            });
        }
    }
}
