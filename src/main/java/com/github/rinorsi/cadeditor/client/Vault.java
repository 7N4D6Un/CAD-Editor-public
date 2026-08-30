package com.github.rinorsi.cadeditor.client;

import com.github.franckyi.guapi.api.Color;
import com.github.rinorsi.cadeditor.PlatformUtil;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Vault {
    private static final int MAGIC = 1128350806;

    private static final int MAX_BROKEN_BACKUPS = 5;
    private static Vault INSTANCE;
    private int version = 1;
    private List<CompoundTag> items = new ArrayList<>();
    private List<CompoundTag> entities = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Path VAULT_FILE_OLD = PlatformUtil.getConfigDir().resolve("ibeeditor-clipboard.dat");
    private static final Path VAULT_FILE_LEGACY = PlatformUtil.getConfigDir().resolve("ibeeditor-vault.dat");
    private static final Path VAULT_FILE_COMPAT = PlatformUtil.getConfigDir().resolve("cadeditor-vault.dat");
    private static final Path VAULT_DIR = PlatformUtil.getConfigDir().resolve("cadeditor");
    private static final Path VAULT_FILE = VAULT_DIR.resolve("vault.dat");
    private static final Path VAULT_FILE_TMP = VAULT_DIR.resolve("vault.dat.tmp");

    private enum LoadResult {
        SUCCESS,
        RECOVERED,
        FAILED
    }

    private Vault() {
    }

    public List<CompoundTag> getItems() {
        return snapshotOf(this.items);
    }

    public List<CompoundTag> getEntities() {
        return snapshotOf(this.entities);
    }

    public boolean saveItem(CompoundTag tag) {
        CompoundTag sanitized = sanitizeTag(tag, "items", this.items.size());
        if (sanitized == null || containsEquivalent(this.items, sanitized)) {
            return false;
        }
        this.items.add(sanitized);
        save();
        return true;
    }

    public boolean saveEntity(CompoundTag tag) {
        CompoundTag sanitized = sanitizeTag(tag, "entities", this.entities.size());
        if (sanitized == null || containsEquivalent(this.entities, sanitized)) {
            return false;
        }
        this.entities.add(sanitized);
        save();
        return true;
    }

    public void clear() {
        this.items.clear();
        this.entities.clear();
    }

    public static synchronized void load() {
        if (Files.exists(VAULT_FILE)) {
            loadFromFile(VAULT_FILE);
            return;
        }
        if (Files.exists(VAULT_FILE_COMPAT)) {
            LOGGER.info("检测到旧路径保险库文件，正在迁移");
            loadFromFile(VAULT_FILE_COMPAT);
            save();
            try {
                Files.deleteIfExists(VAULT_FILE_COMPAT);
                return;
            } catch (IOException e) {
                LOGGER.error("删除旧路径保险库文件时出错", e);
                return;
            }
        }
        if (Files.exists(VAULT_FILE_LEGACY)) {
            LOGGER.info("检测到旧保险库文件，正在迁移");
            loadFromFile(VAULT_FILE_LEGACY);
            save();
            try {
                Files.deleteIfExists(VAULT_FILE_LEGACY);
                return;
            } catch (IOException e) {
                LOGGER.error("删除旧保险库文件时出错", e);
                return;
            }
        }
        if (Files.exists(VAULT_FILE_OLD)) {
            LOGGER.info("检测到旧剪贴板文件，正在转换为保险库文件");
            loadFromFile(VAULT_FILE_OLD);
            try {
                Files.delete(VAULT_FILE_OLD);
            } catch (IOException e) {
                LOGGER.error("删除旧剪贴板文件时出错", e);
            }
            INSTANCE.version = 0;
            save();
            return;
        }
        LOGGER.info("生成空保险库");
        INSTANCE = new Vault();
        save();
    }

    private static void loadFromFile(Path path) {
        INSTANCE = new Vault();
        try {
            byte[] raw = Files.readAllBytes(path);
            if (raw.length == 0) {
                throw new VaultFileFormatException("文件为空");
            }
            LoadResult result = tryReadVaultBytes(raw);
            if (result == LoadResult.SUCCESS) {
                LOGGER.info("保险库已加载");
            } else if (result == LoadResult.RECOVERED) {
                LOGGER.warn("保险库部分损坏，已恢复可读取数据（items={}, entities={}）", INSTANCE.items.size(), INSTANCE.entities.size());
                backupBrokenVault(path);
                save();
            } else {
                throw new VaultFileFormatException("无法识别的保险库格式");
            }
        } catch (VaultFileFormatException e) {
            handleCorruptedVault(path, e);
        } catch (IOException | RuntimeException e) {
            handleCorruptedVault(path, e);
        }
    }

    private static LoadResult tryReadVaultBytes(byte[] raw) throws VaultFileFormatException {
        FriendlyByteBuf readBuffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(raw));
        try {
            int first = readBuffer.readableBytes() >= 4 ? readBuffer.getInt(0) : Color.NONE;
            if (first == MAGIC) {
                LoadResult currentFormat = readCurrentFormat(readBuffer);
                readBuffer.release();
                return currentFormat;
            }
            if (raw.length < 12 || first < 0 || first > 0) {
                readBuffer.release();
                return tryReadNbtFallback(raw) ? LoadResult.RECOVERED : LoadResult.FAILED;
            }
            LoadResult legacyVersionedFormat = readLegacyVersionedFormat(readBuffer);
            readBuffer.release();
            return legacyVersionedFormat;
        } catch (Throwable th) {
            readBuffer.release();
            throw th;
        }
    }

    private static LoadResult readCurrentFormat(FriendlyByteBuf readBuffer) throws VaultFileFormatException {
        readBuffer.readerIndex(0);
        if (readBuffer.readableBytes() < 8) {
            throw new VaultFileFormatException("缺少文件头");
        }
        int magic = readBuffer.readInt();
        if (magic != MAGIC) {
            throw new VaultFileFormatException("无效的保险库文件头");
        }
        int version = readBuffer.readInt();
        if (version < 0 || version > 1) {
            throw new VaultFileFormatException("不支持的版本：" + version);
        }
        INSTANCE.version = version;
        return readPayload(readBuffer);
    }

    private static LoadResult readLegacyVersionedFormat(FriendlyByteBuf readBuffer) throws VaultFileFormatException {
        readBuffer.readerIndex(0);
        if (readBuffer.readableBytes() < 12) {
            throw new VaultFileFormatException("旧格式头不完整");
        }
        int version = readBuffer.readInt();
        if (version < 0 || version > 0) {
            throw new VaultFileFormatException("不支持的旧版本：" + version);
        }
        INSTANCE.version = 1;
        return readPayload(readBuffer);
    }

    private static LoadResult readPayload(FriendlyByteBuf readBuffer) throws VaultFileFormatException {
        int itemCount = readSize(readBuffer, "items");
        boolean recovered = readEntries(readBuffer, "items", itemCount, INSTANCE.items);
        if (recovered) {
            int entityCount = readSize(readBuffer, "entities");
            recovered = readEntries(readBuffer, "entities", entityCount, INSTANCE.entities);
        }
        if (recovered && readBuffer.readableBytes() > 0) {
            LOGGER.warn("保险库存在多余尾部字节：{}，将重写为干净格式", readBuffer.readableBytes());
            recovered = false;
        }
        return recovered ? LoadResult.SUCCESS : LoadResult.RECOVERED;
    }

    private static boolean readEntries(FriendlyByteBuf readBuffer, String section, int expected, List<CompoundTag> output) {
        for (int i = 0; i < expected; i++) {
            try {
                CompoundTag tag = safeReadNbt(readBuffer);
                CompoundTag sanitized = sanitizeTag(tag, section, i);
                if (sanitized != null) {
                    output.add(sanitized);
                } else {
                    return false;
                }
            } catch (Exception e) {
                LOGGER.warn("读取保险库 {}[{}] 失败，后续条目将跳过", section, i, e);
                return false;
            }
        }
        return true;
    }

    private static void handleCorruptedVault(Path sourcePath, Exception exception) {
        LOGGER.error("读取保险库失败，文件可能已损坏：{}", sourcePath, exception);
        backupBrokenVault(sourcePath);
        INSTANCE = new Vault();
        save();
    }

    private static void backupBrokenVault(Path sourcePath) {
        try {
            if (!Files.exists(sourcePath)) {
                return;
            }
            String backupName = sourcePath.getFileName() + ".broken-" + System.currentTimeMillis();
            Path backupPath = sourcePath.resolveSibling(backupName);
            Files.move(sourcePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.warn("已备份损坏的保险库文件到 {}", backupPath);
            cleanupExcessBrokenBackups(sourcePath);
        } catch (IOException e) {
            LOGGER.warn("备份损坏保险库文件失败：{}", sourcePath, e);
        }
    }

    private static void cleanupExcessBrokenBackups(Path sourcePath) {
        Path dir = sourcePath.getParent();
        if (dir == null || !Files.isDirectory(dir)) {
            return;
        }
        String brokenPrefix = sourcePath.getFileName() + ".broken";
        try (Stream<Path> stream = Files.list(dir)) {
            List<Path> backups = stream.filter(Files::isRegularFile).filter(path -> {
                String name = path.getFileName().toString();
                return name.equals(brokenPrefix) || name.startsWith(brokenPrefix + "-");
            }).sorted(Comparator.comparingLong(Vault::safeLastModifiedTime).reversed()).toList();
            for (int i = MAX_BROKEN_BACKUPS; i < backups.size(); i++) {
                try {
                    Files.deleteIfExists(backups.get(i));
                } catch (IOException ignored) {
                }
            }
        } catch (IOException e) {
            LOGGER.warn("清理多余备份文件失败", e);
        }
    }

    private static long safeLastModifiedTime(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            return 0L;
        }
    }

    private static int readSize(FriendlyByteBuf readBuffer, String label) throws VaultFileFormatException {
        if (readBuffer.readableBytes() < 4) {
            throw new VaultFileFormatException(label + " 数量缺失");
        }
        int size = readBuffer.readInt();
        if (size < 0) {
            throw new VaultFileFormatException(label + " 数量为负数：" + size);
        }
        return size;
    }

    private static CompoundTag safeReadNbt(FriendlyByteBuf readBuffer) throws Exception {
        int i = readBuffer.readerIndex();
        try {
            CompoundTag result = readBuffer.readNbt();
            if (result == null || result.isEmpty()) {
                throw new RuntimeException("Tag is empty, this must be an error");
            }
            return result;
        } catch (Exception e) {
            if (i >= readBuffer.writerIndex()) {
                throw e;
            }
            byte firstByte = readBuffer.getByte(i);
            if (firstByte != 10) {
                throw e;
            }
            readBuffer.readerIndex(i);
            try {
                Tag anyTag = NbtIo.readAnyTag(new ByteBufInputStream(readBuffer), NbtAccounter.unlimitedHeap());
                if (anyTag instanceof CompoundTag compound) {
                    return compound;
                }
                throw new RuntimeException("Expected CompoundTag but got " + anyTag.getClass().getSimpleName());
            } catch (IOException io) {
                throw new RuntimeException(io);
            }
        }
    }

    public static synchronized void save() {
        if (INSTANCE == null) {
            INSTANCE = new Vault();
        }
        INSTANCE.version = 1;
        INSTANCE.items = sanitizeSnapshot(INSTANCE.items, "items");
        INSTANCE.entities = sanitizeSnapshot(INSTANCE.entities, "entities");
        FriendlyByteBuf writeBuffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            Files.createDirectories(VAULT_DIR);
        } catch (IOException e) {
            LOGGER.error("创建保险库目录失败", e);
            writeBuffer.release();
            return;
        }
        boolean saved = false;
        try (OutputStream os = Files.newOutputStream(VAULT_FILE_TMP)) {
            writeBuffer.writeInt(MAGIC);
            writeBuffer.writeInt(INSTANCE.version);
            writeBuffer.writeInt(INSTANCE.items.size());
            INSTANCE.items.forEach(writeBuffer::writeNbt);
            writeBuffer.writeInt(INSTANCE.entities.size());
            INSTANCE.entities.forEach(writeBuffer::writeNbt);
            writeBuffer.readerIndex(0);
            writeBuffer.readBytes(os, writeBuffer.readableBytes());
            moveAtomicallyOrReplace(VAULT_FILE_TMP, VAULT_FILE);
            saved = true;
            LOGGER.info("保险库已保存");
        } catch (IOException | RuntimeException e) {
            LOGGER.error("保存保险库时出错", e);
        } finally {
            writeBuffer.release();
        }
        if (!saved) {
            try {
                Files.deleteIfExists(VAULT_FILE_TMP);
            } catch (IOException ignored) {
            }
        }
    }

    private static void moveAtomicallyOrReplace(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static boolean tryReadNbtFallback(byte[] raw) {
        FriendlyByteBuf readBuffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(raw));
        try {
            CompoundTag root = safeReadNbt(readBuffer);
            if (root == null) {
                return false;
            }
            if (readBuffer.readableBytes() > 0) {
                return false;
            }
            boolean hasAny = false | appendCompoundList(root, "items", INSTANCE.items) | appendCompoundList(root, "entities", INSTANCE.entities);
            if (!hasAny) {
                CompoundTag single = sanitizeTag(root, "items", 0);
                if (single == null) {
                    return false;
                }
                INSTANCE.items.add(single);
                hasAny = true;
            }
            INSTANCE.version = 1;
            if (hasAny) {
                LOGGER.warn("检测到非标准保险库格式，已按兼容模式恢复并升级");
            }
            return hasAny;
        } catch (Exception e) {
            return false;
        } finally {
            readBuffer.release();
        }
    }

    private static boolean appendCompoundList(CompoundTag root, String key, List<CompoundTag> target) {
        ListTag list;
        if (!root.contains(key) || (list = root.getList(key).orElse(null)) == null) {
            return false;
        }
        if (list.isEmpty()) {
            return true;
        }
        boolean any = false;
        for (int i = 0; i < list.size(); i++) {
            CompoundTag compoundTag = (CompoundTag) list.get(i);
            if (compoundTag instanceof CompoundTag) {
                CompoundTag compound = compoundTag;
                CompoundTag sanitized = sanitizeTag(compound, key, i);
                if (sanitized != null) {
                    target.add(sanitized);
                    any = true;
                }
            }
        }
        return any;
    }

    private static CompoundTag sanitizeTag(CompoundTag tag, String section, int index) {
        if (tag == null) {
            LOGGER.warn("跳过空 NBT：{}[{}]", section, index);
            return null;
        }
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream(256);
            DataOutputStream out = new DataOutputStream(bytes);
            try {
                NbtIo.writeAnyTag(tag.copy(), out);
                out.close();
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()));
                try {
                    if (NbtIo.readAnyTag(in, NbtAccounter.unlimitedHeap()) instanceof CompoundTag roundTrip && !roundTrip.isEmpty()) {
                        return roundTrip;
                    }
                    LOGGER.warn("跳过无效 NBT：{}[{}]（空或无法解码）", section, index);
                    return null;
                } finally {
                    in.close();
                }
            } finally {
                out.close();
            }
        } catch (Exception e) {
            LOGGER.warn("跳过无效 NBT：{}[{}]", section, index, e);
            return null;
        }
    }

    private static List<CompoundTag> sanitizeSnapshot(List<CompoundTag> source, String section) {
        List<CompoundTag> sanitized = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            CompoundTag tag = sanitizeTag(source.get(i), section, i);
            if (tag != null && !containsEquivalent(sanitized, tag)) {
                sanitized.add(tag);
            }
        }
        return sanitized;
    }

    private static List<CompoundTag> snapshotOf(List<CompoundTag> source) {
        return source.stream().map(value -> value.copy()).toList();
    }

    private static boolean containsEquivalent(List<CompoundTag> source, CompoundTag target) {
        return source.stream().anyMatch(existing -> existing.equals(target));
    }

    private static final class VaultFileFormatException extends IOException {
        private static final long serialVersionUID = 1;

        private VaultFileFormatException(String message) {
            super(message);
        }
    }

    public static Vault getInstance() {
        return INSTANCE;
    }
}