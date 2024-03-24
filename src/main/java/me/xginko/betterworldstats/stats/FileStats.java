package me.xginko.betterworldstats.stats;

import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.config.Config;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class FileStats {

    private final @NotNull Config config;
    private final @NotNull AtomicReference<ScanResult> recentScan;
    private final @NotNull AtomicLong cooldown;

    public FileStats() {
        this.config = BetterWorldStats.getConfiguration();
        this.recentScan = new AtomicReference<>(new ScanResult(config.paths_to_scan));
        this.cooldown = new AtomicLong(System.currentTimeMillis() + config.filesize_update_period_millis);
    }

    private void onRequest() {
        if (cooldown.get() <= System.currentTimeMillis()) {
            CompletableFuture.supplyAsync(() -> {
                cooldown.set(System.currentTimeMillis() + config.filesize_update_period_millis);
                return new ScanResult(config.paths_to_scan);
            }).thenAccept(recentScan::set);
        }
    }

    public String getSize() {
        onRequest();
        return config.filesize_format.format(recentScan.get().sizeInGB);
    }

    public String getSpoofedSize() {
        onRequest();
        return config.filesize_format.format(recentScan.get().sizeInGB + config.additional_spoof_filesize);
    }

    public String getFolderCount() {
        onRequest();
        return Integer.toString(recentScan.get().foldersTotal);
    }

    public String getFileCount() {
        onRequest();
        return Integer.toString(recentScan.get().filesTotal);
    }

    public String getChunkFileCount() {
        onRequest();
        return Integer.toString(recentScan.get().chunkFilesTotal);
    }

    private static class ScanResult {
        public final double sizeInGB;
        public int filesTotal, chunkFilesTotal, foldersTotal;

        protected ScanResult(@NotNull Iterable<String> paths_to_scan) {
            this.filesTotal = this.chunkFilesTotal = this.foldersTotal = 0;
            long byteSize = 0L;
            for (String path : paths_to_scan)
                byteSize += this.getByteSize(new File(path));
            this.sizeInGB = byteSize / 1048576.0D / 1000.0D;
        }

        private long getByteSize(File file) {
            long bytes = 0L;
            if (file.isFile()) {
                this.filesTotal++; // Count file
                if (file.getName().endsWith(".mca")) { // Check if is chunk file
                    final File parent = file.getParentFile();
                    if (parent.isDirectory() && parent.getName().toLowerCase().contains("region")) {
                        this.chunkFilesTotal++;
                    }
                }
                bytes += file.length();
                return bytes;
            }
            if (file.isDirectory()) {
                this.foldersTotal++; // Count folder
                try {
                    File[] subFiles = file.listFiles();
                    assert subFiles != null;
                    for (File subFile : subFiles) {
                        bytes += this.getByteSize(subFile);
                    }
                } catch (SecurityException e) {
                    BetterWorldStats.getLog().error("Could not read directory '"+file.getPath()+"'.", e);
                }
            }
            return bytes;
        }
    }
}