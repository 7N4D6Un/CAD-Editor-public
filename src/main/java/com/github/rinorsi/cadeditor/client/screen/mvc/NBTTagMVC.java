package com.github.rinorsi.cadeditor.client.screen.mvc;

import com.github.franckyi.guapi.api.event.MouseButtonEvent;
import com.github.franckyi.guapi.api.mvc.MVC;
import com.github.franckyi.guapi.api.util.Predicates;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.controller.NBTTagController;
import com.github.rinorsi.cadeditor.client.screen.model.NBTTagModel;
import com.github.rinorsi.cadeditor.client.screen.view.NBTTagView;
import com.github.rinorsi.cadeditor.common.ModTexts;


public final class NBTTagMVC implements MVC<NBTTagModel, NBTTagView, NBTTagController> {
    public static final NBTTagMVC INSTANCE = new NBTTagMVC();

    private NBTTagMVC() {
    }

    @Override 
    public NBTTagView setup(NBTTagModel model) {
        return (NBTTagView) MVC.createViewAndBind(model, () -> {
            return createView(model.getTagType());
        }, NBTTagController::new);
    }

    private NBTTagView createView(byte tagType) {
        return switch (tagType) {
            case 1 -> new NBTTagView(ModTextures.BYTE_TAG, ModTexts.Literal.BYTE, Predicates.IS_BYTE);
            case 2 -> new NBTTagView(ModTextures.SHORT_TAG, ModTexts.Literal.SHORT, Predicates.IS_SHORT);
            case 3 -> new NBTTagView(ModTextures.INT_TAG, ModTexts.Literal.INT, Predicates.IS_INT);
            case 4 -> new NBTTagView(ModTextures.LONG_TAG, ModTexts.Literal.LONG, Predicates.IS_LONG);
            case 5 -> new NBTTagView(ModTextures.FLOAT_TAG, ModTexts.Literal.FLOAT, Predicates.IS_FLOAT);
            case 6 -> new NBTTagView(ModTextures.DOUBLE_TAG, ModTexts.Literal.DOUBLE, Predicates.IS_DOUBLE);
            case 7 -> new NBTTagView(ModTextures.BYTE_ARRAY_TAG, ModTexts.Literal.BYTE_ARRAY);
            case 8 -> new NBTTagView(ModTextures.STRING_TAG, ModTexts.Literal.STRING);
            case 9 -> new NBTTagView(ModTextures.LIST_TAG, ModTexts.Literal.LIST);
            case 10 -> new NBTTagView(ModTextures.COMPOUND_TAG, ModTexts.Literal.COMPOUND);
            case 11 -> new NBTTagView(ModTextures.INT_ARRAY_TAG, ModTexts.Literal.INT_ARRAY);
            case 12 -> new NBTTagView(ModTextures.LONG_ARRAY_TAG, ModTexts.Literal.LONG_ARRAY);
            default -> null;
        };
    }
}
