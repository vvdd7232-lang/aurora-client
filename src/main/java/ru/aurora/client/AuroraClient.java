package ru.aurora.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.aurora.client.gui.AuroraScreen;
import ru.aurora.client.modules.ModuleManager;

public final class AuroraClient implements ClientModInitializer {
    private static KeyBinding openMenu;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.aurora.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.create("aurora:main")
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) client.setScreen(new AuroraScreen());
            ModuleManager.onTick();
        });
    }
}
