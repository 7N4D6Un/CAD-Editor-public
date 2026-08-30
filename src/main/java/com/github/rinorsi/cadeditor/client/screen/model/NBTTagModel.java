package com.github.rinorsi.cadeditor.client.screen.model;

import com.github.franckyi.databindings.api.BooleanProperty;
import com.github.franckyi.databindings.api.ObjectProperty;
import com.github.franckyi.databindings.api.ObservableList;
import com.github.franckyi.databindings.api.StringProperty;
import com.github.franckyi.guapi.api.mvc.Model;
import com.github.franckyi.guapi.api.node.TreeView;
import com.github.rinorsi.cadeditor.client.context.EditorContext;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.apache.commons.lang3.ArrayUtils;


public class NBTTagModel implements TreeView.TreeItem<NBTTagModel>, Model {
    private final ObservableList<NBTTagModel> children;
    private final BooleanProperty expandedProperty;
    private final ObjectProperty<NBTTagModel> parentProperty;
    private final BooleanProperty childrenChangedProperty;
    private final StringProperty nameProperty;
    private final StringProperty valueProperty;
    private final BooleanProperty validProperty;
    protected final Tag tag;
    protected byte forcedTagType;
    private final EditorContext<?> context;

    public NBTTagModel(EditorContext<?> context, Tag tag) {
        this(context, tag, null, null, null);
        setExpanded(true);
    }

    public NBTTagModel(EditorContext<?> context, byte forcedTagType, NBTTagModel parent, String value) {
        this(context, null, parent, null, value);
        this.forcedTagType = forcedTagType;
    }

    public NBTTagModel(EditorContext<?> context, Tag tag, NBTTagModel parent, String name, String value) {
        this.children = ObservableList.create();
        this.expandedProperty = BooleanProperty.create();
        this.childrenChangedProperty = BooleanProperty.create();
        this.validProperty = BooleanProperty.create();
        this.tag = tag;
        this.context = context != null ? context : parent != null ? parent.getContext() : null;
        this.parentProperty = ObjectProperty.create(parent);
        this.nameProperty = StringProperty.create(name);
        this.valueProperty = StringProperty.create(value);
        if (tag != null) {
            switch (tag.getId()) {
                case 1: // ByteTag
                    setValue(Byte.toString(((ByteTag) tag).byteValue()));
                    break;
                case 2: // ShortTag
                    setValue(Short.toString(((ShortTag) tag).shortValue()));
                    break;
                case 3:
                    setValue(Integer.toString(((IntTag) tag).intValue()));
                    break;
                case 4:
                    setValue(Long.toString(((LongTag) tag).longValue()));
                    break;
                case 5:
                    setValue(Float.toString(((FloatTag) tag).floatValue()));
                    break;
                case 6:
                    setValue(Double.toString(((DoubleTag) tag).doubleValue()));
                    break;
                case 7:
                    this.children.setAll(Stream.of((Object[]) ArrayUtils.toObject(((ByteArrayTag) tag).getAsByteArray())).map(b -> new NBTTagModel(getContext(), (byte) 1, this, Byte.toString(((Byte) b).byteValue()))).toList());
                    break;
                case 8:
                    setValue(((StringTag) tag).value());
                    break;
                case 9:
                    this.children.setAll(((ListTag) tag).stream().map(childTag -> new NBTTagModel(getContext(), childTag, this, null, null)).toList());
                    break;
                case 10:
                    this.children.setAll(((CompoundTag) tag).entrySet().stream().map(entry -> new NBTTagModel(getContext(), (Tag) entry.getValue(), this, (String) entry.getKey(), null)).toList());
                    break;
                case 11:
                    this.children.setAll(Stream.of((Object[]) ArrayUtils.toObject(((IntArrayTag) tag).getAsIntArray())).map(i -> new NBTTagModel(getContext(), (byte) 3, this, Integer.toString(((Integer) i).intValue()))).toList());
                    break;
                case 12:
                    this.children.setAll(Stream.of((Object[]) ArrayUtils.toObject(((LongArrayTag) tag).getAsLongArray())).map(l -> new NBTTagModel(getContext(), (byte) 4, this, Long.toString(((Long) l).longValue()))).toList());
                    break;
                default:
                    setValue(tag.toString());
                    break;
            }
        }
        getChildren().addListener(() -> {
            getRoot().setChildrenChanged(true);
        });
        validProperty().bind(getChildren().allMatch(v -> v.isValid(), v -> v.validProperty()));
    }

