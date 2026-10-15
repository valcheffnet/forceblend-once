package com.mauro0805.forceblend.mixin.client;

import com.mauro0805.forceblend.client.WorldOpenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Minecraft 26.1-26.3 adapter for Mojang's unobfuscated runtime.
 * ForceBlend Once: injects by method signature instead of the local variable name, which is not available at runtime on 26.3.
 */
@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.WorldOpenFlows", remap = false)
@SuppressWarnings("unused")
abstract class WorldOpenFlows26WorldOpenMixin {
    @Inject(method = "openWorld(Ljava/lang/String;Ljava/lang/Runnable;)V", at = @At("HEAD"))
    private void forceblend$beforeWorldOpen(String levelId, Runnable onCancel, CallbackInfo ci) {
        WorldOpenHandler.beforeWorldOpen(levelId);
    }
}
