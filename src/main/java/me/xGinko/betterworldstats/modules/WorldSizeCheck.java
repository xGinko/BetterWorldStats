package me.xGinko.betterworldstats.modules;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.config.Config;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class WorldSizeCheck implements BetterWorldStatsModule, Runnable {

    private final Config config;
    private WrappedTask wrappedScanTask;

    protected WorldSizeCheck() {
        this.config = BetterWorldStats.getConfiguration();
    }

    @Override
    public void enable() {
        this.wrappedScanTask = new FoliaLib(BetterWorldStats.getInstance()).getImpl()
                .runTimerAsync(this, 0L, config.filesize_update_period_seconds, TimeUnit.SECONDS);
    }

    @Override
    public void disable() {
        if (this.wrappedScanTask != null) this.wrappedScanTask.cancel();
    }

    @Override
    public void run() {
        final double sizeInGB = this.getTotalSizeInGB();
        BetterWorldStats.worldSize.set(sizeInGB);

        if (!config.log_is_enabled) return;
        BetterWorldStats.getLog().info("Updated filesize asynchronously "
                + "(Real size: " + config.filesize_display_format.format(sizeInGB) + "GB, "
                + "Spoofed size: " + config.filesize_display_format
                .format(sizeInGB + config.additional_spoofed_filesize) + "GB). "
                + "Unique player joins: " + BetterWorldStats.uniquePlayerCount
        );
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