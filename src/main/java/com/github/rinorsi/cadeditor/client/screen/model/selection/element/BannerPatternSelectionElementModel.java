package com.github.rinorsi.cadeditor.client.screen.model.selection.element;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.function.Supplier;

public class BannerPatternSelectionElementModel extends ItemListSelectionElementModel {
    private final String translationKey;

    public BannerPatternSelectionElementModel(String translationKey, Identifier id, ItemStack icon) {
        super(translationKey, id, icon);
        this.translationKey = translationKey;
    }

    public BannerPatternSelectionElementModel(String translationKey, Identifier id, Supplier<ItemStack> iconSupplier) {
        super(translationKey, id, iconSupplier);
        this.translationKey = translationKey;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    @Override
    public boolean matches(String query) {
        if (query == null || query.isEmpty()) {
            return true;
        }
        if (super.matches(query)) {
            return true;
        }
        String lower = query.toLowerCase(Locale.ROOT);
        return I18n.get(translationKey).toLowerCase(Locale.ROOT).contains(lower);
    }
}