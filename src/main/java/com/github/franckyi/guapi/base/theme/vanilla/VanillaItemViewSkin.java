package com.github.franckyi.guapi.base.theme.vanilla;

import com.github.franckyi.guapi.api.RenderHelper;
import com.github.franckyi.guapi.api.node.ItemView;
import com.github.franckyi.guapi.api.theme.Skin;
import com.github.franckyi.guapi.base.theme.AbstractSkin;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public class VanillaItemViewSkin extends AbstractSkin<ItemView> {
    public static final Skin<ItemView> INSTANCE = new VanillaItemViewSkin();

    private VanillaItemViewSkin() {
    }

    @Override
    public void render(ItemView node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.render(node, guiGraphicsExtractor, mouseX, mouseY, delta);
        ItemStack item = node.getItem();
        if (item != null) {
            int x = node.getX() + ((node.getWidth() - node.getComputedWidth()) / 2);
            int y = node.getY() + ((node.getHeight() - node.getComputedHeight()) / 2);
            RenderHelper.drawItem(guiGraphicsExtractor, item, x, y);
            if (node.isDrawDecorations()) {
                RenderHelper.drawItemDecorations(guiGraphicsExtractor, item, x, y);
            }
        }
    }

    @Override
    public void postRender(ItemView node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        if (node.isHovered() && node.getItem() != null && node.isDrawTooltip()) {
            RenderHelper.drawTooltip(guiGraphicsExtractor, node.getItem(), mouseX, mouseY);
        }
        super.postRender(node, guiGraphicsExtractor, mouseX, mouseY, delta);
    }

    @Override
    public int computeWidth(ItemView node) {
        return 16;
    }

    @Override
    public int computeHeight(ItemView node) {
        return 16;
    }
}
