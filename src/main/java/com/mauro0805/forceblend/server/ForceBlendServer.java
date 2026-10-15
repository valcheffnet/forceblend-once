package com.mauro0805.forceblend.server;

import com.mauro0805.forceblend.WorldPreparer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * ForceBlend Once on a dedicated server: runs while mods initialize, before the server opens its world.
 * The world folder is level-name from server.properties, next to the server jar.
 */
public final class ForceBlendServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Path gameDirectory = FabricLoader.getInstance().getGameDir().toAbsolutePath().normalize();
        String levelName = "world";
        Path properties = gameDirectory.resolve("server.properties");
        if (Files.isRegularFile(properties)) {
            Properties values = new Properties();
            try (Reader reader = Files.newBufferedReader(properties, StandardCharsets.UTF_8)) {
                values.load(reader);
            } catch (IOException exception) {
                throw new IllegalStateException("ForceBlend Once could not read " + properties, exception);
            }
            levelName = values.getProperty("level-name", "world").trim();
        }
        Path worldDirectory = gameDirectory.resolve(levelName).normalize();
        if (!worldDirectory.startsWith(gameDirectory)) {
            throw new IllegalArgumentException("level-name resolves outside the server directory: " + levelName);
        }
        if (!Files.isDirectory(worldDirectory)) {
            WorldPreparer.LOGGER.info("No world at {} yet, nothing for ForceBlend Once to prepare", worldDirectory);
            return;
        }
        WorldPreparer.prepare(levelName, worldDirectory);
    }
}
