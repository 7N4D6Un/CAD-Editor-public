package com.github.rinorsi.cadeditor.client.screen.model.selection.element;

import com.github.franckyi.databindings.api.BooleanProperty;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class SelectableSpriteListSelectionElementModel extends SpriteListSelectionElementModel implements SelectableListSelectionElementModel {
    private final BooleanProperty selectedProperty = BooleanProperty.create(false);

    public SelectableSpriteListSelectionElementModel(String name, Identifier id, Supplier<TextureAtlasSprite> spriteFactory) {
        super(name, id, spriteFactory);
    }

    @Override
    public BooleanProperty selectedProperty() {
        return selectedProperty;
    }
}
