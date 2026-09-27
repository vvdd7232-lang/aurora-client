package ru.aurora.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/** A purely visual client menu mockup. No gameplay modules are implemented. */
public final class AuroraScreen extends Screen {
    private static final int WHITE = 0xFFF4F5FF;
    private static final int SOFT = 0xFF9AA4BA;
    private static final int FAINT = 0xFF68738A;
    private static final int ACCENT = 0xFFB18CFF;
    private static final String[] TABS = {"Обзор", "Бой", "Движение", "Визуал", "Игрок", "Утилиты"};
    private static final String[] OVERVIEW_MODULES =
            {"AIM ASSIST", "SPRINT", "FULLBRIGHT", "AUTO TOTEM", "HUD EDITOR", "VELOCITY"};
    private static final String[][] MODULES = {
            {"AIM ASSIST", "TRIGGER BOT", "VELOCITY", "CRITICALS", "REACH", "TARGET HUD"},
            {"SPRINT", "FLIGHT", "SPEED", "SAFE WALK", "STEP", "NO SLOW"},
            {"FULLBRIGHT", "TRACERS", "ESP", "CHAMS", "CROSSHAIR", "WORLD COLOR"},
            {"AUTO TOTEM", "FAST PLACE", "NO FALL", "INVENTORY", "TIMER", "PEARL TRACKER"},
            {"KEYS", "HUD EDITOR", "CONFIGS", "STAFF ALERT", "CLIENT THEME", "SCREEN INFO"}
    };
    private int activeTab = 0;
    private float animation = 0;
    private long startedAt = System.currentTimeMillis();

    public AuroraScreen() { super(Text.literal("Aurora")); }
    @Override public boolean shouldPause() { return false; }

    private int panelW() { return Math.min(760, width - 32); }
    private int panelH() { return Math.min(470, height - 32); }
    private int panelX() { return (width - panelW()) / 2 - (int) ((1 - animation) * 18); }
    private int panelY() { return (height - panelH()) / 2; }

    @Override public void render(DrawContext c, int mouseX, int mouseY, float delta) {
        animation = Math.min(1f, animation + delta * 2.5f);
        int w = panelW(), h = panelH();
        if (w < 380 || h < 260) { super.render(c, mouseX, mouseY, delta); return; }
        int x = panelX(), y = panelY();
        // Dim the game world and draw the deep, layered shell.
        c.fill(0, 0, width, height, 0xB8070910);
        c.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF343044);
        c.fill(x, y, x + w, y + h, 0xF20D1018);
        c.fill(x, y, x + w, y + 2, 0xFF916CFF);
        // Sidebar / main canvas.
        c.fill(x + 1, y + 2, x + 174, y + h - 1, 0xFF11141E);
        c.fill(x + 174, y + 2, x + 175, y + h - 1, 0xFF252A37);

        // Brand block.
        c.fill(x + 19, y + 20, x + 43, y + 44, 0xFF302648);
        c.fill(x + 20, y + 21, x + 42, y + 43, 0xFF211D31);
        c.drawTextWithShadow(textRenderer, "A", x + 27, y + 27, 0xFFC5A8FF);
        c.drawTextWithShadow(textRenderer, "AURORA", x + 52, y + 21, WHITE);
        c.drawText(textRenderer, "CLIENT  •  1.21.11", x + 52, y + 35, FAINT, false);
        c.fill(x + 18, y + 57, x + 156, y + 58, 0xFF282D3A);
        c.drawText(textRenderer, "РАЗДЕЛЫ", x + 20, y + 70, FAINT, false);

        for (int i = 0; i < TABS.length; i++) {
            int ty = y + 88 + i * 32;
            boolean selected = (activeTab == i);
            boolean hover = mouseX >= x + 12 && mouseX <= x + 161 && mouseY >= ty - 4 && mouseY < ty + 23;
            if (selected) {
                c.fill(x + 12, ty - 4, x + 161, ty + 23, 0xFF29243A);
                c.fill(x + 12, ty - 4, x + 14, ty + 23, 0xFFB18CFF);
            } else if (hover) c.fill(x + 12, ty - 4, x + 161, ty + 23, 0xFF1B202C);
            c.drawText(textRenderer, tabIcon(i), x + 23, ty + 3, selected ? 0xFFC9B1FF : FAINT, false);
            c.drawText(textRenderer, TABS[i], x + 43, ty + 3, selected ? WHITE : SOFT, false);
            if (selected) c.drawText(textRenderer, "›", x + 145, ty + 2, ACCENT, false);
        }
        int sideBottom = y + h - 54;
        c.fill(x + 17, sideBottom, x + 157, sideBottom + 1, 0xFF282D3A);
        c.fill(x + 19, sideBottom + 13, x + 26, sideBottom + 20, 0xFF77D9AC);
        c.drawText(textRenderer, "ВИЗУАЛЬНЫЙ РЕЖИМ", x + 33, sideBottom + 12, SOFT, false);
        c.drawText(textRenderer, "Только интерфейс", x + 19, sideBottom + 29, FAINT, false);

