package me.xGinko.betterworldstats.modules;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.WrappedTask;
import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.config.Config;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class WorldSizeCheck implements BetterWorldStatsModule {

    private final FoliaLib foliaLib;
    private final Config config;
    private WrappedTask scanTask;

    protected WorldSizeCheck() {
        this.foliaLib = new FoliaLib(BetterWorldStats.getInstance());
        this.config = BetterWorldStats.getConfiguration();
    }

    @Override
    public void enable() {
        this.scanTask = foliaLib.getImpl().runTimerAsync(() -> {
            double fileSize = count() / 1048576.0D / 1000.0D;
            BetterWorldStats.setWorldFileSize(fileSize);

            if (config.log_is_enabled) {
                 BetterWorldStats.getLog().info(
                         "Updated filesize asynchronously "
                        + "(Real size: " + config.filesize_display_format.format(fileSize) + "GB, "
                        + "Spoofed size: " + config.filesize_display_format.format(fileSize + config.additional_spoofed_filesize) + "GB). "
                        + "Unique player joins: " + BetterWorldStats.getUniquePlayers()
                );
            }
        }, 0L, config.filesize_update_period_seconds, TimeUnit.SECONDS);
    }

    @Override
    public void disable() {
        if (scanTask != null) scanTask.cancel();
    }

    private long count() {
        final AtomicLong atomicLong = new AtomicLong(0L);
        for (String path : config.directories_to_scan) {
            for (File file : Objects.requireNonNull(new File(path).listFiles())) {
                if (file.isFile()) {
                    atomicLong.addAndGet(file.length());
                }
            }
        }
        return atomicLong.get();
    }
}
