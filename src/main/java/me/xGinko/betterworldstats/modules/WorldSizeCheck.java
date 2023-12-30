package me.xGinko.betterworldstats.modules;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.config.Config;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class WorldSizeCheck implements BetterWorldStatsModule {

    private final Config config;
    private WrappedTask scanTask;

    protected WorldSizeCheck() {
        this.config = BetterWorldStats.getConfiguration();
    }

    @Override
    public void enable() {
        this.scanTask = new FoliaLib(BetterWorldStats.getInstance()).getImpl().runTimerAsync(() -> {
            final double fileSize = this.getTotalGBSize();
            BetterWorldStats.worldSize.set(fileSize);

            if (config.log_is_enabled) {
                 BetterWorldStats.getLog().info(
                         "Updated filesize asynchronously "
                        + "(Real size: " + config.filesize_display_format.format(fileSize) + "GB, "
                        + "Spoofed size: " + config.filesize_display_format.format(fileSize + config.additional_spoofed_filesize) + "GB). "
                        + "Unique player joins: " + BetterWorldStats.uniquePlayerCount.get()
                );
            }
        }, 0L, config.filesize_update_period_seconds, TimeUnit.SECONDS);
    }

    @Override
    public void disable() {
        if (scanTask != null) scanTask.cancel();
    }

    private double getTotalGBSize() {
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