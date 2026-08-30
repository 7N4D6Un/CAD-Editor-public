package com.github.rinorsi.cadeditor.client.screen.model.selection.element;

import com.github.franckyi.databindings.api.BooleanProperty;
import com.github.franckyi.guapi.api.mvc.Model;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Locale;

public class ListSelectionElementModel implements Model, SelectableListSelectionElementModel, Comparable<ListSelectionElementModel> {
    private static final Comparator<ListSelectionElementModel> COMPARATOR = Comparator.comparing(ListSelectionElementModel::getName);
    private final String name;
    private final Identifier id;
    private final BooleanProperty selectedProperty = BooleanProperty.create(false);
    private boolean multiSelect;

    public ListSelectionElementModel(String name, Identifier id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Component getDisplayName() {
        MutableComponent component = Component.translatable(getName()).withStyle(ChatFormatting.BOLD);
        if (name.startsWith("cadeditor.gui.rarity.")) {
            ChatFormatting color = switch (id.getPath()) {
                case "uncommon" -> ChatFormatting.YELLOW;
                case "rare" -> ChatFormatting.AQUA;
                case "epic" -> ChatFormatting.LIGHT_PURPLE;
                default -> ChatFormatting.WHITE;
            };
            component = component.copy().withStyle(color);
        }
        return component;
    }

    public Identifier getId() {
        return id;
    }

    public boolean matches(String s) {
        if (s.isEmpty()) {
            return true;
        }
        String lower = s.toLowerCase(Locale.ROOT);
        return id.toString().toLowerCase(Locale.ROOT).contains(lower) || I18n.get(name).toLowerCase(Locale.ROOT).contains(lower);
    }

    public Type getType() {
        return Type.DEFAULT;
    }

    @Override
    public BooleanProperty selectedProperty() {
        return selectedProperty;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    @Override
    public int compareTo(@NotNull ListSelectionElementModel o) {
        return COMPARATOR.compare(this, o);
    }

    public enum Type {
        DEFAULT, ITEM, IMAGE, SPRITE, ENCHANTMENT, TAG, SOUND
    }
}
