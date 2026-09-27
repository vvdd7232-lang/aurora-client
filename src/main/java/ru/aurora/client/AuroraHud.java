package ru.aurora.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public final class AuroraHud implements HudElement {
    private static final Identifier ID = Identifier.of("aurora", "hud");

    public static void register() {
        HudElementRegistry.addLast(ID, new AuroraHud());
    }

    @Override
    public void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null || mc.world == null) {
            return;
        }
        if (mc.options.hudHidden) {
            return;
        }
        ModuleManager modules = AuroraClient.modules();
        if (modules == null) {
            return;
        }
        if (modules.isEnabled("hud")) {
            renderInfo(context, mc);
        }
        if (modules.isEnabled("crosshair")) {
            renderCrosshair(context);
        }
    }

    private void renderInfo(DrawContext context, MinecraftClient mc) {
        TextRenderer text = mc.textRenderer;
        int ping = pingOf(mc);
        BlockPos pos = mc.player.getBlockPos();
        String[] lines = {
                "AURORA",
                mc.getCurrentFps() + " FPS",
                (ping >= 0 ? ping + " MS" : "--- MS"),
                "XYZ " + pos.getX() + " / " + pos.getY() + " / " + pos.getZ(),
                mc.player.getHorizontalFacing().getName().toUpperCase()
        };
        int width = 0;
        for (String line : lines) {
            width = Math.max(width, text.getWidth(line));
        }
        int x = 8;
        int y = 8;
        int pad = 6;
        int lineH = 11;
        int height = lines.length * lineH + pad * 2 - 4;
        Rounded.draw(context, x - pad, y - pad, width + pad * 2, height, 6, 0xB30D1018);
        Rounded.draw(context, x - pad, y - pad + 6, 2, height - 12, 1, 0xFFB18CFF);
        for (int i = 0; i < lines.length; i++) {
            context.drawTextWithShadow(text, lines[i], x, y + i * lineH, i == 0 ? 0xFFC9B1FF : 0xFFF4F5FF);
        }
    }

    private int pingOf(MinecraftClient mc) {
        try {
            if (mc.getNetworkHandler() == null || mc.player == null) {
                return -1;
            }
            UUID uuid = mc.player.getUuid();
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(uuid);
            return entry == null ? -1 : entry.getLatency();
        } catch (Throwable t) {
            return -1;
        }
    }

    private void renderCrosshair(DrawContext context) {
        int cx = context.getScaledWindowWidth() / 2;
        int cy = context.getScaledWindowHeight() / 2;
        int gap = 3;
        int len = 5;
        int dark = 0xCC0B0D12;
        int light = 0xFFF4F5FF;
        // Left arm.
        context.fill(cx - gap - len - 1, cy - 1, cx - gap + 1, cy + 2, dark);
        context.fill(cx - gap - len, cy, cx - gap, cy + 1, light);
        // Right arm.
        context.fill(cx + gap - 1, cy - 1, cx + gap + len + 1, cy + 2, dark);
        context.fill(cx + gap, cy, cx + gap + len, cy + 1, light);
        // Top arm.
        context.fill(cx - 1, cy - gap - len - 1, cx + 2, cy - gap + 1, dark);
        context.fill(cx, cy - gap - len, cx + 1, cy - gap, light);
        // Bottom arm.
        context.fill(cx - 1, cy + gap - 1, cx + 2, cy + gap + len + 1, dark);
        context.fill(cx, cy + gap, cx + 1, cy + gap + len, light);
        // Center dot.
        context.fill(cx - 1, cy - 1, cx + 2, cy + 2, dark);
        context.fill(cx, cy, cx + 1, cy + 1, light);
    }
}
