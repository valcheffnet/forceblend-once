package com.mauro0805.forceblend.client;

import com.mauro0805.forceblend.WorldPreparer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

/**
 * Loader-facing world lifecycle bridge shared by every version-specific mixin.
 */
public final class WorldOpenHandler {
    private WorldOpenHandler() {
    }

    public static void beforeWorldOpen(String levelId) {
        Path savesDirectory = FabricLoader.getInstance().getGameDir().resolve("saves").toAbsolutePath().normalize();
        Path worldDirectory = savesDirectory.resolve(levelId).normalize();
        if (!worldDirectory.startsWith(savesDirectory)) {
            throw new IllegalArgumentException("World id resolves outside the Minecraft saves directory: " + levelId);
        }
        WorldPreparer.prepare(levelId, worldDirectory, FabricLoader.getInstance()
            .getModContainer("minecraft")
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElseThrow(() -> new IllegalStateException("Minecraft version metadata is unavailable")));
    }
}
