package me.xGinko.betterworldstats.stats;

import com.google.common.util.concurrent.AtomicDouble;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.config.Config;

import java.io.File;

public class FileSize {

    private final AtomicDouble sizeInGB;
    private final ServerImplementation serverImpl;
    private final Config config;
    private long next_possible_check_time_millis = 0L;

    public FileSize() {
        this.serverImpl = new FoliaLib(BetterWorldStats.getInstance()).getImpl();
        this.config = BetterWorldStats.getConfiguration();
        this.sizeInGB = new AtomicDouble(0.0);
    }

    public double getSpoofedSize() {
        return this.getTrueSize() + config.additional_spoof_filesize;
    }

    public double getTrueSize() {
        final long current_time_millis = System.currentTimeMillis();
        if (current_time_millis >= next_possible_check_time_millis) {
            this.next_possible_check_time_millis = current_time_millis + config.filesize_update_period_millis;
            this.serverImpl.runAsync(updateFileSize -> {
                final double totalSize = this.getTotalSizeInGB();
                this.sizeInGB.set(totalSize);
                if (config.log_is_enabled) {
                    BetterWorldStats.getLog().info("Updated world size asynchronously "
                            + "(Real size: " + config.filesize_format.format(sizeInGB) + "GB, "
                            + "Spoofed size: " + config.filesize_format
                            .format(totalSize + config.additional_spoof_filesize) + "GB). "
                    );
                }
            });
        }
        return sizeInGB.get();
    }

    private double getTotalSizeInGB() {
        long rawSize = 0L;
        for (String path : config.paths_to_scan) {
            rawSize += this.getSize(new File(path));
        }
        return rawSize / 1048576.0D / 1000.0D;
    }

    private long getSize(File file) {
        long rawSize = 0L;
        if (file.isFile()) {
            rawSize += file.length();
            return rawSize;
        }
        if (file.isDirectory()) {
            try {
                File[] subFiles = file.listFiles();
                assert subFiles != null;
                for (File subFile : subFiles) {
                    rawSize += this.getSize(subFile);
                }
            } catch (SecurityException e) {
                BetterWorldStats.getLog().severe("Could not read directory '"+file.getPath()+"' because access was denied.");
            }
        }
        return rawSize;
    }
}