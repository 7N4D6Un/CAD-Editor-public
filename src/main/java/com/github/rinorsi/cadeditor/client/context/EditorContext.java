package com.github.rinorsi.cadeditor.client.context;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public abstract class EditorContext<T extends EditorContext<T>> {
    private CompoundTag tag;
    private final CompoundTag originalTag;
    protected Component errorTooltip;
    protected boolean canSaveToVault;
    private boolean isSaveToVault = false;

    private boolean isCopyCommand = false;
    private boolean isApplyVanillaCommand = false;
    private final Consumer<T> action;

    public EditorContext(CompoundTag tag, Component errorTooltip, boolean canSaveToVault, Consumer<T> action) {
        this.tag = tag;
        this.originalTag = tag == null ? null : tag.copy();
        this.errorTooltip = errorTooltip;
        this.canSaveToVault = canSaveToVault;
        this.action = action;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public void setTag(CompoundTag tag) {
        this.tag = tag;
    }

    public Component getErrorTooltip() {
        return errorTooltip;
    }

    public boolean hasPermission() {
        return errorTooltip == null;
    }

    @SuppressWarnings("unchecked")
    public void update() {
        if (isSaveToVault() && canSaveToVault()) {
            saveToVault();
        }
        if (isCopyCommand()) {
            Minecraft.getInstance().keyboardHandler.setClipboard(getCommand());
            ClientUtil.showMessage(getCopySuccessMessage());
        }
        if (isApplyVanillaCommand()) {
            applyVanillaCommand();
        }
        if (hasPermission() && action != null && !isUnchanged()) {
            action.accept((T) this);
        }
    }

    protected boolean isUnchanged() {
        return originalTag != null && originalTag.equals(getTag());
    }

    public boolean canSaveToVault() {
        return canSaveToVault;
    }

    public boolean isSaveToVault() {
        return isSaveToVault;
    }

    public void setSaveToVault(boolean isSaveToVault) {
        this.isSaveToVault = isSaveToVault;
    }

    public boolean isCopyCommand() {
        return isCopyCommand;
    }

    public void setCopyCommand(boolean copyCommand) {
        isCopyCommand = copyCommand;
    }

    public boolean isApplyVanillaCommand() {
        return isApplyVanillaCommand;
    }

    public void setApplyVanillaCommand(boolean applyVanillaCommand) {
        isApplyVanillaCommand = applyVanillaCommand;
    }

    protected void applyVanillaCommand() {
    }

    protected CompoundTag buildChangedTag(Set<String> excludedKeys) {
        CompoundTag changed = new CompoundTag();
        CompoundTag current = getTag();
        if (current == null) {
            return changed;
        }
        for (String key : current.keySet()) {
            if (excludedKeys.contains(key)) {
                continue;
            }
            Tag value = current.get(key);
            Tag originalValue = originalTag == null ? null : originalTag.get(key);
            if (originalValue == null || !originalValue.equals(value)) {
                changed.put(key, value);
            }
        }
        return changed;
    }

    protected List<String> findRemovedKeys(Set<String> excludedKeys) {
        List<String> removed = new ArrayList<>();
        CompoundTag current = getTag();
        if (originalTag == null || current == null) {
            return removed;
        }
        for (String key : originalTag.keySet()) {
            if (!excludedKeys.contains(key) && !current.contains(key)) {
                removed.add(key);
            }
        }
        return removed;
    }

    protected static String quoteKey(String key) {
        return "\"" + key.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    protected void sendVanillaCommand(String command) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) {
            return;
        }
        connection.sendCommand(command.startsWith("/") ? command.substring(1) : command);
    }

    public void saveToVault() {
    }

    public abstract MutableComponent getTargetName();

    public MutableComponent getCommandTooltip() {
        return ModTexts.copyCommand(getCommandName());
    }

    public abstract String getCommandName();

    protected abstract String getCommand();

    protected MutableComponent getCopySuccessMessage() {
        return ModTexts.Messages.successCopyClipboard(getCommandName());
    }

    public List<String> getStringSuggestions(List<String> path) {
        if (path == null || path.isEmpty()) {
            return List.of();
        }
        String key = lastKey(path);
        if (key == null) {
            return List.of();
        }
        if (equalsAnyIgnoreCase(key, "LootTable", "LootTableId", "loot_table", "loot_table_id")) {
            return ClientCache.getLootTableSuggestions();
        }
        return List.of();
    }

    protected static String lastKey(List<String> path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        return path.get(path.size() - 1);
    }

    protected static String previousNamedKey(List<String> path, int depth) {
        if (path == null || path.isEmpty() || depth <= 0) {
            return null;
        }
        int remaining = depth;
        for (int i = path.size() - 2; i >= 0; i--) {
            String candidate = path.get(i);
            if (isIndexElement(candidate)) {
                continue;
            }
            remaining--;
            if (remaining == 0) {
                return candidate;
            }
        }
        return null;
    }

    protected static boolean pathContains(List<String> path, String key) {
        if (path == null || key == null) {
            return false;
        }
        for (String element : path) {
            if (isIndexElement(element)) {
                continue;
            }
            if (key.equals(element)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean equalsAnyIgnoreCase(String value, String... candidates) {
        if (value == null) {
            return false;
        }
        for (String candidate : candidates) {
            if (candidate != null && value.equalsIgnoreCase(candidate)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isIndexElement(String value) {
        return value != null && value.startsWith("[") && value.endsWith("]");
    }
}
