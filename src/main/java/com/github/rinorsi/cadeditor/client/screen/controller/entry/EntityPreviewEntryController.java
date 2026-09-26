package com.github.rinorsi.cadeditor.client.screen.controller.entry;

import com.github.franckyi.guapi.api.event.MouseButtonEvent;
import com.github.franckyi.guapi.api.node.EntityView;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntityPreviewEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.EntityPreviewEntryView;

public class EntityPreviewEntryController extends EntryController<EntityPreviewEntryModel, EntityPreviewEntryView> {
    public EntityPreviewEntryController(EntityPreviewEntryModel model, EntityPreviewEntryView view) {
        super(model, view);
    }

    @Override
    public void bind() {
        super.bind();
        view.getButtonBox().setVisible(false);
        view.getEntityViewNode().setEntity(model.getEntity());
        model.entityProperty().addListener(view.getEntityViewNode()::setEntity);
        view.getEntityViewNode().onMouseDrag(event -> {
            if (event.getButton() != MouseButtonEvent.LEFT_BUTTON) {
                return;
            }
            EntityView node = view.getEntityViewNode();
            node.setOrbitYaw(node.getOrbitYaw() - event.getDeltaX());
            node.setOrbitPitch(Math.clamp(node.getOrbitPitch() - event.getDeltaY() * 0.75D, -45.0D, 115.0D));
        });
    }
}
