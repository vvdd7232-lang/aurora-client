package ru.aurora.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import ru.aurora.client.modules.Category;
import ru.aurora.client.modules.Module;
import ru.aurora.client.modules.ModuleManager;
import ru.aurora.client.util.DrawUtil;

import java.util.List;

/**
 * Aurora main menu. Smooth, rounded, non-blocky UI with working module toggles.
 */
public final class AuroraScreen extends Screen {
    private static final int WHITE  = 0xFFF4F5FF;
    private static final int SOFT   = 0xFF9AA4BA;
    private static final int FAINT  = 0xFF68738A;
    private static final int ACCENT = 0xFFB18CFF;

    private static final Identifier LOGO = Identifier.of("aurora", "icon.png");

    private static final Category[] CATEGORIES = {null, Category.COMBAT, Category.MOVEMENT, Category.RENDER, Category.PLAYER, Category.MISC};
    private static final String[] TAB_LABELS = {"Обзор", "Бой", "Движение", "Визуал", "Игрок", "Утилиты"};

    private int activeTab = 0;
    private float anim = 0f;
    private final float[] toggleAnim = new float[64];
    private long startedAt = System.currentTimeMillis();

    public AuroraScreen() { super(Text.literal("Aurora")); }
    @Override public boolean shouldPause() { return false; }

