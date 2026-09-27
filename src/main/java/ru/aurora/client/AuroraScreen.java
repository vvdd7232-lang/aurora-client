package ru.aurora.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class AuroraScreen extends Screen {
    private static final int WHITE = 0xFFF4F5FF;
    private static final int SOFT = 0xFF9AA4BA;
    private static final int FAINT = 0xFF68738A;
    private static final int ACCENT = 0xFFB18CFF;
    private static final int LIVE = 0xFF9FD8B4;
    private static final int PANEL = 0xF20D1018;
    private static final int PANEL_EDGE = 0xFF343044;
    private static final int SIDEBAR = 0xFF11141E;
    private static final int CARD = 0xFF191E29;
    private static final int CARD_HOVER = 0xFF232A3B;
    private static final int TAB_SELECTED = 0xFF29243A;
    private static final int TAB_HOVER = 0xFF1B202C;

    private static final String[] TABS = {"Обзор", "Бой", "Движение", "Визуал", "Игрок", "Утилиты"};
    private static final String[][] NAMES = {
            {"FULLBRIGHT", "HUD", "CROSSHAIR", "AIM ASSIST", "SPRINT", "AUTO TOTEM"},
            {"AIM ASSIST", "TRIGGER BOT", "VELOCITY", "CRITICALS", "REACH", "TARGET HUD"},
            {"SPRINT", "FLIGHT", "SPEED", "SAFE WALK", "STEP", "NO SLOW"},
            {"FULLBRIGHT", "HUD", "CROSSHAIR", "TRACERS", "ESP", "CHAMS"},
            {"AUTO TOTEM", "FAST PLACE", "NO FALL", "INVENTORY", "TIMER", "PEARL TRACKER"},
            {"KEYS", "HUD EDITOR", "CONFIGS", "STAFF ALERT", "CLIENT THEME", "SCREEN INFO"}
    };

    private final Module[][] live = new Module[6][6];
    private int activeTab = 0;
    private float entrance = 0;
    private final float[] tabHover = new float[6];
    private final float[] cardHover = new float[6];
    private final float[][] switchAnim = new float[6][6];
    private final long startedAt = System.currentTimeMillis();

    public AuroraScreen() {
        super(Text.literal("Aurora"));
        ModuleManager modules = AuroraClient.modules();
        if (modules != null) {
            live[0][0] = live[3][0] = modules.get("fullbright");
            live[0][1] = live[3][1] = modules.get("hud");
            live[0][2] = live[3][2] = modules.get("crosshair");
        }
    }

    @Override public boolean shouldPause() {
        return false;
    }

    private int panelW() {
        return Math.min(760, width - 32);
    }

    private int panelH() {
        return Math.min(470, height - 32);
    }

    private int panelX() {
        return (width - panelW()) / 2 - (int) ((1 - entrance) * 18);
    }

    private int panelY() {
        return (height - panelH()) / 2;
    }

    @Override public void render(DrawContext c, int mouseX, int mouseY, float delta) {
        entrance = Math.min(1f, entrance + delta * 2.5f);
        int w = panelW(), h = panelH();
        if (w < 380 || h < 260) {
            super.render(c, mouseX, mouseY, delta);
            return;
        }
        int x = panelX(), y = panelY();

        // Backdrop: vanilla Screen already blurred once this frame (calling
        // applyBlur() again crashes with "Can only blur once per frame"),
        // so we only add dimming and a soft vignette on top.
        c.fill(0, 0, width, height, 0xB8070910);
        c.fillGradient(0, 0, width, height / 4, 0x50000000, 0x00000000);
        c.fillGradient(0, height - height / 4, width, height, 0x00000000, 0x50000000);

        // Soft glow and the window shell, all smooth rounded shapes.
        Rounded.draw(c, x - 10, y - 8, w + 20, h + 16, 18, 0x14B18CFF);
        Rounded.draw(c, x - 5, y - 4, w + 10, h + 8, 15, 0x1EB18CFF);
        Rounded.draw(c, x - 1, y - 1, w + 2, h + 2, 12, PANEL_EDGE);
        Rounded.draw(c, x, y, w, h, 11, PANEL);
        Rounded.draw(c, x + 40, y + 4, w - 80, 8, 4, 0x22B18CFF);
        Rounded.draw(c, x + 40, y + 2, w - 80, 2, 1, ACCENT);

        // Floating sidebar panel.
        Rounded.draw(c, x + 8, y + 10, 162, h - 20, 9, SIDEBAR);

        // Brand block.
        Rounded.draw(c, x + 19, y + 20, 24, 24, 7, 0xFF302648);
        Rounded.draw(c, x + 21, y + 22, 20, 20, 6, 0xFF211D31);
        c.drawTextWithShadow(textRenderer, "A", x + 28, y + 27, 0xFFC5A8FF);
        c.drawTextWithShadow(textRenderer, "AURORA", x + 52, y + 21, WHITE);
        c.drawText(textRenderer, "CLIENT  •  1.21.11", x + 52, y + 35, FAINT, false);
        c.fill(x + 18, y + 57, x + 156, y + 58, 0xFF282D3A);
        c.drawText(textRenderer, "РАЗДЕЛЫ", x + 20, y + 70, FAINT, false);

        for (int i = 0; i < TABS.length; i++) {
            int ty = y + 88 + i * 32;
            boolean selected = activeTab == i;
            boolean hover = mouseX >= x + 12 && mouseX <= x + 161 && mouseY >= ty - 4 && mouseY < ty + 23;
            tabHover[i] = Rounded.approach(tabHover[i], hover || selected ? 1 : 0, delta, 6);
            if (selected) {
                Rounded.draw(c, x + 12, ty - 4, 149, 27, 7, TAB_SELECTED);
                Rounded.draw(c, x + 15, ty + 1, 3, 17, 1, ACCENT);
            } else if (tabHover[i] > 0.01f) {
                Rounded.draw(c, x + 12, ty - 4, 149, 27, 7, Rounded.lerp(0x001B202C, TAB_HOVER, tabHover[i]));
            }
            int iconColor = selected ? 0xFFC9B1FF : Rounded.lerp(FAINT, SOFT, tabHover[i]);
            c.drawText(textRenderer, tabIcon(i), x + 23, ty + 3, iconColor, false);
            c.drawText(textRenderer, TABS[i], x + 43, ty + 3, selected ? WHITE : SOFT, false);
            if (selected) {
                c.drawText(textRenderer, "›", x + 145, ty + 2, ACCENT, false);
            }
        }
        int sideBottom = y + h - 54;
        c.fill(x + 17, sideBottom, x + 157, sideBottom + 1, 0xFF282D3A);
        Rounded.draw(c, x + 19, sideBottom + 13, 7, 7, 3, 0xFF77D9AC);
        c.drawText(textRenderer, "ВИЗУАЛЬНЫЙ РЕЖИМ", x + 33, sideBottom + 12, SOFT, false);
        c.drawText(textRenderer, "3 модуля работают", x + 19, sideBottom + 29, FAINT, false);

        int mx = x + 194;
        c.drawText(textRenderer, "AURORA  /  " + TABS[activeTab].toUpperCase(), mx, y + 20, FAINT, false);
        c.drawTextWithShadow(textRenderer, activeTab == 0 ? "Добро пожаловать" : TABS[activeTab], mx, y + 37, WHITE);
        c.drawText(textRenderer, activeTab == 0 ? "Включай модули кликом по карточке." : "Клик по карточке включает модуль.",
                mx, y + 55, SOFT, false);

        // Small status tile and decorative version tile.
        int right = x + w - 21;
        Rounded.draw(c, right - 168, y + 19, 86, 24, 6, 0xFF191E29);
        Rounded.draw(c, right - 158, y + 28, 6, 6, 3, 0xFF77D9AC);
        c.drawText(textRenderer, "ONLINE", right - 144, y + 27, SOFT, false);
        Rounded.draw(c, right - 73, y + 19, 73, 24, 6, 0xFF191E29);
        c.drawText(textRenderer, "v1.0.0", right - 61, y + 27, 0xFFC2A8FF, false);

        // Search field, visual only; the counter shows enabled modules.
        int gridTop = y + 89;
        Rounded.draw(c, mx, gridTop - 1, right - mx, 26, 8, 0xFF171B25);
        c.drawText(textRenderer, "⌕", mx + 10, gridTop + 7, ACCENT, false);
        c.drawText(textRenderer, "Поиск модулей...", mx + 27, gridTop + 7, FAINT, false);
        String counter = "ВКЛ · " + String.format("%02d", enabledCount());
        c.drawText(textRenderer, counter, right - textRenderer.getWidth(counter) - 10, gridTop + 7, FAINT, false);

        String[] names = NAMES[activeTab];
        int cardW = (right - mx - 10) / 2;
        int cardH = 58;
        for (int i = 0; i < 6; i++) {
            int col = i % 2, row = i / 2;
            int bx = mx + col * (cardW + 10), by = gridTop + 37 + row * (cardH + 9);
            boolean hover = mouseX >= bx && mouseX <= bx + cardW && mouseY >= by && mouseY <= by + cardH;
            cardHover[i] = Rounded.approach(cardHover[i], hover ? 1 : 0, delta, 6);
            Module module = live[activeTab][i];
            boolean enabled = module != null && module.isEnabled();
            float target = enabled ? 1 : 0;
            switchAnim[activeTab][i] = Rounded.approach(switchAnim[activeTab][i], target, delta, 5);
            float p = switchAnim[activeTab][i];

            Rounded.draw(c, bx, by, cardW, cardH, 9, Rounded.lerp(CARD, CARD_HOVER, cardHover[i]));
            int stripBase = enabled ? ACCENT : 0xFF3A3F4D;
            Rounded.draw(c, bx + 8, by + 12, 3, cardH - 24, 1, Rounded.lerp(stripBase, ACCENT, cardHover[i] * 0.35f));
            Rounded.draw(c, bx + 16, by + 12, 20, 20, 6, 0xFF282237);
            String icon = cardIcon(i);
            c.drawText(textRenderer, icon, bx + 26 - textRenderer.getWidth(icon) / 2, by + 18, 0xFFC6ACFF, false);
            c.drawText(textRenderer, names[i], bx + 42, by + 12, WHITE, false);
            if (module == null) {
                c.drawText(textRenderer, "В разработке", bx + 42, by + 29, FAINT, false);
            } else {
                c.drawText(textRenderer, module.description(), bx + 42, by + 29, enabled ? LIVE : SOFT, false);
            }

            // Sliding switch: live for real modules, muted for placeholders.
            int trackX = bx + cardW - 35, trackY = by + 19;
            int track = module == null ? 0xFF303645 : Rounded.lerp(0xFF303645, 0xFF4A3566, p);
            Rounded.draw(c, trackX, trackY, 22, 12, 6, track);
            int knobX = trackX + 2 + Math.round(10 * (module == null ? 0 : p));
            int knob = module == null ? 0xFF5A6272 : Rounded.lerp(0xFF7B8494, ACCENT, p);
            Rounded.draw(c, knobX, trackY + 2, 8, 8, 4, knob);
        }

        // Footer bar, tiny animated accent and controls hint.
        int fy = y + h - 30;
        c.fill(mx, fy - 7, right, fy - 6, 0xFF282D3A);
        int pulse = (int) (4 + 12 * (0.5 + 0.5 * Math.sin((System.currentTimeMillis() - startedAt) / 420.0)));
        c.fill(mx, fy - 7, mx + pulse, fy - 6, ACCENT);
        c.drawText(textRenderer, "AURORA  •  EARLY ACCESS", mx, fy + 2, FAINT, false);
        c.drawText(textRenderer, "RIGHT SHIFT  ·  МЕНЮ", right - 116, fy + 2, 0xFFB9A1EE, false);
    }

    private int enabledCount() {
        ModuleManager modules = AuroraClient.modules();
        return modules == null ? 0 : modules.enabledCount();
    }

    private String tabIcon(int i) {
        return new String[]{"⌂", "⚔", "↗", "◈", "♙", "⚙"}[i];
    }

    private String cardIcon(int i) {
        return new String[]{"✦", "↗", "◉", "◇", "⌘", "＋"}[i];
    }

    @Override public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x(), mouseY = click.y();
        int w = panelW();
        int x = panelX(), y = panelY();
        for (int i = 0; i < TABS.length; i++) {
            int ty = y + 88 + i * 32;
            if (mouseX >= x + 12 && mouseX <= x + 161 && mouseY >= ty - 4 && mouseY < ty + 23) {
                if (activeTab != i) {
                    activeTab = i;
                    for (int k = 0; k < cardHover.length; k++) {
                        cardHover[k] = 0;
                    }
                }
                return true;
            }
        }
        int mx = x + 194, right = x + w - 21, gridTop = y + 89;
        int cardW = (right - mx - 10) / 2, cardH = 58;
        for (int i = 0; i < 6; i++) {
            int col = i % 2, row = i / 2;
            int bx = mx + col * (cardW + 10), by = gridTop + 37 + row * (cardH + 9);
            if (mouseX >= bx && mouseX <= bx + cardW && mouseY >= by && mouseY <= by + cardH) {
                Module module = live[activeTab][i];
                if (module != null) {
                    module.toggle();
                    return true;
                }
                break;
            }
        }
        return super.mouseClicked(click, doubled);
    }
}
