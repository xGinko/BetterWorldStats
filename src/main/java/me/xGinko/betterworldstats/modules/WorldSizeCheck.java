package me.xGinko.betterworldstats.modules;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.config.Config;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class WorldSizeCheck implements BetterWorldStatsModule {

    private final Config config;
    private WrappedTask scanTask;

    protected WorldSizeCheck() {
        this.config = BetterWorldStats.getConfiguration();
    }

    @Override
    public void enable() {
        this.scanTask = new FoliaLib(BetterWorldStats.getInstance()).getImpl().runTimerAsync(() -> {
            double fileSize = count() / 1048576.0D / 1000.0D;
            BetterWorldStats.worldSize().set(fileSize);

            if (config.log_is_enabled) {
                 BetterWorldStats.getLog().info(
                         "Updated filesize asynchronously "
                        + "(Real size: " + config.filesize_display_format.format(fileSize) + "GB, "
                        + "Spoofed size: " + config.filesize_display_format.format(fileSize + config.additional_spoofed_filesize) + "GB). "
                        + "Unique player joins: " + BetterWorldStats.uniquePlayerCount().get()
                );
            }
        }, 0L, config.filesize_update_period_seconds, TimeUnit.SECONDS);
    }

    @Override
    public void disable() {
        if (scanTask != null) scanTask.cancel();
    }

    private double count() {
        final AtomicLong atomicLong = new AtomicLong(0L);
        config.directories_to_scan.forEach(directory -> {
            File worldFolder = new File(directory);
            try {
                File[] files = worldFolder.listFiles();
                if (files == null) {
                    BetterWorldStats.getLog().warning("Pathname '"+directory+"' is not a folder or directory. Skipping it.");
                    return;
                }
                for (File file : files) {
                    if (file.isFile()) {
                        atomicLong.addAndGet(file.length());
                    }
                }
            } catch (SecurityException e) {
                BetterWorldStats.getLog().severe("Could not read files in directory '"+directory+"' because access was denied.");
            }
        });
        return atomicLong.get();
    }
}