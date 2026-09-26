package com.github.rinorsi.cadeditor.client.screen.view.entry;

import com.github.franckyi.guapi.api.node.EntityView;
import com.github.franckyi.guapi.api.node.Node;

import static com.github.franckyi.guapi.api.GuapiHelper.entityView;

public class EntityPreviewEntryView extends EntryView {
    private EntityView entityViewNode;

    @Override
    protected Node createContent() {
        return entityViewNode = entityView().size(40).prefHeight(116);
    }

    public EntityView getEntityViewNode() {
        return entityViewNode;
    }
}
