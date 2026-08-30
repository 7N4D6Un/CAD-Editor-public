package com.github.rinorsi.cadeditor.client.screen.view.entry;

import com.github.franckyi.guapi.api.node.Node;

import static com.github.franckyi.guapi.api.GuapiHelper.vBox;

public class SpacerEntryView extends EntryView {
    @Override
    protected Node createContent() {
        return vBox().minHeight(16).prefHeight(16);
    }
}