package com.github.rinorsi.cadeditor.client.screen.model;

import com.github.franckyi.databindings.api.BooleanProperty;
import com.github.franckyi.databindings.api.ObjectProperty;
import com.github.franckyi.databindings.api.ObservableList;
import com.github.franckyi.guapi.api.node.TreeView;
import java.util.Map;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagTypes;


public final class SNBTPreviewNode implements TreeView.TreeItem<SNBTPreviewNode> {
    private final ObservableList<SNBTPreviewNode> children = ObservableList.create();
    private final BooleanProperty expandedProperty = BooleanProperty.create(true);
    private final ObjectProperty<SNBTPreviewNode> parentProperty = ObjectProperty.create();
    private final BooleanProperty childrenChangedProperty = BooleanProperty.create();
    private final String label;

    private SNBTPreviewNode(String label) {
        this.label = label;
    }

    public static SNBTPreviewNode fromTag(Tag tag) {
        if (tag == null) {
            return new SNBTPreviewNode("null");
        }
        SNBTPreviewNode root = new SNBTPreviewNode(describe(null, tag, null));
        populate(root, tag);
        return root;
    }

    private static void populate(SNBTPreviewNode node, Tag tag) {
        switch (tag.getId()) {
            case 7:
                byte[] bytes = ((ByteArrayTag) tag).getAsByteArray();
                for (int i = 0; i < bytes.length; i++) {
                    addChild(node, new SNBTPreviewNode("[%d] %d".formatted(i, bytes[i])));
                }
                break;
            case 9:
                ListTag listTag = (ListTag) tag;
                for (int i = 0; i < listTag.size(); i++) {
                    Tag child = listTag.get(i);
                    SNBTPreviewNode childNode = new SNBTPreviewNode(describe(null, child, i));
                    addChild(node, childNode);
                    populate(childNode, child);
                }
                break;
            case 10:
                CompoundTag compound = (CompoundTag) tag;
                for (Map.Entry<String, Tag> entry : compound.entrySet()) {
                    Tag child = entry.getValue();
                    SNBTPreviewNode childNode = new SNBTPreviewNode(describe(entry.getKey(), child, null));
                    addChild(node, childNode);
                    populate(childNode, child);
                }
                break;
            case 11:
                int[] ints = ((IntArrayTag) tag).getAsIntArray();
                for (int i = 0; i < ints.length; i++) {
                    addChild(node, new SNBTPreviewNode("[%d] %d".formatted(i, ints[i])));
                }
                break;
            case 12:
                long[] longs = ((LongArrayTag) tag).getAsLongArray();
                for (int i = 0; i < longs.length; i++) {
                    addChild(node, new SNBTPreviewNode("[%d] %d".formatted(i, longs[i])));
                }
                break;
        }
    }

    private static void addChild(SNBTPreviewNode parent, SNBTPreviewNode child) {
        child.parentProperty().setValue(parent);
        parent.children.add(child);
        parent.childrenChangedProperty().setValue(true);
    }

    private static String describe(String name, Tag tag, Integer listIndex) {
        String prefix = "";
        if (name != null) {
            prefix = name + ": ";
        } else if (listIndex != null) {
            prefix = "[" + listIndex + "]: ";
        }
        if (tag == null) {
            return prefix + "null";
        }
        return switch (tag.getId()) {
            case 7 -> prefix + "ByteArray (" + ((ByteArrayTag) tag).getAsByteArray().length + ")";
            case 8, 0 -> prefix + tag.toString();
            case 9 -> {
                ListTag list = (ListTag) tag;
                byte elementId = list.isEmpty() ? (byte) 0 : list.get(0).getId();
                String type = TagTypes.getType(elementId).getName();
                yield prefix + "List<" + type + "> (" + list.size() + ")";
            }
            case 10 -> prefix + "Compound (" + ((CompoundTag) tag).size() + ")";
            case 11 -> prefix + "IntArray (" + ((IntArrayTag) tag).getAsIntArray().length + ")";
            case 12 -> prefix + "LongArray (" + ((LongArrayTag) tag).getAsLongArray().length + ")";
            default -> prefix + tag.toString();
        };
    }

    public String getLabel() {
        return this.label;
    }

    @Override 
    public ObservableList<SNBTPreviewNode> getChildren() {
        return this.children;
    }

    @Override 
    public BooleanProperty expandedProperty() {
        return this.expandedProperty;
    }

    @Override 
    public ObjectProperty<SNBTPreviewNode> parentProperty() {
        return this.parentProperty;
    }

    @Override 
    public BooleanProperty childrenChangedProperty() {
        return this.childrenChangedProperty;
    }
}
