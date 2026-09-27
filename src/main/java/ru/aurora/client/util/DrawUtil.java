package ru.aurora.client.util;

import net.minecraft.client.gui.DrawContext;

/**
 * Helper utilities for smooth, rounded, non-blocky UI rendering.
 */
public final class DrawUtil {
    private DrawUtil() {}

    /** Draw a filled rectangle with softened, rounded corners (no sharp pixel corners). */
    public static void fillRounded(DrawContext c, int x, int y, int w, int h, int radius, int color) {
        if (radius < 1) { c.fill(x, y, x + w, y + h, color); return; }
        int r = Math.min(radius, Math.min(w / 2, h / 2));
        int a = (color >> 24) & 0xFF;
        int rc = (color >> 16) & 0xFF, gc = (color >> 8) & 0xFF, bc = color & 0xFF;

        // Rectangular body
        c.fill(x + r, y, x + w - r, y + h, color);
        c.fill(x, y + r, x + w, y + h - r, color);

        // Four corner quarter-circles with edge softening for anti-aliased look
        for (int i = 0; i <= r; i++) {
            for (int j = 0; j <= r; j++) {
                double dist = Math.sqrt((double)i * i + (double)j * j);
                if (dist > r) continue;
                int alpha = a;
                double edge = r - dist;
                if (edge < 1.0) alpha = Math.max(0, Math.min(255, (int)(a * edge)));
                int col = (alpha << 24) | (rc << 16) | (gc << 8) | bc;
                c.fill(x + r - i, y + r - j, x + r - i + 1, y + r - j + 1, col);
                c.fill(x + w - r + i - 1, y + r - j, x + w - r + i, y + r - j + 1, col);
                c.fill(x + r - i, y + h - r + j - 1, x + r - i + 1, y + h - r + j, col);
                c.fill(x + w - r + i - 1, y + h - r + j - 1, x + w - r + i, y + h - r + j, col);
            }
        }
    }

    /** Draw a 1px rounded outline. */
    public static void drawRoundedOutline(DrawContext c, int x, int y, int w, int h, int radius, int color) {
        int r = Math.min(radius, Math.min(w / 2, h / 2));
        for (int i = 0; i <= r; i++) {
            for (int j = 0; j <= r; j++) {
                double dist = Math.sqrt((double)i * i + (double)j * j);
                if (dist > r || dist < r - 1) continue;
                c.fill(x + r - i, y + r - j, x + r - i + 1, y + r - j + 1, color);
                c.fill(x + w - r + i - 1, y + r - j, x + w - r + i, y + r - j + 1, color);
                c.fill(x + r - i, y + h - r + j - 1, x + r - i + 1, y + h - r + j, color);
                c.fill(x + w - r + i - 1, y + h - r + j - 1, x + w - r + i, y + h - r + j, color);
            }
        }
        c.fill(x + r, y, x + w - r, y + 1, color);
        c.fill(x + r, y + h - 1, x + w - r, y + h, color);
        c.fill(x, y + r, x + 1, y + h - r, color);
        c.fill(x + w - 1, y + r, x + w, y + h - r, color);
    }

    /** Vertical gradient with rounded ends (pill shape). */
    public static void fillPill(DrawContext c, int x, int y, int w, int h, int topColor, int bottomColor) {
        if (w < 1 || h < 1) return;
        int r = h / 2;
        // Use 1-pixel-tall horizontal strips to draw a smooth top-to-bottom gradient
        // because DrawContext.fillGradient signature changed between MC versions (vertical ↔ horizontal).
        for (int row = 0; row < h; row++) {
            float t = h <= 1 ? 0f : (float)row / (float)(h - 1);
            int col = lerp(topColor, bottomColor, t);
            c.fill(x + r, y + row, x + w - r, y + row + 1, col);
        }
        // Rounded left cap
        for (int i = 0; i <= r; i++) {
            int j = (int)Math.round(Math.sqrt((double)r * r - (double)i * i));
            for (int row = Math.max(0, r - j); row < Math.min(h, r + j); row++) {
                float t = h <= 1 ? 0f : (float)row / (float)(h - 1);
                int col = lerp(topColor, bottomColor, t);
                c.fill(x + r - i, y + row, x + r - i + 1, y + row + 1, col);
                c.fill(x + w - r + i - 1, y + row, x + w - r + i, y + row + 1, col);
            }
        }
    }

    private static int lerp(int a, int b, float t) {
        int ar = (a >> 24) & 0xFF, ag = (a >> 16) & 0xFF, abg = (a >> 8) & 0xFF, abb = a & 0xFF;
        int br = (b >> 24) & 0xFF, bg = (b >> 16) & 0xFF, bbg = (b >> 8) & 0xFF, bbb = b & 0xFF;
        int r = (int)(ar + (br - ar) * t);
        int g = (int)(ag + (bg - ag) * t);
        int bl = (int)(abg + (bbg - abg) * t);
        int bv = (int)(abb + (bbb - abb) * t);
        return (r << 24) | (g << 16) | (bl << 8) | bv;
    }
}
