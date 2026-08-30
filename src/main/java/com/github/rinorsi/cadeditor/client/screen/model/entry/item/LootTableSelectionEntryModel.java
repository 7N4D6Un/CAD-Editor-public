package com.github.rinorsi.cadeditor.client.screen.model.entry.item;

import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.LootTableListSelectionElementModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.github.rinorsi.cadeditor.common.loot.LootTableIndex;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;


public class LootTableSelectionEntryModel extends SelectionEntryModel {
    public LootTableSelectionEntryModel(CategoryModel category, String value, Consumer<String> action) {
        super(category, ModTexts.LOOT_TABLE, value, action);
    }

    @Override 
    public List<String> getSuggestions() {
        return List.of("minecraft:chests/", "minecraft:entities/", "minecraft:blocks/", "minecraft:gameplay/");
    }

    @Override 
    public MutableComponent getSelectionScreenTitle() {
        return ModTexts.LOOT_TABLE;
    }

    @Override 
    public List<? extends ListSelectionElementModel> getSelectionItems() {
        List<ListSelectionElementModel> out = new ArrayList<>();
        try {
            List<Identifier> indexed = LootTableIndex.getAll();
            if (!indexed.isEmpty()) {
                for (Identifier id : indexed) {
                    out.add(new LootTableListSelectionElementModel(id));
                }
            }
        } catch (Throwable th) {
        }
        if (out.isEmpty()) {
            try {
                Minecraft mc = Minecraft.getInstance();
                IntegratedServer integratedServer = mc.getSingleplayerServer();
                if (integratedServer != null) {
                    Map<Identifier, Resource> map = integratedServer.getResourceManager().listResources("loot_tables", rl -> rl.getPath().endsWith(".json"));
                    for (Identifier entry : map.keySet()) {
                        String path = entry.getPath();
                        if (path.startsWith("loot_tables/")) {
                            String clean = path.substring("loot_tables/".length(), path.length() - ".json".length());
                            Identifier id = Identifier.fromNamespaceAndPath(entry.getNamespace(), clean);
                            out.add(new LootTableListSelectionElementModel(id));
                        }
                    }
                }
            } catch (Throwable th) {
            }
        }
        if (out.isEmpty()) {
            try {
                Class<?> helper = Class.forName("net.fabricmc.fabric.api.resource.ResourceManagerHelper");
                Class.forName("net.fabricmc.api.EnvType");
                Class<?> resType = Class.forName("net.fabricmc.fabric.api.resource.ResourceType");
                Field serverDataField = resType.getField("SERVER_DATA");
                Object SERVER_DATA = serverDataField.get(null);
                Method getMethod = helper.getMethod("get", resType);
                Object helperInst = getMethod.invoke(null, SERVER_DATA);
                Method getRm = helperInst.getClass().getMethod("getResourceManager", new Class<?>[0]);
                Object rm = getRm.invoke(helperInst, new Object[0]);
                Method listResources = rm.getClass().getMethod("listResources", String.class, Predicate.class);
                java.util.function.Predicate<Identifier> lootFilter = rl -> rl.getPath().endsWith(".json");
                Map<Identifier, Object> map = (Map) listResources.invoke(rm, "loot_tables", lootFilter);
                for (Identifier entry : map.keySet()) {
                    String path = entry.getPath();
                    if (path.startsWith("loot_tables/")) {
                        String clean = path.substring("loot_tables/".length(), path.length() - ".json".length());
                        Identifier id = Identifier.fromNamespaceAndPath(entry.getNamespace(), clean);
                        out.add(new LootTableListSelectionElementModel(id));
                    }
                }
            } catch (Throwable th) {
            }
        }
        if (out.isEmpty()) {
            try {
                Class<?> loader = Class.forName("net.fabricmc.loader.api.FabricLoader");
                Method getInstance = loader.getMethod("getInstance", new Class<?>[0]);
                Object fl = getInstance.invoke(null, new Object[0]);
                Method getAllMods = fl.getClass().getMethod("getAllMods", new Class<?>[0]);
                Collection<Object> mods = (Collection) getAllMods.invoke(fl, new Object[0]);
                for (Object mod : mods) {
                    Method getOrigin = mod.getClass().getMethod("getOrigin", new Class<?>[0]);
                    Object origin = getOrigin.invoke(mod, new Object[0]);
                    Method getPaths = origin.getClass().getMethod("getPaths", new Class<?>[0]);
                    List<Path> paths = (List) getPaths.invoke(origin, new Object[0]);
                    for (Path root : paths) {
                        try (FileSystem fs = Files.isRegularFile(root) ? FileSystems.newFileSystem(root, (ClassLoader) null) : null) {
                            Path base = fs == null ? root : fs.getPath("/");
                            Path dataDir = base.resolve("data");
                            if (Files.exists(dataDir)) {
                                try (Stream<Path> walk = Files.walk(dataDir)) {
                                    walk.filter(p -> {
                                        String s = p.toString().replace('\\', '/');
                                        return p.getFileName() != null && p.getFileName().toString().endsWith(".json") && s.contains("/loot_tables/");
                                    }).forEach(p -> {
                                        try {
                                            int idx = p.toString().replace('\\', '/').indexOf("/loot_tables/");
                                            if (idx <= 0) {
                                                return;
                                            }
                                            Path rel = dataDir.relativize(p);
                                            String namespace = rel.getName(0).toString();
                                            String sub = rel.subpath(1, rel.getNameCount()).toString().replace('\\', '/');
                                            if (sub.startsWith("loot_tables/")) {
                                                String clean = sub.substring("loot_tables/".length(), sub.length() - ".json".length());
                                                out.add(new LootTableListSelectionElementModel(Identifier.fromNamespaceAndPath(namespace, clean)));
                                            }
                                        } catch (Throwable ignored) {
                                        }
                                    });
                                } catch (Throwable ignored) {
                                }
                            }
                        } catch (Throwable ignored) {
                        }
                    }
                }
            } catch (Throwable th) {
            }
        }
        if (out.isEmpty()) {
            Path runDir = Paths.get("fabric", "run");
            List<Path> candidates = new ArrayList<>();
            candidates.add(runDir.resolve(".fabric").resolve("processedMods"));
            candidates.add(runDir.resolve("mods"));
            for (Path dir : candidates) {
                if (!Files.exists(dir)) {
                    continue;
                }
                try (Stream<Path> stream = Files.list(dir)) {
                    for (Path path : iterable(stream)) {
                        try {
                            if (Files.isDirectory(path)) {
                                scanLootTablesDir(path.resolve("data"), out);
                            } else if (Files.isRegularFile(path) && path.getFileName().toString().endsWith(".jar")) {
                                try (FileSystem fs = FileSystems.newFileSystem(path, (ClassLoader) null)) {
                                    scanLootTablesDir(fs.getPath("data"), out);
                                }
                            }
                        } catch (Throwable ignored) {
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        out.sort(Comparator.naturalOrder());
        return out;
    }

    private static Iterable<Path> iterable(Stream<Path> stream) {
        return stream::iterator;
    }

    private static void scanLootTablesDir(Path dataDir, List<? super LootTableListSelectionElementModel> out) throws java.io.IOException {
        if (!Files.exists(dataDir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(dataDir)) {
            walk.filter(p -> {
                String s = p.toString().replace('\\', '/');
                return p.getFileName() != null && p.getFileName().toString().endsWith(".json") && s.contains("/loot_tables/");
            }).forEach(p -> {
                try {
                    int idx = p.toString().replace('\\', '/').indexOf("/loot_tables/");
                    if (idx <= 0) {
                        return;
                    }
                    Path rel = dataDir.relativize(p);
                    String namespace = rel.getName(0).toString();
                    String sub = rel.subpath(1, rel.getNameCount()).toString().replace('\\', '/');
                    if (sub.startsWith("loot_tables/")) {
                        String clean = sub.substring("loot_tables/".length(), sub.length() - ".json".length());
                        out.add(new LootTableListSelectionElementModel(Identifier.fromNamespaceAndPath(namespace, clean)));
                    }
                } catch (Throwable ignored) {
                }
            });
        }
    }
}
