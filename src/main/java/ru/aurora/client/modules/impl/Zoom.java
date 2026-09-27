package ru.aurora.client.modules.impl;

import net.minecraft.client.MinecraftClient;
import ru.aurora.client.modules.Category;
import ru.aurora.client.modules.Module;

public class Zoom extends Module {
    private int oldFov = 70;

    public Zoom() {
        super("Zoom", "Оптическое приближение (как в Optifine)", Category.RENDER, -1);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options != null) {
            oldFov = ((Number) mc.options.getFov().getValue()).intValue();
            ((net.minecraft.client.option.SimpleOption) mc.options.getFov()).setValue(Integer.valueOf(30));
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options != null) {
            ((net.minecraft.client.option.SimpleOption) mc.options.getFov()).setValue(Integer.valueOf(oldFov));
        }
    }
}
