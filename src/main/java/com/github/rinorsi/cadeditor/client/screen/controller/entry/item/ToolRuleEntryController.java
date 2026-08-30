package com.github.rinorsi.cadeditor.client.screen.controller.entry.item;

import com.github.franckyi.databindings.api.ObservableList;
import com.github.franckyi.databindings.api.event.ObservableValueChangeListener;
import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.HBox;
import com.github.franckyi.guapi.api.node.Label;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.Parent;
import com.github.franckyi.guapi.api.node.builder.LabelBuilder;
import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.screen.controller.entry.EntryController;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.ToolRuleEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.item.ToolRuleEntryView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;


public class ToolRuleEntryController extends EntryController<ToolRuleEntryModel, ToolRuleEntryView> {
    private Component summaryDisplay;
    private Component renderedSummary;
    private String summaryText;
    private boolean updatingSelectionWidth;
    private boolean handlingSelectionContainerChange;
    private HBox selectionContainer;
    private ObservableValueChangeListener<? super Integer> selectionContainerWidthListener;
    private ObservableValueChangeListener<? super Integer> selectionContainerComputedWidthListener;
    private ObservableValueChangeListener<? super Integer> selectionContainerParentPrefWidthListener;
    private Label summaryLabel;
    private String inlineText;

    public ToolRuleEntryController(ToolRuleEntryModel model, ToolRuleEntryView view) {
        super(model, view);
        this.summaryDisplay = Component.empty();
        this.renderedSummary = Component.empty();
        this.summaryText = "";
        this.inlineText = "";
    }

    
    @Override 
    public void bind() {
        super.bind();
        ((ToolRuleEntryView) this.view).setListButtonsVisible(true);
        updateRuleIndex();
        ((ToolRuleEntryModel) this.model).listIndexProperty().addListener(v -> {
            updateRuleIndex();
        });
        installSummaryLabelOverlay();
        if (this.summaryLabel != null) {
            this.summaryLabel.widthProperty().addListener(v -> refreshSummaryRenderer());
            this.summaryLabel.computedWidthProperty().addListener(v -> refreshSummaryRenderer());
            this.summaryLabel.parentPrefWidthProperty().addListener(v -> refreshSummaryRenderer());
        }
        ((ToolRuleEntryView) this.view).getSelectionField().widthProperty().addListener(v -> refreshSummaryRenderer());
        ((ToolRuleEntryView) this.view).getSelectionField().computedWidthProperty().addListener(v -> refreshSummaryRenderer());
        ((ToolRuleEntryView) this.view).getSelectionField().parentPrefWidthProperty().addListener(v -> refreshSummaryRenderer());
        ((ToolRuleEntryView) this.view).getSelectionField().parentProperty().addListener((oldP, newP) -> {
            observeSelectionContainer(newP);
        });
        observeSelectionContainer(((ToolRuleEntryView) this.view).getSelectionField().getParent());
        updateSummary();
        this.inlineText = toInlineTextFromModel();
        enterDisplayMode();
        ((ToolRuleEntryView) this.view).getSelectionField().focusedProperty().addListener(focused -> {
            if (Boolean.TRUE.equals(focused)) {
                enterEditMode();
                return;
            }
            this.inlineText = ((ToolRuleEntryView) this.view).getSelectionField().getText();
            InlineSelection parsed = parseInlineSelection(this.inlineText);
            ((ToolRuleEntryModel) this.model).setBlockIds(new ArrayList(new LinkedHashSet(parsed.blockIds)));
            ((ToolRuleEntryModel) this.model).setTagIds(new ArrayList(new LinkedHashSet(parsed.tagIds)));
            updateSummary();
            validateRule();
            enterDisplayMode();
        });
        ((ToolRuleEntryView) this.view).getSelectionField().textProperty().addListener(value -> {
            if (Boolean.TRUE.equals(((ToolRuleEntryView) this.view).getSelectionField().focusedProperty().getValue())) {
                this.inlineText = value;
                InlineSelection parsed = parseInlineSelection(value);
                ((ToolRuleEntryModel) this.model).setBlockIds(new ArrayList(new LinkedHashSet(parsed.blockIds)));
                ((ToolRuleEntryModel) this.model).setTagIds(new ArrayList(new LinkedHashSet(parsed.tagIds)));
                updateSummary();
                validateRule();
            }
        });
        ((ToolRuleEntryView) this.view).getSelectBlocksButton().onAction(this::openBlockSelection);
        ((ToolRuleEntryView) this.view).getSelectTagsButton().onAction(this::openTagSelection);
        ((ToolRuleEntryView) this.view).getDeleteRuleButton().onAction(() -> {
            if (((ToolRuleEntryModel) this.model).getListIndex() >= 0) {
                ((ToolRuleEntryModel) this.model).getCategory().deleteEntry(((ToolRuleEntryModel) this.model).getListIndex());
            }
        });
        ((ToolRuleEntryView) this.view).getDeleteRuleButton().disableProperty().bind(((ToolRuleEntryModel) this.model).listIndexProperty().lt(0).or(((ToolRuleEntryModel) this.model).enabledProperty().not()));
        syncRuleEnabled();
        ((ToolRuleEntryModel) this.model).enabledProperty().addListener(enabled -> syncRuleEnabled());
        ((ToolRuleEntryView) this.view).getSpeedField().setText(((ToolRuleEntryModel) this.model).getSpeedText());
        ((ToolRuleEntryView) this.view).getSpeedField().textProperty().addListener(value -> {
            boolean valid = ((ToolRuleEntryModel) this.model).setSpeedText(value);
            if (!valid) {
                updateSummary();
                ((ToolRuleEntryModel) this.model).setValid(false);
            } else {
                updateSummary();
                validateRule();
            }
        });
        ((ToolRuleEntryView) this.view).getBehaviorButton().getValues().setAll(List.of(ToolRuleEntryModel.DropBehavior.values()));
        ((ToolRuleEntryView) this.view).getBehaviorButton().setTextFactory(value -> value.getText());
        ((ToolRuleEntryView) this.view).getBehaviorButton().setValue(((ToolRuleEntryModel) this.model).getBehavior());
        ((ToolRuleEntryView) this.view).getBehaviorButton().valueProperty().addListener(value -> {
            ((ToolRuleEntryModel) this.model).setBehavior(value);
            updateSummary();
            updateBehaviorTooltip();
            validateRule();
        });
        updateBehaviorTooltip();
        validateRule();
    }

