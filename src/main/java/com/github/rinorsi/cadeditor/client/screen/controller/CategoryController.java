package com.github.rinorsi.cadeditor.client.screen.controller;

import com.github.franckyi.guapi.api.mvc.AbstractController;
import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import com.github.rinorsi.cadeditor.client.screen.view.CategoryView;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public class CategoryController extends AbstractController<CategoryModel, CategoryView> {
    public CategoryController(CategoryModel model, CategoryView view) {
        super(model, view);
    }

    
    @Override 
    public void bind() {
        ((CategoryModel) this.model).selectedProperty().addListener(this::updateLabel);
        ((CategoryModel) this.model).validProperty().addListener(this::updateLabel);
        ((CategoryView) this.view).getLabel().hoveredProperty().addListener(this::updateLabel);
        ((CategoryView) this.view).getLabel().onAction(() -> {
            ((CategoryModel) this.model).getParent().setSelectedCategory((CategoryModel) this.model);
        });
        ((CategoryView) this.view).getLabel().setLabel(((CategoryModel) this.model).getName());
        updateLabel();
    }

    
    private void updateLabel() {
        MutableComponent componentPlainCopy = ((CategoryModel) this.model).getName().plainCopy();
        if (((CategoryView) this.view).getLabel().isHovered()) {
            componentPlainCopy.withStyle(ChatFormatting.UNDERLINE);
        }
        if (((CategoryModel) this.model).isSelected()) {
            componentPlainCopy.withStyle(ChatFormatting.BOLD);
            if (((CategoryModel) this.model).isValid()) {
                componentPlainCopy.withStyle(ChatFormatting.YELLOW);
            } else {
                componentPlainCopy.withStyle(ChatFormatting.RED);
            }
        } else if (!((CategoryModel) this.model).isValid()) {
            componentPlainCopy.withStyle(ChatFormatting.RED);
        }
        ((CategoryView) this.view).getLabel().setLabel(componentPlainCopy);
    }
}
