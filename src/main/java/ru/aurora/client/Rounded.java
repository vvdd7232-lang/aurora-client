package ru.aurora.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Smooth anti-aliased rounded rectangles.
 * Each radius gets one tiny white nine-slice mask generated at runtime;
 * the requested color is applied per draw as a tint, so animations are free.
 */
public final class Rounded {
    private static final Map<Integer, Identifier> MASKS = new HashMap<>();

    private Rounded() {
    }

    public static void draw(DrawContext c, int x, int y, int w, int h, int radius, int argb) {
        if (w <= 0 || h <= 0) {
            return;
        }
        int r = Math.max(1, Math.min(radius, Math.min(w, h) / 2));
        Identifier mask = mask(r);
        int s = size(r);
        // Corners are drawn 1:1 so edges stay smooth whatever sampler is used.
        slice(c, mask, x, y, r, r, 0, 0, r, r, s, argb);
        slice(c, mask, x + w - r, y, r, r, s - r, 0, r, r, s, argb);
        slice(c, mask, x, y + h - r, r, r, 0, s - r, r, r, s, argb);
        slice(c, mask, x + w - r, y + h - r, r, r, s - r, s - r, r, r, s, argb);
        slice(c, mask, x + r, y, w - 2 * r, r, r, 0, s - 2 * r, r, s, argb);
        slice(c, mask, x + r, y + h - r, w - 2 * r, r, r, s - r, s - 2 * r, r, s, argb);
        slice(c, mask, x, y + r, r, h - 2 * r, 0, r, r, s - 2 * r, s, argb);
        slice(c, mask, x + w - r, y + r, r, h - 2 * r, s - r, r, r, s - 2 * r, s, argb);
        slice(c, mask, x + r, y + r, w - 2 * r, h - 2 * r, r, r, s - 2 * r, s - 2 * r, s, argb);
    }

    private static void slice(DrawContext c, Identifier mask, int x, int y, int w, int h,
                              float u, float v, int regionW, int regionH, int texSize, int argb) {
        if (w <= 0 || h <= 0 || regionW <= 0 || regionH <= 0) {
            return;
        }
        c.drawTexture(RenderPipelines.GUI_TEXTURED, mask, x, y, u, v, w, h, regionW, regionH, texSize, texSize, argb);
    }

    private static int size(int radius) {
        return radius * 2 + 8;
    }

    private static Identifier mask(int radius) {
        Identifier existing = MASKS.get(radius);
        if (existing != null) {
            return existing;
        }
        Identifier id = generate(radius);
        MASKS.put(radius, id);
        return id;
    }

    private static Identifier generate(int radius) {
        int s = size(radius);
        NativeImage image = new NativeImage(NativeImage.Format.RGBA, s, s, false);
        for (int y = 0; y < s; y++) {
            for (int x = 0; x < s; x++) {
                float coverage = coverage(x + 0.5f, y + 0.5f, s, radius);
                int alpha = Math.round(255 * coverage);
                image.setColorArgb(x, y, (alpha << 24) | 0xFFFFFF);
            }
        }
        NativeImageBackedTexture texture = new NativeImageBackedTexture(() -> "aurora/rounded", image);
        TextureManager manager = MinecraftClient.getInstance().getTextureManager();
        Identifier id = Identifier.of("aurora", "rounded/r" + radius);
        manager.registerTexture(id, texture);
        texture.upload();
        return id;
    }

    /** Anti-aliased coverage of a rounded rect with radius r inside an s*s mask. */
    private static float coverage(float x, float y, float s, float r) {
        float dx = Math.max(Math.max(r - x, x - (s - r)), 0);
        float dy = Math.max(Math.max(r - y, y - (s - r)), 0);
        float d = (float) Math.sqrt(dx * dx + dy * dy);
        return Math.min(1f, Math.max(0f, r + 0.5f - d));
    }

    /** Linear interpolation between two ARGB colors. */
    public static int lerp(int from, int to, float t) {
        float k = t < 0 ? 0 : Math.min(1f, t);
        int a = Math.round(lerpChannel((from >>> 24) & 0xFF, (to >>> 24) & 0xFF, k));
        int r = Math.round(lerpChannel((from >> 16) & 0xFF, (to >> 16) & 0xFF, k));
        int g = Math.round(lerpChannel((from >> 8) & 0xFF, (to >> 8) & 0xFF, k));
        int b = Math.round(lerpChannel(from & 0xFF, to & 0xFF, k));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static float lerpChannel(int from, int to, float k) {
        return from + (to - from) * k;
    }

    /** Moves current towards target by at most delta*speed. */
    public static float approach(float current, float target, float delta, float speed) {
        if (current < target) {
            return Math.min(target, current + delta * speed);
        }
        if (current > target) {
            return Math.max(target, current - delta * speed);
        }
        return target;
    }
}
