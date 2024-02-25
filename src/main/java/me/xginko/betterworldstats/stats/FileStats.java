package me.xginko.betterworldstats.stats;

import com.google.common.util.concurrent.AtomicDouble;
import com.tcoded.folialib.impl.ServerImplementation;
import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.config.Config;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class FileStats {

    private final ServerImplementation scheduler;
    private final Config config;
    private final AtomicDouble sizeInGB;
    private final AtomicInteger filesTotal, chunkFiles, foldersTotal;
    private long next_possible_check_time_millis = 0L;

    public FileStats() {
        this.scheduler = BetterWorldStats.getFoliaLib().getImpl();
        this.config = BetterWorldStats.getConfiguration();
        this.sizeInGB = new AtomicDouble(0.0);
        this.filesTotal = this.chunkFiles = this.foldersTotal = new AtomicInteger(0);
        this.updateAsync(); // Check on init so values aren't 0 on first request
    }

    private void updateAsync() {
        final long current_time_millis = System.currentTimeMillis();
        // If on cooldown, do nothing
        if (current_time_millis < next_possible_check_time_millis) return;
        // Schedule check async
        this.scheduler.runAsync(updateFileSize -> {
            // Reset values so they can be updated next
            this.filesTotal.set(0);
            this.chunkFiles.set(0);
            this.foldersTotal.set(0);
            // Get total size, updating other stats in the process
            final double totalSize = this.getTotalSizeInGB();
            this.sizeInGB.set(totalSize);
            // Log
            if (config.log_is_enabled) {
                BetterWorldStats.getLog().info("Updated world size asynchronously "
                        + "(Real size: " + config.filesize_format.format(totalSize) + "GB, "
                        + "Spoofed size: " + config.filesize_format
                        .format(totalSize + config.additional_spoof_filesize) + "GB).");
            }
            // Set cooldown
            this.next_possible_check_time_millis = current_time_millis + config.filesize_update_period_millis;
        });
    }

    public double getSpoofedSize() {
        return this.getTrueSize() + config.additional_spoof_filesize;
    }

    public double getTrueSize() {
        this.updateAsync();
        return this.sizeInGB.get();
    }

    private double getTotalSizeInGB() {
        long byteSize = 0L;
        for (String path : config.paths_to_scan) {
            byteSize += this.getByteSize(new File(path));
        }
        return byteSize / 1048576.0D / 1000.0D;
    }

    private long getByteSize(File file) {
        long bytes = 0L;
        if (file.isFile()) {
            this.filesTotal.getAndIncrement(); // Count file
            if (file.getName().endsWith(".mca")) { // Check if is chunk file
                final File parent = file.getParentFile();
                if (parent.isDirectory() && parent.getName().toLowerCase().contains("region")) {
                    this.chunkFiles.getAndIncrement();
                }
            }
            bytes += file.length();
            return bytes;
        }
        if (file.isDirectory()) {
            this.foldersTotal.getAndIncrement(); // Count folder
            try {
                File[] subFiles = file.listFiles();
                assert subFiles != null;
                for (File subFile : subFiles) {
                    bytes += this.getByteSize(subFile);
                }
            } catch (SecurityException e) {
                BetterWorldStats.getLog().error("Could not read directory '"+file.getPath()+"' because access was denied.", e);
            }
        }
        return bytes;
    }

    public int getFolderCount() {
        this.updateAsync();
        return this.foldersTotal.get();
    }

    public int getFileCount() {
        this.updateAsync();
        return this.filesTotal.get();
    }

    public int getChunkFileCount() {
        this.updateAsync();
        return this.chunkFiles.get();
    }
}