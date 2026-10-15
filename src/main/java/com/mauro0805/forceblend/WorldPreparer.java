package com.mauro0805.forceblend;

import com.mauro0805.forceblend.processing.ProcessingReport;
import com.mauro0805.forceblend.processing.WorldProcessor;
import com.mauro0805.forceblend.version.BlendingProfile;
import com.mauro0805.forceblend.version.SupportedMinecraftVersions;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ForceBlend Once: shared once-per-world preparation, used by the client world-open hook and the dedicated server start.
 */
public final class WorldPreparer {
    public static final Logger LOGGER = LoggerFactory.getLogger("forceblendonce");
    private static final Path MODERN_OVERWORLD_PATH = Path.of(
        "dimensions",
        "minecraft",
        "overworld"
    );

    // ForceBlend Once: a world is prepared a single time. After that the marker file makes every later open a no-op.
    private static final String MARKER_FILE = "forceblendonce.json";

    private WorldPreparer() {
    }

    public static void prepare(String levelId, Path worldDirectory) {
        Path modernOverworld = worldDirectory.resolve(MODERN_OVERWORLD_PATH);
        Path overworldDirectory = Files.isDirectory(modernOverworld.resolve("region"))
            ? modernOverworld
            : worldDirectory;
        Path marker = worldDirectory.resolve(MARKER_FILE);
        if (Files.exists(marker)) {
            LOGGER.info("World '{}' was already prepared by ForceBlend Once, skipping", levelId);
            return;
        }

        String minecraftVersion = FabricLoader.getInstance()
            .getModContainer("minecraft")
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElseThrow(() -> new IllegalStateException("Minecraft version metadata is unavailable"));
        BlendingProfile profile = SupportedMinecraftVersions.profileFor(minecraftVersion);
        long startNanos = System.nanoTime();

        LOGGER.info(
            "Scanning the current ForceBlend terrain frontier for world '{}' at {} using the {} marker",
            levelId,
            overworldDirectory,
            profile.format()
        );
        try {
            Path backup = backupRegions(worldDirectory, overworldDirectory.resolve("region"));
            ProcessingReport report = WorldProcessor.process(levelId, overworldDirectory, profile);
            writeMarker(marker, minecraftVersion, report, backup);
            long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
            if (report.regionFiles() == 0) {
                LOGGER.info(
                    "No existing Overworld region files require processing for world '{}' at {}",
                    levelId,
                    overworldDirectory.resolve("region")
                );
            }
            LOGGER.info(
                "ForceBlend completed the frontier scan for world '{}': {} regions, {} chunks discovered, "
                    + "{} complete frontier chunks selected, {} prepared, {} unsafe markers repaired, "
                    + "{} partial, interior, or native-migration chunks skipped, {} already prepared ({} ms)",
                levelId,
                report.regionFiles(),
                report.discoveredChunks(),
                report.frontierChunks(),
                report.preparedChunks(),
                report.repairedChunks(),
                report.skippedChunks(),
                report.alreadyPreparedChunks(),
                elapsedMillis
            );
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException(
                "ForceBlend could not safely prepare the Overworld for world '" + levelId + "'",
                exception
            );
        }
    }

    /** Zips the Overworld region files next to the world before anything is changed. Returns null when there is nothing to save. */
    private static Path backupRegions(Path worldDirectory, Path regionDirectory) throws IOException {
        if (!Files.isDirectory(regionDirectory)) {
            return null;
        }
        List<Path> regions;
        try (Stream<Path> files = Files.list(regionDirectory)) {
            regions = files.filter(file -> file.getFileName().toString().endsWith(".mca")).sorted().toList();
        }
        if (regions.isEmpty()) {
            return null;
        }
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Path backup = worldDirectory.resolve("forceblendonce-backup-" + stamp + ".zip");
        try (OutputStream out = Files.newOutputStream(backup); ZipOutputStream zip = new ZipOutputStream(out)) {
            for (Path region : regions) {
                zip.putNextEntry(new ZipEntry("region/" + region.getFileName()));
                Files.copy(region, zip);
                zip.closeEntry();
            }
        }
        LOGGER.info("ForceBlend Once saved {} region files to {}", regions.size(), backup);
        return backup;
    }

    private static void writeMarker(Path marker, String minecraftVersion, ProcessingReport report, Path backup) throws IOException {
        String json = String.join("\n",
            "{",
            "  \"prepared\": \"" + LocalDateTime.now() + "\",",
            "  \"minecraft\": \"" + minecraftVersion + "\",",
            "  \"preparedChunks\": " + report.preparedChunks() + ",",
            "  \"backup\": " + (backup == null ? "null" : "\"" + backup.getFileName() + "\""),
            "}",
            "");
        Files.writeString(marker, json, StandardCharsets.UTF_8);
    }
}