    @Override
    public void render(DrawContext c, int mouseX, int mouseY, float delta) {
        anim = MathHelper.clamp(anim + delta * 4f, 0f, 1f);

        int w = Math.min(820, width - 48);
        int h = Math.min(510, height - 48);
        if (w < 400 || h < 280) { super.render(c, mouseX, mouseY, delta); return; }

        int x = (width - w) / 2;
        int y = (height - h) / 2;
        float ease = easeOutCubic(anim);
        int yOffset = (int)((1f - ease) * 12f);

        // Dim background
        c.fill(0, 0, width, height, 0xAA0A0D18);

        // Top accent glow
        for (int i = 0; i < 40; i++) {
            int a = (int)(18 * (1f - i / 40f) * ease);
            c.fill(x + 80, y + 10 - i + yOffset, x + w - 80, y + 11 - i + yOffset, (a << 24) | 0xB18CFF);
        }

        // Window drop shadow
        for (int i = 10; i >= 0; i--) {
            int sa = (int)(10 * (1f - i / 10f));
            DrawUtil.fillRounded(c, x - i, y - i + yOffset, w + i * 2, h + i * 2, 18 + i, (sa << 24) | 0x0A0D18);
        }

        // Main window
        DrawUtil.fillRounded(c, x, y + yOffset, w, h, 16, 0xF10E121C);
        DrawUtil.fillPill(c, x + 24, y + yOffset + 14, w - 48, 3, 0xFFB18CFF, 0xFF5F83FF);

        // Sidebar
        int sideW = 200;
        DrawUtil.fillRounded(c, x + 10, y + yOffset + 26, sideW, h - 36, 12, 0xFF141825);

        // Logo: draw a purple rounded square + letter "A" (skip texture draw to avoid API mismatch)
        DrawUtil.fillRounded(c, x + 24, y + yOffset + 40, 44, 44, 10, 0xFF302648);
        DrawUtil.fillRounded(c, x + 26, y + yOffset + 42, 40, 40, 8, 0xFF211D31);
        c.drawTextWithShadow(textRenderer, "A", x + 40, y + yOffset + 52, 0xFFC5A8FF);
        c.drawTextWithShadow(textRenderer, "AURORA", x + 80, y + yOffset + 46, WHITE);
        c.drawText(textRenderer, "CLIENT  •  1.21.11", x + 80, y + yOffset + 61, FAINT, false);

        c.drawText(textRenderer, "РАЗДЕЛЫ", x + 24, y + yOffset + 106, FAINT, false);

        // Tabs
        for (int i = 0; i < CATEGORIES.length; i++) {
            int ty = y + yOffset + 126 + i * 34;
            boolean selected = (activeTab == i);
            boolean hover = isHover(mouseX, mouseY, x + 18, ty - 4, sideW - 16, 28);

            if (selected) {
                DrawUtil.fillRounded(c, x + 18, ty - 4, sideW - 16, 28, 8, 0xFF27223C);
                DrawUtil.fillPill(c, x + 26, ty + 8, 4, 10, 0xFFC9B1FF, 0xFF7B6CE8);
            } else if (hover) {
                DrawUtil.fillRounded(c, x + 18, ty - 4, sideW - 16, 28, 8, 0xFF1B1F2D);
            }
            String icon = i == 0 ? "⌂" : CATEGORIES[i].icon;
            c.drawText(textRenderer, icon, x + 40, ty + 4, selected ? 0xFFC9B1FF : FAINT, false);
            c.drawText(textRenderer, TAB_LABELS[i], x + 60, ty + 4, selected ? WHITE : SOFT, false);
        }

        int sideBottom = y + yOffset + h - 78;
        c.drawText(textRenderer, "● ВКЛЮЧЕНО МОДУЛЕЙ", x + 24, sideBottom, SOFT, false);
        long enabledCount = ModuleManager.getModules().stream().filter(Module::isEnabled).count();
        c.drawTextWithShadow(textRenderer, Long.toString(enabledCount), x + 24, sideBottom + 14, ACCENT);

        // Main content
        int cx = x + sideW + 28;
        int cw = w - sideW - 46;
        int top = y + yOffset + 32;

        c.drawText(textRenderer, "AURORA  /  " + TAB_LABELS[activeTab].toUpperCase(), cx, top, FAINT, false);
        c.drawTextWithShadow(textRenderer, activeTab == 0 ? "Добро пожаловать" : TAB_LABELS[activeTab], cx, top + 17, WHITE);
        String subtitle = activeTab == 0
                ? "Настрой Aurora под себя. Переключатели работают в реальном времени."
                : "Клик по карточке включает и отключает модуль.";
        c.drawText(textRenderer, subtitle, cx, top + 35, SOFT, false);

        int right = x + w - 28;
        DrawUtil.fillPill(c, right - 78, y + yOffset + 30, 60, 22, 0xFF191E29, 0xFF131721);
        int dotPulse = (int)(4 + 3 * Math.sin((System.currentTimeMillis() - startedAt) / 300.0));
        DrawUtil.fillPill(c, right - 67, y + yOffset + 38 - dotPulse/2, 6 + dotPulse, 6 + dotPulse, 0x8077D9AC, 0x0056BF92);
        DrawUtil.fillPill(c, right - 67, y + yOffset + 38, 6, 6, 0xFF77D9AC, 0xFF56BF92);
        c.drawText(textRenderer, "ONLINE", right - 57, y + yOffset + 35, SOFT, false);

        // Cards grid
        List<Module> modules = activeTab == 0 ? ModuleManager.getModules() : ModuleManager.getByCategory(CATEGORIES[activeTab]);
        int gridTop = top + 64;
        int cardW = (cw - 12) / 2;
        int cardH = 64;

        int displayCount = Math.min(modules.size(), 6);
        for (int i = 0; i < displayCount; i++) {
            Module m = modules.get(i);
            int col = i % 2, row = i / 2;
            int bx = cx + col * (cardW + 12);
            int by = gridTop + row * (cardH + 10);
            boolean hover = isHover(mouseX, mouseY, bx, by, cardW, cardH);
            boolean on = m.isEnabled();

            toggleAnim[i] += ((on ? 1f : 0f) - toggleAnim[i]) * Math.min(1f, delta * 8f);
            float ta = toggleAnim[i];

            int bg = on ? lerpColor(0xFF191E29, 0xFF272040, ta) : (hover ? 0xFF1E2332 : 0xFF171B26);
            DrawUtil.fillRounded(c, bx, by, cardW, cardH, 12, bg);

            int edgeCol = lerpColor(0xFF343344, ACCENT, ta);
            DrawUtil.fillPill(c, bx + 4, by + 10, 3, cardH - 20, edgeCol, edgeCol);

            if (ta > 0.01f) {
                for (int g = 0; g < 8; g++) {
                    int ga = (int)(10 * (1f - g / 8f) * ta);
                    DrawUtil.drawRoundedOutline(c, bx - g, by - g, cardW + g * 2, cardH + g * 2, 12 + g, (ga << 24) | 0xB18CFF);
                }
            }

            int iconCol = on ? 0xFFC6ACFF : 0xFF6E6889;
            DrawUtil.fillRounded(c, bx + 16, by + 18, 28, 28, 10, on ? 0xFF342A52 : 0xFF212633);
            c.drawText(textRenderer, "✦", bx + 26, by + 24, iconCol, false);

            c.drawText(textRenderer, m.getName(), bx + 54, by + 14, on ? WHITE : 0xFFD8DCEC, false);
            c.drawText(textRenderer, m.getDescription(), bx + 54, by + 32, on ? 0xFFB8A6E6 : FAINT, false);

            int sx = bx + cardW - 42, sy = by + 22, sw = 30, sh = 18;
            if (ta < 0.01f) {
                DrawUtil.fillPill(c, sx, sy, sw, sh, 0xFF2C3242, 0xFF2C3242);
            } else {
                int t = lerpColor(0xFF2C3242, 0xFF916CFF, ta);
                int bb = lerpColor(0xFF2C3242, 0xFF6B88FF, ta);
                DrawUtil.fillPill(c, sx, sy, sw, sh, t, bb);
            }
            int knobX = (int)(sx + 3 + ta * (sw - 16));
            DrawUtil.fillPill(c, knobX, sy + 3, 12, 12, 0xFFFFFFFF, 0xFFE0E2EC);
        }

        // Footer
        int fy = y + yOffset + h - 34;
        c.drawText(textRenderer, "AURORA  •  EARLY ACCESS", cx, fy, FAINT, false);
        c.drawText(textRenderer, "ПРАВЫЙ SHIFT  ·  ЗАКРЫТЬ", right - 150, fy, 0xFFB9A1EE, false);

        int pulse = (int)(6 + 80 * (0.5f + 0.5f * Math.sin((System.currentTimeMillis() - startedAt) / 500.0)));
        DrawUtil.fillPill(c, cx, fy - 8, Math.min(pulse, cw), 2, ACCENT, 0xFF5F83FF);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int w = Math.min(820, width - 48);
        int h = Math.min(510, height - 48);
        int x = (width - w) / 2, y = (height - h) / 2;
        float ease = easeOutCubic(anim);
        int yOffset = (int)((1f - ease) * 12f);
        int sideW = 200;

        for (int i = 0; i < CATEGORIES.length; i++) {
            int ty = y + yOffset + 126 + i * 34;
            if (isHover(mouseX, mouseY, x + 18, ty - 4, sideW - 16, 28)) {
                activeTab = i;
                return true;
            }
        }

        int cx = x + sideW + 28;
        int cw = w - sideW - 46;
        int top = y + yOffset + 32;
        List<Module> modules = activeTab == 0 ? ModuleManager.getModules() : ModuleManager.getByCategory(CATEGORIES[activeTab]);
        int gridTop = top + 64;
        int cardW = (cw - 12) / 2, cardH = 64;
        int displayCount = Math.min(modules.size(), 6);
        for (int i = 0; i < displayCount; i++) {
            int col = i % 2, row = i / 2;
            int bx = cx + col * (cardW + 12);
            int by = gridTop + row * (cardH + 10);
            if (isHover(mouseX, mouseY, bx, by, cardW, cardH)) {
                modules.get(i).toggle();
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    private static boolean isHover(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private static float easeOutCubic(float t) { return 1f - (float)Math.pow(1f - t, 3); }

    private static int lerpColor(int a, int b, float t) {
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int r = (int)(ar + (br - ar) * t);
        int g = (int)(ag + (bg - ag) * t);
        int bl = (int)(ab + (bb - ab) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | bl;
    }
}
