package me.xginko.betterworldstats.stats;

import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.utils.Disableable;
import me.xginko.betterworldstats.utils.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class WorldStats implements Runnable, Disableable {

    private static final boolean CAN_GET_CHUNK_COUNT, CAN_GET_ENTITY_COUNT;

    static {
        CAN_GET_CHUNK_COUNT = Util.hasMethod(World.class, "getChunkCount");
        CAN_GET_ENTITY_COUNT = Util.hasMethod(World.class, "getEntityCount");
    }

    private final ScheduledTask scheduledScan;
    private final AtomicInteger chunkCount, entityCount;
    private @Nullable FileScanResult fileScanResult;

    public WorldStats() {
        this.chunkCount = new AtomicInteger();
        this.entityCount = new AtomicInteger();
        this.scheduledScan = BetterWorldStats.scheduling().asyncScheduler().runAtFixedRate(this,
                Duration.ofMillis(1L), Duration.ofMillis(BetterWorldStats.config().filesizeUpdatePeriodMillis));
    }

    @Override
    public void disable() {
        scheduledScan.cancel();
    }

    @Override
    public void run() {
        if (CAN_GET_ENTITY_COUNT || CAN_GET_CHUNK_COUNT) {
            BetterWorldStats.scheduling().globalRegionalScheduler().run(() -> { // Needs to be sync
                chunkCount.set(0);
                entityCount.set(0);
                for (World world : BetterWorldStats.getInstance().getServer().getWorlds()) {
                    if (CAN_GET_CHUNK_COUNT)
                        chunkCount.addAndGet(world.getChunkCount());
                    if (CAN_GET_ENTITY_COUNT)
                        entityCount.addAndGet(world.getEntityCount());
                }
            });
        }

        fileScanResult = new FileScanResult(BetterWorldStats.config().scanPaths, BetterWorldStats.config().filesizeUpdatePeriodMillis);

        if (BetterWorldStats.config().doLogging) {
            BetterWorldStats.logger().info(Component.text("Updated file stats asynchronously.", Util.GUPPIE_GREEN));
            BetterWorldStats.logger().info(Component.text(
                    "Size: " + BetterWorldStats.config().filesizeFormat.format(fileScanResult.sizeInGb) + "GB, " +
                            "files: " + fileScanResult.fileCount + ", " +
                            "folders: " + fileScanResult.folderCount + ", " +
                            "chunks: " + chunkCount + ", " +
                            "entities: " + entityCount, Util.GUPPIE_GREEN));
        }
    }

    public String getSize() {
        if (fileScanResult == null) return "";

        return BetterWorldStats.config().filesizeFormat.format(fileScanResult.sizeInGb);
    }

    public String getSpoofedSize() {
        if (fileScanResult == null) return "";

        return BetterWorldStats.config().filesizeFormat
                .format(fileScanResult.sizeInGb + BetterWorldStats.config().additionalSpoofFilesize);
    }

    public String getFolderCount() {
        if (fileScanResult == null) return "";

        return Integer.toString(fileScanResult.folderCount);
    }

    public String getFileCount() {
        if (fileScanResult == null) return "";

        return Integer.toString(fileScanResult.fileCount);
    }

    public String getChunkCount() {
        if (fileScanResult == null) return "";

        return CAN_GET_CHUNK_COUNT ? chunkCount.toString() : "unsupported";
    }

    public String getEntityCount() {
        if (fileScanResult == null) return "";

        return CAN_GET_ENTITY_COUNT ? entityCount.toString() : "unsupported";
    }

    private static class FileScanResult {

        public final long expiration_time_millis;
        public final double sizeInGb;
        public int fileCount, folderCount;

        protected FileScanResult(@NotNull Iterable<String> paths_to_scan, long cooldown_millis) {
            this.fileCount = this.folderCount = 0;
            long byteSize = 0L;
            for (String path : paths_to_scan) {
                byteSize += this.getByteSize(new File(path));
            }
            this.sizeInGb = byteSize / 1048576.0D / 1000.0D;
            this.expiration_time_millis = System.currentTimeMillis() + cooldown_millis;
        }

        private long getByteSize(File file) {
            long bytes = 0L;

            if (file.isDirectory()) {
                this.folderCount++;
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
                this.fileCount++;
                bytes += file.length();
            }

            return bytes;
        }
    }
}