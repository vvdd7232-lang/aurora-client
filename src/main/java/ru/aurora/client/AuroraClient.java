package ru.aurora.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuroraClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("aurora-client");
    private static ModuleManager modules;
    private static KeyBinding openMenu;

    public static ModuleManager modules() {
        return modules;
    }

    public static boolean crosshairEnabled() {
        return modules != null && modules.isEnabled("crosshair");
    }

    @Override public void onInitializeClient() {
        modules = new ModuleManager();
        modules.register(new FullbrightModule());
        modules.register(new HudModule());
        modules.register(new CrosshairModule());
        AuroraHud.register();
        modules.load();

        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.aurora.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.create(Identifier.of("aurora", "main"))));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) client.setScreen(new AuroraScreen());
        });
    }
}
