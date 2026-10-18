package com.mauro0805.forceblend.neoforge;

import com.mauro0805.forceblend.WorldPreparer;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import java.nio.file.Path;

/**
 * ForceBlend Once on NeoForge. ServerAboutToStartEvent fires before the server loads its levels, both for the
 * integrated singleplayer server and for a dedicated server, so one hook covers both.
 */
@Mod("forceblendonce")
public final class ForceBlendOnceNeoForge {
    public ForceBlendOnceNeoForge(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(ForceBlendOnceNeoForge::beforeServerStart);
    }

    private static void beforeServerStart(ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        Path worldDirectory = server.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize();
        String levelId = worldDirectory.getFileName() == null ? "world" : worldDirectory.getFileName().toString();
        WorldPreparer.prepare(levelId, worldDirectory, SharedConstants.getCurrentVersion().name());
    }
}
