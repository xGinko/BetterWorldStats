package me.xginko.betterworldstats.stats;

import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.config.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class FileStats {

    private final @NotNull Config config;
    private final @NotNull AtomicReference<ScanResult> scan_result;
    private boolean scanning = false;

    public FileStats() {
        this.config = BetterWorldStats.getConfiguration();
        this.scan_result = new AtomicReference<>();
        this.refresh();
    }

    private void refresh() {
        if (System.currentTimeMillis() <= scan_result.get().expiration_time_millis || scanning) {
            return;
        }

        scanning = true;

        CompletableFuture.supplyAsync(() -> new ScanResult(config.paths_to_scan, config.filesize_update_period_millis)).thenAccept(result -> {
            scan_result.set(result);
            scanning = false;
            if (config.log_is_enabled) {
                BetterWorldStats.getLog().info(Component.text(
                        "Updated file stats asynchronously.").color(BetterWorldStats.COLOR));
                BetterWorldStats.getLog().info(Component.text(
                        "Size: " + config.filesize_format.format(result.size_in_gb) + "GB, " +
                                "files: " + result.file_count + ", " +
                                "folders: " + result.folder_count + ", " +
                                "chunks: " + result.chunk_count + ", " +
                                "entities: " + result.entity_count).color(BetterWorldStats.COLOR));
            }
        });
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
        return Integer.toString(scan_result.get().chunk_count);
    }

    public String getEntityCount() {
        refresh();
        return Integer.toString(scan_result.get().entity_count);
    }

    private static class ScanResult {

        public final long expiration_time_millis;
        public final double size_in_gb;
        public int file_count, folder_count, chunk_count, entity_count;

        protected ScanResult(@NotNull Iterable<String> paths_to_scan, long cooldown_millis) {
            this.file_count = this.chunk_count = this.folder_count = 0;

            long byteSize = 0L;
            for (String path : paths_to_scan)
                byteSize += this.getByteSize(new File(path));
            this.size_in_gb = byteSize / 1048576.0D / 1000.0D;

            for (final World world : BetterWorldStats.getInstance().getServer().getWorlds()) {
                this.chunk_count += world.getChunkCount();
                this.entity_count += world.getEntityCount();
            }

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