package ru.aurora.client.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.aurora.client.AuroraClient;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void aurora$hideVanillaCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        try {
            if (AuroraClient.crosshairEnabled()) {
                ci.cancel();
            }
        } catch (Throwable ignored) {
            // Never break vanilla rendering.
        }
    }
}
