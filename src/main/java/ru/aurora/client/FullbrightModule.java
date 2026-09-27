package ru.aurora.client;

import net.minecraft.client.MinecraftClient;

public final class FullbrightModule extends Module {
    private static final double FULLBRIGHT_GAMMA = 16.0;
    private double previousGamma = 1.0;
    private boolean hasPrevious;

    public FullbrightModule() {
        super("fullbright", "FULLBRIGHT", "Максимальная яркость в темноте", Category.VISUAL);
    }

    @Override
    protected void onEnable() {
        previousGamma = MinecraftClient.getInstance().options.getGamma().getValue();
        hasPrevious = true;
        MinecraftClient.getInstance().options.getGamma().setValue(FULLBRIGHT_GAMMA);
    }

    @Override
    protected void onDisable() {
        if (!hasPrevious) {
            return;
        }
        hasPrevious = false;
        MinecraftClient.getInstance().options.getGamma().setValue(previousGamma);
    }
}
