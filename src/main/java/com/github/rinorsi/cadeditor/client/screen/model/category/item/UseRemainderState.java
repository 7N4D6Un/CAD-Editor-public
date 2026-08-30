package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.debug.DebugLog;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.UseRemainder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class UseRemainderState {
    private static final int MAX_CONVERT_COUNT = 64;
    private static final Logger LOGGER = LogManager.getLogger();
    private String usingConvertsToId = "";
    private Optional<ItemStack> originalUsingConvertsTo = Optional.empty();
    private Optional<ItemStack> customUsingConvertsTo = Optional.empty();

    public void loadFrom(ItemStack stack) {
        UseRemainder remainder = (UseRemainder) stack.get(DataComponents.USE_REMAINDER);
        if (remainder != null) {
            ItemStack convert = remainder.convertInto().create();
            this.originalUsingConvertsTo = Optional.of(convert);
            this.customUsingConvertsTo = this.originalUsingConvertsTo.map(value -> value.copy());
            this.usingConvertsToId = BuiltInRegistries.ITEM.getKey(convert.getItem()).toString();
        } else {
            this.originalUsingConvertsTo = Optional.empty();
            this.customUsingConvertsTo = Optional.empty();
            this.usingConvertsToId = "";
        }
        DebugLog.infoKey("cadeditor.debug.food.loaded", describeStack(stack), this.usingConvertsToId, this.originalUsingConvertsTo.map(this::describeStack).orElse("<empty>"));
    }

    public boolean hasRemainder() {
        return !this.usingConvertsToId.isBlank();
    }

    public String getUsingConvertsToId() {
        return this.usingConvertsToId;
    }

    public void setUsingConvertsToId(String id) {
        this.usingConvertsToId = id == null ? "" : id.trim();
        if (this.usingConvertsToId.isBlank()) {
            this.customUsingConvertsTo = Optional.empty();
            this.originalUsingConvertsTo = Optional.empty();
        } else {
            this.customUsingConvertsTo = filterStackById(this.customUsingConvertsTo, this.usingConvertsToId);
            this.originalUsingConvertsTo = filterStackById(this.originalUsingConvertsTo, this.usingConvertsToId);
        }
    }

    public Optional<ItemStack> resolveUsingConvertsTo() {
        if (this.usingConvertsToId.isBlank()) {
            DebugLog.infoKey("cadeditor.debug.food.convert_blank", new Object[0]);
            return Optional.empty();
        }
        Identifier rl = Identifier.tryParse(this.usingConvertsToId);
        if (rl == null) {
            LOGGER.warn("Failed to parse using_converts_to id '{}'", this.usingConvertsToId);
            return Optional.empty();
        }
        Item item = (Item) BuiltInRegistries.ITEM.getOptional(rl).orElse(null);
        if (item == null) {
            LOGGER.warn("Unknown using_converts_to item id '{}'", this.usingConvertsToId);
            return Optional.empty();
        }
        Optional<ItemStack> custom = this.customUsingConvertsTo.filter(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(rl)).map(value -> value.copy());
        if (custom.isPresent()) {
            DebugLog.infoKey("cadeditor.debug.food.convert_reuse", describeStack(custom.get()));
            return custom;
        }
        Optional<ItemStack> original = this.originalUsingConvertsTo.filter(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(rl)).map(value -> value.copy());
        if (original.isPresent()) {
            DebugLog.infoKey("cadeditor.debug.food.convert_reuse", describeStack(original.get()));
            return original;
        }
        ItemStack result = item.getDefaultInstance();
        if (result.isEmpty()) {
            result = new ItemStack(item);
        }
        ItemStack prepared = prepareConvertStack(result);
        DebugLog.infoKey("cadeditor.debug.food.convert_new", this.usingConvertsToId, describeStack(prepared));
        return Optional.of(prepared);
    }

    public Optional<UseRemainder> buildUseRemainder(Optional<ItemStack> convertStack) {
        return convertStack.map(stack -> new UseRemainder(ItemStackTemplate.fromNonEmptyStack(stack)));
    }

    public void updateOriginalUsingConvertsTo(Optional<ItemStack> stack) {
        this.originalUsingConvertsTo = stack.filter(s -> !s.isEmpty()).map(this::prepareConvertStack);
        DebugLog.infoKey("cadeditor.debug.food.convert_update", this.originalUsingConvertsTo.map(this::describeStack).orElse("<empty>"));
    }

    public Optional<ItemStack> getUsingConvertsToPreview() {
        return this.customUsingConvertsTo.filter(stack -> !stack.isEmpty()).or(() -> this.originalUsingConvertsTo.filter(stack -> !stack.isEmpty())).map(value -> value.copy());
    }

    public Optional<ItemStack> getUsingConvertsToEditorStack() {
        return this.customUsingConvertsTo.filter(stack -> !stack.isEmpty()).or(() -> this.originalUsingConvertsTo.filter(stack -> !stack.isEmpty())).map(this::prepareConvertStack).map(value -> value.copy());
    }

    public void useCustomUsingConvertsTo(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            this.customUsingConvertsTo = Optional.empty();
            setUsingConvertsToId("");
            return;
        }
        ItemStack sanitized = prepareConvertStack(stack);
        this.customUsingConvertsTo = Optional.of(sanitized);
        Identifier id = BuiltInRegistries.ITEM.getKey(sanitized.getItem());
        this.usingConvertsToId = id != null ? id.toString() : "";
        this.originalUsingConvertsTo = filterStackById(this.originalUsingConvertsTo, this.usingConvertsToId);
    }

    private ItemStack prepareConvertStack(ItemStack source) {
        if (source == null || source.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack sanitized = source.copy();
        int count = sanitized.getCount();
        if (count < 1 || count > MAX_CONVERT_COUNT) {
            count = Math.min(Math.max(count, 1), MAX_CONVERT_COUNT);
        }
        sanitized.setCount(count);
        return sanitized;
    }

    private Optional<ItemStack> filterStackById(Optional<ItemStack> stack, String id) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        return stack.filter(s -> BuiltInRegistries.ITEM.getKey(s.getItem()).toString().equals(id));
    }

    private String describeStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return "<empty>";
        }
        return BuiltInRegistries.ITEM.getKey(stack.getItem()) + " x" + stack.getCount();
    }
}
