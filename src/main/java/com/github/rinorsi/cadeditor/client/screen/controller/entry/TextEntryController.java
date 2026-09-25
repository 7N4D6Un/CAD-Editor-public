package com.github.rinorsi.cadeditor.client.screen.controller.entry;

import com.github.franckyi.databindings.api.ObservableList;
import com.github.rinorsi.cadeditor.client.screen.model.entry.TextEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.TextEntryView;
import com.github.rinorsi.cadeditor.client.util.texteditor.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.github.franckyi.guapi.api.GuapiHelper.*;

public class TextEntryController extends ValueEntryController<TextEntryModel, TextEntryView> implements TextEditorActionHandler {
    private final ObservableList<Formatting> formattings = ObservableList.create();
    private boolean isResettingModel;

    public TextEntryController(TextEntryModel model, TextEntryView view) {
        super(model, view);
    }

    @Override
    public void bind() {
        super.bind();
        view.getTextField().setTextRenderer(this::renderText);
        view.getTextField().focusedProperty().addListener(this::onTextFieldFocus);
        view.getTextField().setOnTextUpdate(this::onTextUpdate);
        view.getTextField().textProperty().addListener(this::updateModel);
        formattings.addListener(this::updateModel);
        view.getTextField().validProperty().addListener(model::setValid);
        initFormattings(model.getValue());
        model.resetDefaultValue();
        model.setOnApply(this::updateModel);
    }

    private void updateModel() {
        if (!isResettingModel) {
            model.setValue(createText());
        }
    }

    @Override
    protected void resetModel() {
        super.resetModel();
        isResettingModel = true;
        try {
            initFormattings(model.getValue());
        } finally {
            isResettingModel = false;
        }
    }

    private void onTextUpdate(int oldCursorPos, int oldHighlightPos, String oldText, int newCursorPos, String newText) {
        int oldLength = oldText.length();
        int newLength = newText.length();
        int amount = newLength - oldLength;
        if (amount != 0) {
            if (oldCursorPos == oldHighlightPos && oldCursorPos != newCursorPos) {
                formattings.removeIf(formatting -> {
                    if (oldCursorPos <= formatting.getStart()) {
                        formatting.setStart(formatting.getStart() + amount);
                        formatting.setEnd(formatting.getEnd() + amount);
                    } else if (oldCursorPos > formatting.getStart() && oldCursorPos <= formatting.getEnd()) {
                        formatting.setEnd(formatting.getEnd() + amount);
                    }
                    return formatting.getStart() >= formatting.getEnd();
                });
            } else {
                int pos = Math.min(oldCursorPos, oldHighlightPos);
                formattings.removeIf(formatting -> {
                    if (pos < formatting.getStart()) {
                        formatting.setStart(formatting.getStart() + amount);
                        formatting.setEnd(formatting.getEnd() + amount);
                    } else if (pos >= formatting.getStart() && pos <= formatting.getEnd()) {
                        formatting.setEnd(formatting.getEnd() + amount);
                    }
                    return formatting.getStart() >= formatting.getEnd();
                });
            }
        }
    }

    private Component renderText(String str, int firstCharacterIndex) {
        if (!str.isEmpty()) {
            TextEditorOutputFormatter formatter = new TextEditorOutputFormatter(text().append(text("")), false);
            formatter.format(str, firstCharacterIndex, formattings);
            return formatter.getText();
        }
        return EMPTY_TEXT;
    }

    private MutableComponent createText() {
        TextEditorOutputFormatter formatter = new TextEditorOutputFormatter(text().append(text("")));
        formatter.format(view.getTextField().getText(), 0, formattings);
        return formatter.getText();
    }

    private void initFormattings(MutableComponent text) {
        TextEditorInputParser parser = new TextEditorInputParser();
        String flattened = parser.flatten(text);
        view.getTextField().setText(flattened);
        formattings.setAll(parser.getFormattings());
    }

    private void onTextFieldFocus(boolean focused) {
        if (focused) {
            model.getCategory().getParent().setActiveTextEditor(this);
        } else if (model.getCategory().getParent().getActiveTextEditor() == this) {
            model.getCategory().getParent().setActiveTextEditor(null);
        }
    }

    @Override
    public void removeColorFormatting() {
        int i = view.getTextField().getCursorPosition();
        int j = view.getTextField().getHighlightPosition();
        if (i == j) return;
        resizeOtherColorFormattings(new ColorFormatting(Math.min(i, j), Math.max(i, j), null), false);
    }

    @Override
    public void addColorFormatting(String color) {
        int i = view.getTextField().getCursorPosition();
        int j = view.getTextField().getHighlightPosition();
        if (i == j) return;
        ColorFormatting formatting = new ColorFormatting(Math.min(i, j), Math.max(i, j), color);
        if (formattings.contains(formatting)) {
            return;
        }
        mergeIdenticalFormattings(ColorFormatting.class, other -> other.getColor().equals(color), formatting);
        resizeOtherColorFormattings(formatting, true);
    }

