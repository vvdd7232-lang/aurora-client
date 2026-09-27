package ru.aurora.client.modules.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import ru.aurora.client.modules.Category;
import ru.aurora.client.modules.Module;

public class Fullbright extends Module {
    private double oldGamma;

    public Fullbright() {
        super("Fullbright", "Максимальная яркость в любое время", Category.RENDER, -1);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        oldGamma = ((Number) mc.options.getGamma().getValue()).doubleValue();
        ((net.minecraft.client.option.SimpleOption) mc.options.getGamma()).setValue(Double.valueOf(10.0));
        mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999999, 0, false, false, false));
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        ((net.minecraft.client.option.SimpleOption) mc.options.getGamma()).setValue(Double.valueOf(oldGamma));
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && !mc.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999999, 0, false, false, false));
        }
    }
}
