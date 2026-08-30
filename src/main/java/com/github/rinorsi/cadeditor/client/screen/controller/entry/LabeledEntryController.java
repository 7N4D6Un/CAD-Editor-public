package com.github.rinorsi.cadeditor.client.screen.controller.entry;

import com.github.franckyi.guapi.api.node.Node;
import com.github.rinorsi.cadeditor.client.screen.model.entry.LabeledEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.LabeledEntryView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public abstract class LabeledEntryController<M extends LabeledEntryModel, V extends LabeledEntryView> extends EntryController<M, V> {
    public LabeledEntryController(M model, V view) {
        super(model, view);
    }

    @Override
    public void bind() {
        super.bind();
        syncLabelDisplay();
        this.model.labelProperty().addListener(label -> syncLabelDisplay());
        this.model.enabledProperty().addListener(enabled -> syncLabelDisplay());
        syncContentEnabled();
        this.model.enabledProperty().addListener(enabled -> syncContentEnabled());
        syncLabelTooltip();
        this.model.labelTooltipProperty().addListener(this::syncLabelTooltip);
        if (((LabeledEntryView) this.view).getLabel().getParent() == ((LabeledEntryView) this.view).getRoot()) {
            int labelWeight = this.model.getLabelWeight();
            if (labelWeight <= 0) {
                ((LabeledEntryView) this.view).getRoot().getChildren().remove(((LabeledEntryView) this.view).getLabel());
            } else {
                ((LabeledEntryView) this.view).getRoot().setWeight(((LabeledEntryView) this.view).getLabel(), labelWeight);
            }
        }
    }

    private void syncLabelDisplay() {
        MutableComponent label = this.model.getLabel();
        if (label == null) {
            ((LabeledEntryView) this.view).getLabel().setLabel(null);
            return;
        }
        if (this.model.isEnabled()) {
            ((LabeledEntryView) this.view).getLabel().setLabel(label);
        } else {
            ((LabeledEntryView) this.view).getLabel().setLabel(label.copy().withStyle(ChatFormatting.GRAY));
        }
    }

    private void syncContentEnabled() {
        Node content = ((LabeledEntryView) this.view).getLabeledContent();
        if (content != null) {
            content.setDisable(!this.model.isEnabled());
        }
    }

    private void syncLabelTooltip() {
        ArrayList arrayList = new ArrayList(this.model.getLabelTooltip());
        if (arrayList.isEmpty()) {
            arrayList.addAll(resolveWikiTooltipFromLabel());
        }
        ((LabeledEntryView) this.view).getLabel().getTooltip().setAll(wrapTooltipLines(arrayList, 28));
    }

    private Collection<Component> resolveWikiTooltipFromLabel() {
        if (this.model.getLabel() == null || this.model.getLabel().getString().isEmpty()) {
            return List.of();
        }
        if (!(this.model.getLabel().getContents() instanceof TranslatableContents contents)) {
            return List.of();
        }
        String key = contents.getKey();
        if (!key.startsWith("cadeditor.gui.")) {
            return List.of();
        }
        String suffix = key.substring("cadeditor.gui.".length());
        if (suffix.startsWith("wiki.")) {
            return List.of();
        }
        List<Component> lines = new ArrayList<>();
        String base = "cadeditor.gui.wiki." + suffix;
        if (Language.getInstance().has(base)) {
            lines.add(Component.translatable(base).withStyle(ChatFormatting.GRAY));
        }
        for (int i = 0; i < 16; i++) {
            String indexed = base + "." + i;
            if (!Language.getInstance().has(indexed)) {
                if (i != 0 || !lines.isEmpty()) {
                    break;
                }
                return List.of();
            }
            lines.add(Component.translatable(indexed).withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }

    private List<Component> wrapTooltipLines(Collection<Component> source, int maxChars) {
        String text;
        List<Component> out = new ArrayList<>();
        for (Component component : source) {
            if (component != null && (text = component.getString()) != null && !text.isBlank()) {
                String[] chunks = text.split("\\\\n|\\n");
                for (String chunk : chunks) {
                    appendWrapped(out, chunk.trim(), component, maxChars);
                }
            }
        }
        return out;
    }

    private void appendWrapped(List<Component> out, String text, Component source, int maxChars) {
        int split;
        if (text.isEmpty()) {
            return;
        }
        int i = 0;
        while (true) {
            int start = i;
            if (start < text.length()) {
                int end = Math.min(text.length(), start + maxChars);
                if (end < text.length() && (split = lastBreak(text, start, end)) > start + 6) {
                    end = split + 1;
                }
                String piece = text.substring(start, end).trim();
                if (!piece.isEmpty()) {
                    out.add(Component.literal(piece).withStyle(source.getStyle()));
                }
                i = end;
            } else {
                return;
            }
        }
    }

    private int lastBreak(String text, int start, int endExclusive) {
        for (int i = endExclusive - 1; i > start; i--) {
            char c = text.charAt(i);
            if (c == ' ' || c == ',' || c == '.' || c == ';' || c == 65292 || c == 12290 || c == 65307 || c == 65306 || c == 12289 || c == ')' || c == 65289) {
                return i;
            }
        }
        return -1;
    }
}
