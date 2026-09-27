package ru.aurora.client.modules.impl;

import net.minecraft.client.MinecraftClient;
import ru.aurora.client.modules.Category;
import ru.aurora.client.modules.Module;

/**
 * NoHurtCam — disables camera shake when taking damage.
 * Works by resetting the player's hurtTime each tick (no Mixin needed, safe for any build).
 */
public class NoHurtCam extends Module {
    public NoHurtCam() {
        super("NoHurtCam", "Otkluchaet tryasku ekrana pri urone", Category.RENDER, -1);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            // Reset hurt time so the camera tilt never starts
            mc.player.hurtTime = 0;
        }
    }
}