    private void installSummaryLabelOverlay() {
        this.summaryLabel = ((LabelBuilder) GuapiHelper.label().prefHeight(16)).textAlign(GuapiHelper.CENTER_LEFT);
        this.summaryLabel.setDisable(false);
        this.summaryLabel.setPrefWidth(0);
        this.summaryLabel.setMaxWidth(Node.INFINITE_SIZE);
        Parent parent = ((ToolRuleEntryView) this.view).getSelectionField().getParent();
        if (parent instanceof HBox) {
            HBox container = (HBox) parent;
            int idx = container.getChildren().indexOf(((ToolRuleEntryView) this.view).getSelectionField());
            if (idx < 0) {
                idx = container.getChildren().size();
            }
            container.getChildren().add(idx, this.summaryLabel);
            this.summaryLabel.setPrefWidth(((ToolRuleEntryView) this.view).getSelectionField().getPrefWidth());
        }
    }

    private void enterEditMode() {
        if (this.summaryLabel != null) {
            this.summaryLabel.setVisible(false);
        }
        ((ToolRuleEntryView) this.view).getSelectionField().setDisable(false);
        ((ToolRuleEntryView) this.view).getSelectionField().setVisible(true);
        ((ToolRuleEntryView) this.view).getSelectionField().setMinWidth(60);
        ((ToolRuleEntryView) this.view).getSelectionField().setPrefWidth(0);
        ((ToolRuleEntryView) this.view).getSelectionField().setMaxWidth(Node.INFINITE_SIZE);
        ((ToolRuleEntryView) this.view).getSelectionField().setText(this.inlineText);
        int end = ((ToolRuleEntryView) this.view).getSelectionField().getText().length();
        ((ToolRuleEntryView) this.view).getSelectionField().setCursorPosition(end);
        ((ToolRuleEntryView) this.view).getSelectionField().setHighlightPosition(end);
    }