    @Override
    public void addStyleFormatting(StyleType target) {
        int i = view.getTextField().getCursorPosition();
        int j = view.getTextField().getHighlightPosition();
        if (i == j) return;
        StyleFormatting formatting = new StyleFormatting(Math.min(i, j), Math.max(i, j), target);
        if (formattings.contains(formatting)) {
            formattings.remove(formatting);
            return;
        }
        Optional<StyleFormatting> surrounding = getSurroundingStyleFormatting(formatting);
        if (surrounding.isPresent()) {
            removeStyleFormatting(formatting, surrounding.get());
        } else {
            mergeIdenticalFormattings(StyleFormatting.class, other -> other.getType().equals(target), formatting);
            formattings.add(formatting);
        }
    }

    private <T extends Formatting> void mergeIdenticalFormattings(Class<T> formattingClass, Predicate<T> identicalPredicate, T formatting) {
        Iterator<Formatting> it = formattings.iterator();
        while (it.hasNext()) {
            Formatting f = it.next();
            if (formattingClass.isInstance(f)) {
                T other = formattingClass.cast(f);
                if (identicalPredicate.test(other)) {
                    boolean remove = false;
                    if (other.getEnd() >= formatting.getStart() && other.getEnd() <= formatting.getEnd()) {
                        remove = true;
                        if (other.getStart() < formatting.getStart()) {
                            formatting.setStart(other.getStart());
                        }
                    }
                    if (other.getStart() >= formatting.getStart() && other.getStart() <= formatting.getEnd()) {
                        remove = true;
                        if (other.getEnd() > formatting.getEnd()) {
                            formatting.setEnd(other.getEnd());
                        }
                    }
                    if (remove) {
                        it.remove();
                    }
                }
            }
        }
    }

    private void resizeOtherColorFormattings(ColorFormatting formatting, boolean add) {
        List<Formatting> addedFormattings = new ArrayList<>();
        if (add) {
            addedFormattings.add(formatting);
        }
        Iterator<Formatting> it = formattings.iterator();
        while (it.hasNext()) {
            Formatting f = it.next();
            if (f instanceof ColorFormatting other) {
                if (!other.getColor().equals(formatting.getColor())) {
                    if (formatting.getStart() <= other.getStart() && formatting.getEnd() >= other.getEnd()) {
                        it.remove();
                        continue;
                    }
                    if (formatting.getStart() <= other.getStart() && formatting.getEnd() > other.getStart() && formatting.getEnd() <= other.getEnd()) {
                        other.setStart(formatting.getEnd());
                    }
                    if (formatting.getEnd() >= other.getEnd() && formatting.getStart() < other.getEnd() && formatting.getStart() >= other.getStart()) {
                        other.setEnd(formatting.getStart());
                    }
                    if (formatting.getStart() >= other.getStart() && formatting.getEnd() > other.getStart() && formatting.getEnd() <= other.getEnd()) {
                        addedFormattings.add(new ColorFormatting(formatting.getEnd(), other.getEnd(), other.getColor()));
                        other.setEnd(formatting.getStart());
                    }
                    if (other.getStart() >= other.getEnd()) {
                        it.remove();
                    }
                }
            }
        }
        formattings.addAll(addedFormattings);
    }

    private Optional<StyleFormatting> getSurroundingStyleFormatting(StyleFormatting formatting) {
        return formattings.stream()
                .filter(StyleFormatting.class::isInstance)
                .map(StyleFormatting.class::cast)
                .filter(other -> other.getType().equals(formatting.getType()))
                .filter(other -> formatting.getStart() >= other.getStart() && formatting.getEnd() <= other.getEnd())
                .findFirst();
    }

    private void removeStyleFormatting(StyleFormatting formatting, StyleFormatting other) {
        if (formatting.getStart() == other.getStart()) {
            other.setStart(formatting.getEnd());
        } else if (formatting.getEnd() == other.getEnd()) {
            other.setEnd(formatting.getStart());
        } else {
            int otherEnd = other.getEnd();
            other.setEnd(formatting.getStart());
            formatting.setStart(formatting.getEnd());
            formatting.setEnd(otherEnd);
            formattings.add(formatting);
        }
    }

    @Override
    public void addFontFormatting(String fontId) {
        int i = view.getTextField().getCursorPosition();
        int j = view.getTextField().getHighlightPosition();
        if (i == j) return;
        clearTypeInRange(Math.min(i, j), Math.max(i, j), FontFormatting.class);
        formattings.add(new FontFormatting(Math.min(i, j), Math.max(i, j), fontId));
    }

