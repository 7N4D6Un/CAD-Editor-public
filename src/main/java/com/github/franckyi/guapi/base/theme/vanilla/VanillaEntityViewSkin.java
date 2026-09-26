package com.github.franckyi.guapi.base.theme.vanilla;

import com.github.franckyi.guapi.api.node.EntityView;
import com.github.franckyi.guapi.api.theme.Skin;
import com.github.franckyi.guapi.base.theme.AbstractSkin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VanillaEntityViewSkin extends AbstractSkin<EntityView> {
    public static final Skin<EntityView> INSTANCE = new VanillaEntityViewSkin();

    private VanillaEntityViewSkin() {
    }

    @Override
    public void render(EntityView node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.render(node, guiGraphicsExtractor, mouseX, mouseY, delta);
        Entity entity = node.getEntity();
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> renderer = entityRenderDispatcher.getRenderer(livingEntity);
        EntityRenderState renderState = renderer.createRenderState(livingEntity, 1.0f);
        renderState.shadowPieces.clear();
        renderState.outlineColor = 0;
        if (renderState.boundingBoxHeight <= 0.0f) {
            return;
        }
        float yaw = (float) node.getOrbitYaw();
        float pitch = (float) node.getOrbitPitch();
        if (renderState instanceof LivingEntityRenderState livingRenderState) {
            livingRenderState.bodyRot = 180.0f + yaw;
            livingRenderState.yRot = yaw;
            livingRenderState.xRot = livingRenderState.pose != Pose.FALL_FLYING ? -pitch : 0.0f;
            livingRenderState.boundingBoxWidth /= livingRenderState.scale;
            livingRenderState.boundingBoxHeight /= livingRenderState.scale;
            livingRenderState.scale = 1.0f;
        }
        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf pitchRotation = new Quaternionf().rotateX((float) Math.toRadians(pitch));
        rotation.mul(pitchRotation);
        float scale = (float) node.getSize();
        Vector3f translation;
        if (renderState.nameTag != null && renderState.nameTagAttachment != null) {
            float top = (float) renderState.nameTagAttachment.y + 0.75f;
            translation = new Vector3f(0.0f, top / 2.0f, 0.0f);
        } else {
            translation = new Vector3f(0.0f, renderState.boundingBoxHeight / 2.0f, 0.0f);
        }
        guiGraphicsExtractor.entity(renderState, scale, translation, rotation, pitchRotation,
                node.getX(), node.getY(), node.getX() + node.getWidth(), node.getY() + node.getHeight());
    }

    @Override
    public int computeWidth(EntityView node) {
        return (int) (node.getSize() * 1.7);
    }

    @Override
    public int computeHeight(EntityView node) {
        return (int) (node.getSize() * 2.4);
    }
}
