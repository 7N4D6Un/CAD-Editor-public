package com.github.rinorsi.cadeditor.client.screen.controller.entry.item;

import com.github.franckyi.databindings.api.ObjectProperty;
import com.github.franckyi.guapi.api.node.TextField;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.controller.entry.EntryController;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.MapDecorationEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.item.MapDecorationEntryView;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;


public class MapDecorationEntryController extends EntryController<MapDecorationEntryModel, MapDecorationEntryView> {
    private boolean updating;
    private List<MapDecorationEntryModel.DecorationTypeOption> typeOptions;
    private Map<String, MapDecorationEntryModel.DecorationTypeOption> typeOptionById;

    public MapDecorationEntryController(MapDecorationEntryModel model, MapDecorationEntryView view) {
        super(model, view);
        this.updating = false;
        this.typeOptions = List.of();
        this.typeOptionById = Map.of();
    }

    
    @Override 
    public void bind() {
        super.bind();
        bindTextBidirectional(((MapDecorationEntryView) this.view).getNameField(), ((MapDecorationEntryModel) this.model).nameProperty());
        ((MapDecorationEntryView) this.view).getNameField().setValidator(v -> v == null || v.isBlank() || !v.contains("|"));
        HolderLookup.RegistryLookup<MapDecorationType> decorationLookup = (HolderLookup.RegistryLookup) ClientUtil.registryAccess().lookup(Registries.MAP_DECORATION_TYPE).orElse(null);
        this.typeOptions = MapDecorationEntryModel.DecorationTypeOption.collect(decorationLookup);
        ((MapDecorationEntryView) this.view).setTypeOptions(this.typeOptions);
        this.typeOptions = List.copyOf(((MapDecorationEntryView) this.view).getTypeSelector().getValues());
        this.typeOptionById = (Map) this.typeOptions.stream().collect(LinkedHashMap::new, (map, option) -> {
            map.put(option.getResourceId(), option);
        }, (a, b) -> {
            a.putAll(b);
        });
        ((MapDecorationEntryView) this.view).getTypeSelector().valueProperty().addListener(val -> {
            String id;
            if (this.updating || val == null || (id = val.getResourceId()) == null || id.isBlank()) {
                return;
            }
            this.updating = true;
            ((MapDecorationEntryModel) this.model).typeProperty().setValue(id);
            this.updating = false;
        });
        ((MapDecorationEntryModel) this.model).typeProperty().addListener(value -> {
            MapDecorationEntryModel.DecorationTypeOption option;
            if (!this.updating && value != null && !value.isBlank() && (option = resolveOption(value)) != null && option != ((MapDecorationEntryView) this.view).getTypeSelector().getValue()) {
                this.updating = true;
                ((MapDecorationEntryView) this.view).getTypeSelector().setValue(option);
                this.updating = false;
            }
        });
        ((MapDecorationEntryModel) this.model).typeProperty().addListener(value -> {
            MapDecorationEntryModel.DecorationTypeOption option;
            if (!this.updating && value != null && !value.isBlank() && (option = resolveOption(value)) != null && option != ((MapDecorationEntryView) this.view).getTypeSelector().getValue()) {
                this.updating = true;
                ((MapDecorationEntryView) this.view).getTypeSelector().setValue(option);
                this.updating = false;
            }
        });
        bindTextBidirectional(((MapDecorationEntryView) this.view).getXField(), ((MapDecorationEntryModel) this.model).xTextProperty());
        bindTextBidirectional(((MapDecorationEntryView) this.view).getZField(), ((MapDecorationEntryModel) this.model).zTextProperty());
        bindTextBidirectional(((MapDecorationEntryView) this.view).getRotationField(), ((MapDecorationEntryModel) this.model).rotationTextProperty());
        ((MapDecorationEntryView) this.view).getNameField().setText(((MapDecorationEntryModel) this.model).nameProperty().getValue());
        ((MapDecorationEntryView) this.view).getXField().setText(((MapDecorationEntryModel) this.model).xTextProperty().getValue());
        ((MapDecorationEntryView) this.view).getZField().setText(((MapDecorationEntryModel) this.model).zTextProperty().getValue());
        ((MapDecorationEntryView) this.view).getRotationField().setText(((MapDecorationEntryModel) this.model).rotationTextProperty().getValue());
        MapDecorationEntryModel.DecorationTypeOption initType = null;
        String typeId = ((MapDecorationEntryModel) this.model).typeProperty().getValue();
        if (typeId != null && !typeId.isBlank()) {
            initType = resolveOption(typeId);
        }
        this.updating = true;
        if (initType != null) {
            ((MapDecorationEntryView) this.view).getTypeSelector().setValue(initType);
        } else {
            MapDecorationEntryModel.DecorationTypeOption fallback = ((MapDecorationEntryView) this.view).getTypeSelector().getValue();
            if (fallback == null && !this.typeOptions.isEmpty()) {
                fallback = this.typeOptions.get(0);
                ((MapDecorationEntryView) this.view).getTypeSelector().setValue(fallback);
            }
            if (fallback != null) {
                ((MapDecorationEntryModel) this.model).typeProperty().setValue(fallback.getResourceId());
            } else {
                ((MapDecorationEntryModel) this.model).typeProperty().setValue("");
            }
        }
        this.updating = false;
    }

    private MapDecorationEntryModel.DecorationTypeOption resolveOption(String resourceId) {
        if (resourceId == null || resourceId.isBlank()) {
            return null;
        }
        MapDecorationEntryModel.DecorationTypeOption direct = this.typeOptionById.get(resourceId);
        if (direct != null) {
            return direct;
        }
        Identifier normalized = MapDecorationEntryModel.DecorationTypeOption.normalizeId(resourceId);
        if (normalized == null) {
            return null;
        }
        return this.typeOptionById.get(normalized.toString());
    }

    private void bindTextBidirectional(TextField field, ObjectProperty<String> property) {
        field.textProperty().addListener(property::setValue);
        property.addListener(field::setText);
    }
}
