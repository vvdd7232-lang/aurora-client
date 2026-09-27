package ru.aurora.client.modules.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import ru.aurora.client.modules.Category;
import ru.aurora.client.modules.Module;

public class AutoSprint extends Module {
    public AutoSprint() {
        super("AutoSprint", "Постоянный спринт при движении", Category.MOVEMENT, -1);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        if (p != null) {
            p.setSprinting(true);
        }
    }
}
