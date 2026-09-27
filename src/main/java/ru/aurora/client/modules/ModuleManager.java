package ru.aurora.client.modules;

import ru.aurora.client.modules.impl.AutoSprint;
import ru.aurora.client.modules.impl.Fullbright;
import ru.aurora.client.modules.impl.NoHurtCam;
import ru.aurora.client.modules.impl.Zoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    private ModuleManager() {}

    public static void init() {
        register(new Fullbright());
        register(new Zoom());
        register(new NoHurtCam());
        register(new AutoSprint());
    }

    private static void register(Module m) { MODULES.add(m); }

    public static List<Module> getModules() { return MODULES; }

    public static List<Module> getByCategory(Category c) {
        return MODULES.stream().filter(m -> m.getCategory() == c).toList();
    }

    public static Optional<Module> getByName(String name) {
        return MODULES.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst();
    }

    public static void onTick() {
        MODULES.stream().filter(Module::isEnabled).forEach(Module::onTick);
    }

    public static void onRender() {
        MODULES.stream().filter(Module::isEnabled).forEach(Module::onRender);
    }
}