    @Override
    public void addShadowColorFormatting(int argb) {
        int i = view.getTextField().getCursorPosition();
        int j = view.getTextField().getHighlightPosition();
        if (i == j) return;
        clearTypeInRange(Math.min(i, j), Math.max(i, j), ShadowColorFormatting.class);
        formattings.add(new ShadowColorFormatting(Math.min(i, j), Math.max(i, j), argb));
    }

    @Override
    public void applyGradient(List<Integer> colors, boolean shadow) {
        com.github.franckyi.guapi.api.node.TextField field = view.getTextField();
        int i = field.getCursorPosition();
        int j = field.getHighlightPosition();
        int start;
        int end;
        if (i == j) {
            start = 0;
            end = field.getText().length();
        } else {
            start = Math.min(i, j);
            end = Math.max(i, j);
        }
        if (end <= start || colors == null || colors.isEmpty()) {
            return;
        }
        String text = field.getText();
        int total = 0;
        for (int p = start; p < end; ) {
            int tokenLength = TextTokens.tokenLengthAt(text, p);
            if (tokenLength > 0) {
                p += tokenLength;
                continue;
            }
            int codePoint = text.codePointAt(p);
            int charCount = Character.charCount(codePoint);
            if (codePoint == '\n') {
                p += charCount;
                continue;
            }
            total++;
            p += charCount;
        }
        if (total == 0) {
            return;
        }
        clearTypeInRange(start, end, shadow ? ShadowColorFormatting.class : ColorFormatting.class);
        int colorIndex = 0;
        for (int p = start; p < end; ) {
            int tokenLength = TextTokens.tokenLengthAt(text, p);
            if (tokenLength > 0) {
                p += tokenLength;
                continue;
            }
            int codePoint = text.codePointAt(p);
            int charCount = Character.charCount(codePoint);
            if (codePoint == '\n') {
                p += charCount;
                continue;
            }
            int rgb = interpolateGradient(colors, total == 1 ? 0f : (float) colorIndex / (total - 1));
            if (shadow) {
                formattings.add(new ShadowColorFormatting(p, p + charCount, (rgb & 0xFFFFFF) | 0xFF000000));
            } else {
                formattings.add(new ColorFormatting(p, p + charCount, String.format("#%06X", rgb & 0xFFFFFF)));
            }
            colorIndex++;
            p += charCount;
        }
    }

    @Override
    public void insertToken(String token) {
        com.github.franckyi.guapi.api.node.TextField field = view.getTextField();
        int cursor = field.getCursorPosition();
        String old = field.getText();
        int safeCursor = Math.min(cursor, old.length());
        field.setText(old.substring(0, safeCursor) + token + old.substring(safeCursor));
        field.setCursorPosition(safeCursor + token.length());
        field.setHighlightPosition(safeCursor + token.length());
    }

    private void clearTypeInRange(int start, int end, Class<? extends Formatting> type) {
        List<Formatting> restored = new ArrayList<>();
        Iterator<Formatting> it = formattings.iterator();
        while (it.hasNext()) {
            Formatting f = it.next();
            if (!type.isInstance(f) || f.getEnd() <= start || f.getStart() >= end) {
                continue;
            }
            if (f.getStart() < start) {
                restored.add(copyFormatting(f, f.getStart(), start));
            }
            if (f.getEnd() > end) {
                restored.add(copyFormatting(f, end, f.getEnd()));
            }
            it.remove();
        }
        formattings.addAll(restored);
    }

    private Formatting copyFormatting(Formatting f, int start, int end) {
        if (f instanceof ColorFormatting color) {
            return new ColorFormatting(start, end, color.getColor());
        }
        if (f instanceof StyleFormatting style) {
            return new StyleFormatting(start, end, style.getType());
        }
        if (f instanceof FontFormatting font) {
            return new FontFormatting(start, end, font.getFontId());
        }
        if (f instanceof ShadowColorFormatting shadow) {
            return new ShadowColorFormatting(start, end, shadow.getArgb());
        }
        return null;
    }

    private static int interpolateGradient(List<Integer> stops, float progress) {
        if (stops.size() == 1) {
            return stops.get(0) & 0xFFFFFF;
        }
        float pos = progress * (stops.size() - 1);
        int lower = Math.min((int) pos, stops.size() - 2);
        float frac = pos - lower;
        int from = stops.get(lower) & 0xFFFFFF;
        int to = stops.get(lower + 1) & 0xFFFFFF;
        int r = lerpChannel((from >> 16) & 0xFF, (to >> 16) & 0xFF, frac);
        int g = lerpChannel((from >> 8) & 0xFF, (to >> 8) & 0xFF, frac);
        int b = lerpChannel(from & 0xFF, to & 0xFF, frac);
        return (r << 16) | (g << 8) | b;
    }

    private static int lerpChannel(int from, int to, float frac) {
        return Math.round(from + (to - from) * frac);
    }
}
