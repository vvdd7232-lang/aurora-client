package ru.aurora.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModuleManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, Module> modules = new LinkedHashMap<>();

    public void register(Module module) {
        modules.put(module.id(), module);
    }

    public Module get(String id) {
        return modules.get(id);
    }

    public boolean isEnabled(String id) {
        Module module = modules.get(id);
        return module != null && module.isEnabled();
    }

    public Collection<Module> all() {
        return modules.values();
    }

    public int enabledCount() {
        int count = 0;
        for (Module module : modules.values()) {
            if (module.isEnabled()) {
                count++;
            }
        }
        return count;
    }

    private Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("aurora-client.json");
    }

    public void load() {
        Path path = configPath();
        if (!Files.isRegularFile(path)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null || !root.has("modules") || !root.get("modules").isJsonObject()) {
                return;
            }
            JsonObject states = root.getAsJsonObject("modules");
            for (Map.Entry<String, JsonElement> entry : states.entrySet()) {
                Module module = modules.get(entry.getKey());
                if (module != null && entry.getValue().isJsonPrimitive()) {
                    module.setEnabledInternal(entry.getValue().getAsBoolean());
                }
            }
        } catch (Exception e) {
            AuroraClient.LOGGER.warn("Failed to load aurora-client.json", e);
        }
    }

    public void save() {
        JsonObject states = new JsonObject();
        for (Module module : modules.values()) {
            states.addProperty(module.id(), module.isEnabled());
        }
        JsonObject root = new JsonObject();
        root.add("modules", states);
        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(root, writer);
            }
        } catch (IOException e) {
            AuroraClient.LOGGER.warn("Failed to save aurora-client.json", e);
        }
    }
}