    @Override 
    public ObservableList<NBTTagModel> getChildren() {
        return this.children;
    }

    @Override 
    public BooleanProperty expandedProperty() {
        return this.expandedProperty;
    }

    @Override 
    public ObjectProperty<NBTTagModel> parentProperty() {
        return this.parentProperty;
    }

    @Override 
    public BooleanProperty childrenChangedProperty() {
        return this.childrenChangedProperty;
    }

    public EditorContext<?> getContext() {
        return this.context;
    }

    public String getName() {
        return nameProperty().getValue();
    }

    public StringProperty nameProperty() {
        return this.nameProperty;
    }

    public void setName(String value) {
        nameProperty().setValue(value);
    }

    public String getValue() {
        return valueProperty().getValue();
    }

    public StringProperty valueProperty() {
        return this.valueProperty;
    }

    public void setValue(String value) {
        valueProperty().setValue(value);
    }

    public boolean isValid() {
        return validProperty().getValue();
    }

    public BooleanProperty validProperty() {
        return this.validProperty;
    }

    public void setValid(boolean value) {
        validProperty().setValue(value);
    }

    @Override 
    public NBTTagModel getParent() {
        return parentProperty().getValue();
    }

    public byte getTagType() {
        return this.tag != null ? this.tag.getId() : this.forcedTagType;
    }

    public boolean canBuild() {
        return this.tag != null;
    }

    public Tag build() {
        if (canBuild()) {
            return switch (this.tag.getId()) {
                case 1 -> ByteTag.valueOf(Byte.parseByte(getValue()));
                case 2 -> ShortTag.valueOf(Short.parseShort(getValue()));
                case 3 -> IntTag.valueOf(Integer.parseInt(getValue()));
                case 4 -> LongTag.valueOf(Long.parseLong(getValue()));
                case 5 -> FloatTag.valueOf(Float.parseFloat(getValue()));
                case 6 -> DoubleTag.valueOf(Double.parseDouble(getValue()));
                case 7 -> {
                    byte[] bytes = new byte[getChildren().size()];
                    for (int i = 0; i < bytes.length; i++) {
                        bytes[i] = Byte.parseByte(getChildren().get(i).getValue());
                    }
                    yield new ByteArrayTag(bytes);
                }
                case 8 -> StringTag.valueOf(getValue());
                case 9 -> {
                    ListTag listTag = new ListTag();
                    listTag.addAll(getChildren().stream().map(value -> value.build()).toList());
                    yield listTag;
                }
                case 10 -> {
                    CompoundTag compoundTag = new CompoundTag();
                    getChildren().forEach(childTag -> {
                        compoundTag.put(childTag.getName(), childTag.build());
                    });
                    yield compoundTag;
                }
                case 11 -> {
                    int[] ints = new int[getChildren().size()];
                    for (int i = 0; i < ints.length; i++) {
                        ints[i] = Integer.parseInt(getChildren().get(i).getValue());
                    }
                    yield new IntArrayTag(ints);
                }
                case 12 -> {
                    long[] longs = new long[getChildren().size()];
                    for (int i = 0; i < longs.length; i++) {
                        longs[i] = Long.parseLong(getChildren().get(i).getValue());
                    }
                    yield new LongArrayTag(longs);
                }
                default -> null;
            };
        }
        return null;
    }

    public NBTTagModel createClipboardTag() {
        if (canBuild()) {
            return new NBTTagModel(getContext(), build(), null, getName(), getValue());
        }
        return new NBTTagModel(getContext(), getTagType(), null, getValue());
    }

    public List<String> getPath() {
        LinkedList<String> path = new LinkedList<>();
        NBTTagModel parent = this;
        while (true) {
            NBTTagModel current = parent;
            if (current != null) {
                String name = current.getName();
                if (name != null) {
                    path.addFirst(name);
                } else if (current.getParent() != null && current.getParent().getTagType() == 9) {
                    int index = current.getParent().getChildren().indexOf(current);
                    path.addFirst("[" + index + "]");
                }
                parent = current.getParent();
            } else {
                return List.copyOf(path);
            }
        }
    }

    public List<String> getStringSuggestions() {
        EditorContext<?> ctx = getContext();
        if (ctx == null) {
            return List.of();
        }
        return ctx.getStringSuggestions(getPath());
    }
}
