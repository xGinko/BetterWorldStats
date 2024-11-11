package me.xginko.betterworldstats.stats;

import io.papermc.lib.PaperLib;
import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.config.Config;
import me.xginko.betterworldstats.utils.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class WorldStats {

    private final @NotNull Config config;
    private @Nullable FileScanResult scan_result;
    private int chunk_count, entity_count;
    private boolean scanning = false;

    public WorldStats() {
        this.config = BetterWorldStats.config();
    }

    private boolean shouldScan() {
        return !scanning && (scan_result == null || System.currentTimeMillis() >= scan_result.expiration_time_millis);
    }

    public CompletableFuture<WorldStats> get() {
        return shouldScan() ? runScan() : CompletableFuture.completedFuture(this);
    }

    private CompletableFuture<WorldStats> runScan() {
        return CompletableFuture
                .supplyAsync(() -> {
                    scanning = true;
                    return new FileScanResult(config.paths_to_scan, config.filesize_update_period_millis);
                })
                .thenApply(result -> {
                    scan_result = result;
                    if (config.log_is_enabled) {
                        BetterWorldStats.logger().info(Component.text(
                                "Updated file stats asynchronously.").color(Util.GUPPIE_GREEN));
                        BetterWorldStats.logger().info(Component.text(
                                "Size: " + config.filesize_format.format(result.size_in_gb) + "GB, " +
                                        "files: " + result.file_count + ", " +
                                        "folders: " + result.folder_count + ", " +
                                        "chunks: " + chunk_count + ", " +
                                        "entities: " + entity_count).color(Util.GUPPIE_GREEN));
                    }
                    if (PaperLib.isPaper()) {
                        for (final World world : BetterWorldStats.getInstance().getServer().getWorlds()) {
                            chunk_count += world.getChunkCount();
                            entity_count += world.getEntityCount();
                        }
                    }
                    scanning = false;
                    return this;
                });
    }

    public String getSize() {
        if (shouldScan()) runScan();
        if (scan_result == null) return "";

        return config.filesize_format.format(scan_result.size_in_gb);
    }

    public String getSpoofedSize() {
        if (shouldScan()) runScan();
        if (scan_result == null) return "";

        return config.filesize_format.format(scan_result.size_in_gb + config.additional_spoof_filesize);
    }

    public String getFolderCount() {
        if (shouldScan()) runScan();
        if (scan_result == null) return "";

        return Integer.toString(scan_result.folder_count);
    }

    public String getFileCount() {
        if (shouldScan()) runScan();
        if (scan_result == null) return "";

        return Integer.toString(scan_result.file_count);
    }

    public String getChunkCount() {
        if (shouldScan()) runScan();
        if (scan_result == null) return "";

        return PaperLib.isPaper() ? Integer.toString(chunk_count) : "unsupported";
    }

    public String getEntityCount() {
        if (shouldScan()) runScan();
        if (scan_result == null) return "";

        return PaperLib.isPaper() ? Integer.toString(entity_count) : "unsupported";
    }

    private static class FileScanResult {

        public final long expiration_time_millis;
        public final double size_in_gb;
        public int file_count, folder_count;

        protected FileScanResult(@NotNull Iterable<String> paths_to_scan, long cooldown_millis) {
            this.file_count = this.folder_count = 0;
            long byteSize = 0L;
            for (String path : paths_to_scan) {
                byteSize += this.getByteSize(new File(path));
            }
            this.size_in_gb = byteSize / 1048576.0D / 1000.0D;
            this.expiration_time_millis = System.currentTimeMillis() + cooldown_millis;
        }

        private long getByteSize(File file) {
            long bytes = 0L;

            if (file.isDirectory()) {
                this.folder_count++;
                try {
                    File[] subFiles = file.listFiles();
                    for (File subFile : subFiles) {
                        bytes += this.getByteSize(subFile);
                    }
                } catch (Throwable t) {
                    BetterWorldStats.logger().warn("Unable to stat directory '{}'.", file.getPath(), t);
                }
            }

            else if (file.isFile()) {
                this.file_count++;
                bytes += file.length();
            }

            return bytes;
        }
    }
}