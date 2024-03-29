package me.xginko.betterworldstats.stats;

import io.papermc.lib.PaperLib;
import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.config.Config;
import me.xginko.betterworldstats.utils.KyoriUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class WorldStats {

    private final @NotNull Config config;
    private final @NotNull AtomicReference<FileScanResult> scan_result;
    private final @NotNull AtomicInteger chunk_count, entity_count;
    private @Nullable CompletableFuture<Void> runningScan;

    public WorldStats() {
        this.config = BetterWorldStats.getConfiguration();
        this.scan_result = new AtomicReference<>();
        this.chunk_count = new AtomicInteger();
        this.entity_count = new AtomicInteger();
        this.refresh();
    }

    private void refresh() {
        if (scan_result.get() != null && scan_result.get().expiration_time_millis > System.currentTimeMillis()) {
            return;
        }

        if (runningScan != null && !runningScan.isDone() && !runningScan.isCompletedExceptionally() && !runningScan.isCancelled()) {
            return;
        }

        this.runningScan = CompletableFuture
                .supplyAsync(() -> new FileScanResult(config.paths_to_scan, config.filesize_update_period_millis))
                .thenAccept(result -> {
                    scan_result.set(result);
                    if (config.log_is_enabled) {
                        BetterWorldStats.getLog().info(Component.text(
                                "Updated file stats asynchronously.").color(KyoriUtil.GUPPIE_GREEN));
                        BetterWorldStats.getLog().info(Component.text(
                                "Size: " + config.filesize_format.format(result.size_in_gb) + "GB, " +
                                        "files: " + result.file_count + ", " +
                                        "folders: " + result.folder_count + ", " +
                                        "chunks: " + chunk_count + ", " +
                                        "entities: " + entity_count).color(KyoriUtil.GUPPIE_GREEN));
                    }
                });

        if (PaperLib.isPaper()) {
            for (final World world : BetterWorldStats.getInstance().getServer().getWorlds()) {
                this.chunk_count.addAndGet(world.getChunkCount());
                this.entity_count.addAndGet(world.getEntityCount());
            }
        }
    }

    public String getSize() {
        refresh();
        return config.filesize_format.format(scan_result.get().size_in_gb);
    }

    public String getSpoofedSize() {
        refresh();
        return config.filesize_format.format(scan_result.get().size_in_gb + config.additional_spoof_filesize);
    }

    public String getFolderCount() {
        refresh();
        return Integer.toString(scan_result.get().folder_count);
    }

    public String getFileCount() {
        refresh();
        return Integer.toString(scan_result.get().file_count);
    }

    public String getChunkCount() {
        refresh();
        return PaperLib.isPaper() ? chunk_count.toString() : "unsupported";
    }

    public String getEntityCount() {
        refresh();
        return PaperLib.isPaper() ? entity_count.toString() : "unsupported";
    }

    private static class FileScanResult {

        public final long expiration_time_millis;
        public final double size_in_gb;
        public int file_count, folder_count;

        protected FileScanResult(@NotNull Iterable<String> paths_to_scan, long cooldown_millis) {
            this.file_count = this.folder_count = 0;
            long byteSize = 0L;
            for (String path : paths_to_scan)
                byteSize += this.getByteSize(new File(path));
            this.size_in_gb = byteSize / 1048576.0D / 1000.0D;
            this.expiration_time_millis = System.currentTimeMillis() + cooldown_millis;
        }

        private long getByteSize(File file) {
            long bytes = 0L;

            if (file.isDirectory()) {
                this.folder_count++;
                try {
                    File[] subFiles = file.listFiles();
                    assert subFiles != null;
                    for (File subFile : subFiles) {
                        bytes += this.getByteSize(subFile);
                    }
                } catch (Throwable t) {
                    BetterWorldStats.getLog().warn("Unable to stat directory '"+file.getPath()+"'.", t);
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