    private void enterDisplayMode() {
        ((ToolRuleEntryView) this.view).getSelectionField().setVisible(false);
        ((ToolRuleEntryView) this.view).getSelectionField().setDisable(true);
        ((ToolRuleEntryView) this.view).getSelectionField().setMinWidth(0);
        ((ToolRuleEntryView) this.view).getSelectionField().setPrefWidth(0);
        ((ToolRuleEntryView) this.view).getSelectionField().setMaxWidth(0);
        if (this.summaryLabel != null) {
            this.summaryLabel.setVisible(true);
            this.summaryLabel.setPrefWidth(computeSelectionContainerWidth());
            this.summaryLabel.setMaxWidth(Node.INFINITE_SIZE);
            refreshSummaryRenderer();
            this.summaryLabel.setLabel(this.renderedSummary.copy());
        }
    }

    
    private void openBlockSelection() {
        Set<Identifier> initiallySelected = new LinkedHashSet<>(((ToolRuleEntryModel) this.model).getBlockIds());
        ModScreenHandler.openListSelectionScreen(ModTexts.BLOCK, "tool_rule_blocks", ClientCache.getBlockSelectionItems(), null, true, selected -> {
            ((ToolRuleEntryModel) this.model).setBlockIds(new ArrayList(new LinkedHashSet(selected)));
            this.inlineText = toInlineTextFromModel();
            if (Boolean.TRUE.equals(((ToolRuleEntryView) this.view).getSelectionField().focusedProperty().getValue())) {
                ((ToolRuleEntryView) this.view).getSelectionField().setText(this.inlineText);
            } else {
                updateSummary();
                if (this.summaryLabel != null) {
                    this.summaryLabel.setLabel(this.renderedSummary.copy());
                }
            }
            validateRule();
        }, initiallySelected);
    }

    
    private void openTagSelection() {
        Set<Identifier> initiallySelected = new LinkedHashSet<>(((ToolRuleEntryModel) this.model).getTagIds());
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("tool_rule_tags"), "tool_rule_tags", ClientCache.getBlockTagSelectionItems(), null, true, selected -> {
            ((ToolRuleEntryModel) this.model).setTagIds(new ArrayList(new LinkedHashSet(selected)));
            this.inlineText = toInlineTextFromModel();
            if (Boolean.TRUE.equals(((ToolRuleEntryView) this.view).getSelectionField().focusedProperty().getValue())) {
                ((ToolRuleEntryView) this.view).getSelectionField().setText(this.inlineText);
            } else {
                updateSummary();
                if (this.summaryLabel != null) {
                    this.summaryLabel.setLabel(this.renderedSummary.copy());
                }
            }
            validateRule();
        }, initiallySelected);
    }

    
    private void updateSummary() {
        this.summaryDisplay = ((ToolRuleEntryModel) this.model).getSummaryComponent().copy();
        this.summaryText = ((ToolRuleEntryModel) this.model).hasSelection() ? this.summaryDisplay.getString() : "";
        refreshSummaryRenderer();
        ObservableList<Component> tfTooltip = ((ToolRuleEntryView) this.view).getSelectionField().getTooltip();
        tfTooltip.clear();
        ((ToolRuleEntryModel) this.model).getSummaryTooltip().forEach(c -> {
            tfTooltip.add(c.copy());
        });
        if (this.summaryLabel != null) {
            ObservableList<Component> lblTooltip = this.summaryLabel.getTooltip();
            lblTooltip.clear();
            ((ToolRuleEntryModel) this.model).getSummaryTooltip().forEach(c2 -> {
                lblTooltip.add(c2.copy());
            });
            this.summaryLabel.setLabel(this.renderedSummary.copy());
        }
    }

    private void refreshSummaryRenderer() {
        this.renderedSummary = this.summaryText.isEmpty() ? Component.empty() : trimSummaryComponent(this.summaryDisplay.copy());
        if (this.summaryLabel != null && this.summaryLabel.isVisible()) {
            this.summaryLabel.setLabel(this.renderedSummary.copy());
        }
    }

    private Component trimSummaryComponent(Component component) {
        String trimmed;
        int maxWidth = computeSummaryInnerWidth();
        if (maxWidth <= 0) {
            return this.renderedSummary.copy();
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.font == null) {
            return component.copy();
        }
        Font font = mc.font;
        if (font.width(component) <= maxWidth) {
            return component.copy();
        }
        int ellipsisWidth = font.width("...");
        int availableWidth = Math.max(0, maxWidth - ellipsisWidth);
        String strPlainSubstrByWidth = font.plainSubstrByWidth(component.getString(), availableWidth);
        while (true) {
            trimmed = strPlainSubstrByWidth;
            if (trimmed.isEmpty() || font.width(trimmed) <= availableWidth) {
                break;
            }
            strPlainSubstrByWidth = trimmed.substring(0, trimmed.length() - 1);
        }
        MutableComponent trimmedComponent = Component.literal(trimmed).withStyle(component.getStyle());
        if (trimmed.isEmpty()) {
            return Component.literal("...").withStyle(component.getStyle());
        }
        return trimmedComponent.append(Component.literal("...").withStyle(component.getStyle()));
    }

    private int computeSummaryInnerWidth() {
        Node n = (this.summaryLabel == null || !this.summaryLabel.isVisible()) ? ((ToolRuleEntryView) this.view).getSelectionField() : this.summaryLabel;
        int width = n.getWidth();
        if (width <= 0) {
            width = n.getComputedWidth();
        }
        if (width <= 0) {
            width = n.getParentPrefWidth();
        }
        if (width <= 0) {
            width = n.getPrefWidth();
        }
        if (width <= 0) {
            width = computeSelectionContainerWidth();
        }
        if (width <= 0) {
            return 0;
        }
        int padding = 0;
        if (n == ((ToolRuleEntryView) this.view).getSelectionField()) {
            padding = ((ToolRuleEntryView) this.view).getSelectionField().getPadding().getHorizontal();
        }
        int usable = width - padding;
        int maxWidth = (int) (((double) Math.max(0, usable)) * 0.9d);
        return Math.max(0, maxWidth);
    }

    private int computeSelectionContainerWidth() {
        Parent parent = ((ToolRuleEntryView) this.view).getSelectionField().getParent();
        if (!(parent instanceof HBox)) {
            return 0;
        }
        HBox container = (HBox) parent;
        int parentWidth = container.getWidth();
        if (parentWidth <= 0) {
            parentWidth = container.getComputedWidth();
        }
        if (parentWidth <= 0) {
            parentWidth = container.getParentPrefWidth();
        }
        if (parentWidth <= 0) {
            parentWidth = container.getPrefWidth();
        }
        if (parentWidth <= 0) {
            return 0;
        }
        int spacing = container.getSpacing();
        int totalSpacing = spacing * Math.max(0, container.getChildren().size() - 1);
        int padding = container.getPadding().getHorizontal();
        int buttonWidth = 0;
        if (container.getChildren().contains(((ToolRuleEntryView) this.view).getSelectBlocksButton())) {
            buttonWidth = 0 + getNodeWidth(((ToolRuleEntryView) this.view).getSelectBlocksButton());
        }
        if (container.getChildren().contains(((ToolRuleEntryView) this.view).getSelectTagsButton())) {
            buttonWidth += getNodeWidth(((ToolRuleEntryView) this.view).getSelectTagsButton());
        }
        return Math.max(0, ((parentWidth - totalSpacing) - padding) - buttonWidth);
    }

    private void observeSelectionContainer(Parent parent) {
        if (!(parent instanceof HBox)) {
            detachSelectionContainerListeners();
            return;
        }
        HBox container = (HBox) parent;
        if (this.selectionContainer != container) {
            detachSelectionContainerListeners();
            this.selectionContainer = container;
            this.selectionContainerWidthListener = container.widthProperty().addListener(value -> {
                handleSelectionContainerSizeChange();
            });
            this.selectionContainerComputedWidthListener = container.computedWidthProperty().addListener(value -> {
                handleSelectionContainerSizeChange();
            });
            this.selectionContainerParentPrefWidthListener = container.parentPrefWidthProperty().addListener(value -> {
                handleSelectionContainerSizeChange();
            });
        }
        handleSelectionContainerSizeChange();
    }

    private void detachSelectionContainerListeners() {
        if (this.selectionContainer == null) {
            return;
        }
        if (this.selectionContainerWidthListener != null) {
            this.selectionContainer.widthProperty().removeListener(this.selectionContainerWidthListener);
            this.selectionContainerWidthListener = null;
        }
        if (this.selectionContainerComputedWidthListener != null) {
            this.selectionContainer.computedWidthProperty().removeListener(this.selectionContainerComputedWidthListener);
            this.selectionContainerComputedWidthListener = null;
        }
        if (this.selectionContainerParentPrefWidthListener != null) {
            this.selectionContainer.parentPrefWidthProperty().removeListener(this.selectionContainerParentPrefWidthListener);
            this.selectionContainerParentPrefWidthListener = null;
        }
        this.selectionContainer = null;
    }

    private void handleSelectionContainerSizeChange() {
        if (this.handlingSelectionContainerChange) {
            return;
        }
        this.handlingSelectionContainerChange = true;
        try {
            updateSelectionFieldPreferredWidth();
            refreshSummaryRenderer();
            if (this.summaryLabel != null) {
                this.summaryLabel.setLabel(this.renderedSummary.copy());
            }
        } finally {
            this.handlingSelectionContainerChange = false;
        }
    }

    private void updateSelectionFieldPreferredWidth() {
        if (this.updatingSelectionWidth) {
            return;
        }
        this.updatingSelectionWidth = true;
        try {
            int width = computeSelectionContainerWidth();
            if (width > 0) {
                int pref = Math.max(width, ((ToolRuleEntryView) this.view).getSelectionField().getMinWidth());
                if (this.summaryLabel != null && this.summaryLabel.isVisible()) {
                    this.summaryLabel.setPrefWidth(pref);
                } else {
                    ((ToolRuleEntryView) this.view).getSelectionField().setPrefWidth(pref);
                }
            }
        } finally {
            this.updatingSelectionWidth = false;
        }
    }

    private int getNodeWidth(Node node) {
        int width = node.getWidth();
        if (width <= 0) {
            width = node.getComputedWidth();
        }
        if (width <= 0) {
            width = node.getParentPrefWidth();
        }
        if (width <= 0) {
            width = node.getPrefWidth();
        }
        return Math.max(width, 0);
    }

    
    private void validateRule() {
        if (!((ToolRuleEntryModel) this.model).hasSelection() && ((ToolRuleEntryModel) this.model).getSpeedText().isBlank()) {
            ((ToolRuleEntryModel) this.model).setValid(true);
            return;
        }
        HolderLookup.RegistryLookup<Block> lookup = (HolderLookup.RegistryLookup) ClientUtil.registryAccess().lookup(Registries.BLOCK).orElse(null);
        if (lookup == null) {
            ((ToolRuleEntryModel) this.model).setValid(false);
        } else {
            ((ToolRuleEntryModel) this.model).setValid(((ToolRuleEntryModel) this.model).toRules(lookup).isPresent());
        }
    }

    
    private void updateBehaviorTooltip() {
        ObservableList<Component> tooltip = ((ToolRuleEntryView) this.view).getBehaviorButton().getTooltip();
        tooltip.clear();
        tooltip.add(ModTexts.gui("tool_rule_behavior").copy().withStyle(ChatFormatting.GRAY));
        tooltip.add(((ToolRuleEntryModel) this.model).getBehavior().getDescription());
    }

    
    private void updateRuleIndex() {
        int index = Math.max(0, ((ToolRuleEntryModel) this.model).getListIndex());
        MutableComponent text = Component.literal(String.format(Locale.ROOT, "#%02d", index + 1));
        if (!((ToolRuleEntryModel) this.model).isEnabled()) {
            text = text.withStyle(ChatFormatting.GRAY);
        }
        ((ToolRuleEntryView) this.view).getRuleLabel().setLabel(text);
    }

    private void syncRuleEnabled() {
        boolean enabled = ((ToolRuleEntryModel) this.model).isEnabled();
        ToolRuleEntryView v = ((ToolRuleEntryView) this.view);
        v.getSpeedField().setDisable(!enabled);
        v.getBehaviorButton().setDisable(!enabled);
        v.getSelectBlocksButton().setDisable(!enabled);
        v.getSelectTagsButton().setDisable(!enabled);
        updateRuleIndex();
    }

    
    private static final class InlineSelection {
        final Set<Identifier> blockIds = new LinkedHashSet<>();
        final Set<Identifier> tagIds = new LinkedHashSet<>();

        private InlineSelection() {
        }
    }

    private InlineSelection parseInlineSelection(String raw) {
        InlineSelection out = new InlineSelection();
        if (raw == null || raw.isBlank()) {
            return out;
        }
        String[] tokens = raw.split("[,\\s]+");
        for (String t : tokens) {
            if (!t.isBlank()) {
                String s = t.trim();
                boolean isTag = s.startsWith("#");
                if (isTag) {
                    s = s.substring(1);
                }
                if (!s.contains(":")) {
                    s = "minecraft:" + s;
                }
                Identifier id = Identifier.tryParse(s);
                if (id != null) {
                    if (isTag) {
                        out.tagIds.add(id);
                    } else {
                        out.blockIds.add(id);
                    }
                }
            }
        }
        return out;
    }

    
    private String toInlineTextFromModel() {
        LinkedHashSet<Identifier> blocks = new LinkedHashSet<>(((ToolRuleEntryModel) this.model).getBlockIds());
        LinkedHashSet<Identifier> tags = new LinkedHashSet<>(((ToolRuleEntryModel) this.model).getTagIds());
        StringBuilder sb = new StringBuilder();
        for (Identifier id : blocks) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(id.toString());
        }
        for (Identifier tagId : tags) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append('#').append(tagId.toString());
        }
        return sb.toString();
    }

    
    @Override 
    public ToolRuleEntryModel getModel() {
        return (ToolRuleEntryModel) this.model;
    }
}