        int mx = x + 194;
        c.drawText(textRenderer, "AURORA  /  " + TABS[activeTab].toUpperCase(), mx, y + 20, FAINT, false);
        c.drawTextWithShadow(textRenderer, activeTab == 0 ? "Добро пожаловать" : TABS[activeTab], mx, y + 37, WHITE);
        c.drawText(textRenderer, activeTab == 0 ? "Настрой свой интерфейс. Остальное появится позже." : "Макет раздела — функции пока не подключены.", mx, y + 55, SOFT, false);

        // Small status tile and decorative version tile.
        int right = x + w - 21;
        c.fill(right - 168, y + 19, right - 82, y + 43, 0xFF191E29);
        c.fill(right - 158, y + 28, right - 152, y + 34, 0xFF77D9AC);
        c.drawText(textRenderer, "ONLINE", right - 144, y + 27, SOFT, false);
        c.fill(right - 73, y + 19, right, y + 43, 0xFF191E29);
        c.drawText(textRenderer, "v1.0.0", right - 61, y + 27, 0xFFC2A8FF, false);

        // Search field, visual only.
        int gridTop = y + 89;
        c.fill(mx, gridTop - 1, right, gridTop + 25, 0xFF171B25);
        c.drawText(textRenderer, "⌕", mx + 10, gridTop + 7, ACCENT, false);
        c.drawText(textRenderer, "Поиск модулей...", mx + 27, gridTop + 7, FAINT, false);
        c.drawText(textRenderer, "ВСЕ  ·  06", right - 59, gridTop + 7, FAINT, false);

        String[] names = activeTab == 0 ? OVERVIEW_MODULES : MODULES[activeTab - 1];
        int cardW = (right - mx - 10) / 2;
        int cardH = 58;
        for (int i = 0; i < 6; i++) {
            int col = i % 2, row = i / 2;
            int bx = mx + col * (cardW + 10), by = gridTop + 37 + row * (cardH + 9);
            boolean hover = mouseX >= bx && mouseX <= bx + cardW && mouseY >= by && mouseY <= by + cardH;
            c.fill(bx, by, bx + cardW, by + cardH, hover ? 0xFF202635 : 0xFF191E29);
            c.fill(bx, by, bx + 2, by + cardH, hover ? 0xFF9B7BFF : 0xFF343344);
            c.fill(bx + 12, by + 12, bx + 31, by + 31, 0xFF282237);
            c.drawText(textRenderer, cardIcon(i), bx + 18, by + 18, 0xFFC6ACFF, false);
            c.drawText(textRenderer, names[i], bx + 40, by + 12, WHITE, false);
            c.drawText(textRenderer, "В разработке", bx + 40, by + 29, FAINT, false);
            // Muted, inactive switch — deliberately non-interactive.
            c.fill(bx + cardW - 35, by + 19, bx + cardW - 13, by + 31, 0xFF303645);
            c.fill(bx + cardW - 33, by + 21, bx + cardW - 23, by + 29, 0xFF7B8494);
        }

        // Footer bar, tiny animated accent and controls hint.
        int fy = y + h - 30;
        c.fill(mx, fy - 7, right, fy - 6, 0xFF282D3A);
        int pulse = (int)(4 + 12 * (0.5 + 0.5 * Math.sin((System.currentTimeMillis() - startedAt) / 420.0)));
        c.fill(mx, fy - 7, mx + pulse, fy - 6, 0xFFB18CFF);
        c.drawText(textRenderer, "AURORA  •  EARLY ACCESS", mx, fy + 2, FAINT, false);
        c.drawText(textRenderer, "RIGHT SHIFT  ·  МЕНЮ", right - 116, fy + 2, 0xFFB9A1EE, false);
    }

    private String tabIcon(int i) { return new String[]{"⌂", "⚔", "↗", "◈", "♙", "⚙"}[i]; }
    private String cardIcon(int i) { return new String[]{"✦", "↗", "◉", "◇", "⌘", "＋"}[i]; }

    @Override public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x(), mouseY = click.y();
        int x = panelX(), y = panelY();
        for (int i = 0; i < TABS.length; i++) {
            int ty = y + 88 + i * 32;
            if (mouseX >= x + 12 && mouseX <= x + 161 && mouseY >= ty - 4 && mouseY < ty + 23) {
                activeTab = i; return true;
            }
        }
        // Card switches and search remain decorative in this design-only build.
        return super.mouseClicked(click, doubled);
    }
}
