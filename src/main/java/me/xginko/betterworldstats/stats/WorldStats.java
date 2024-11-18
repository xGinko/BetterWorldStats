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
    private @Nullable FileStatResult fileStatResult;

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

        fileStatResult = new FileStatResult(BetterWorldStats.config().scanPaths);

        if (BetterWorldStats.config().doLogging) {
            BetterWorldStats.logger().info(Component.text("Updated file stats asynchronously.", Util.GUPPIE_GREEN));
            BetterWorldStats.logger().info(Component.text(
                    "Size: " + BetterWorldStats.config().filesizeFormat.format(fileStatResult.sizeInGb) + "GB, " +
                            "files: " + fileStatResult.fileCount + ", " +
                            "folders: " + fileStatResult.folderCount + ", " +
                            "chunks: " + chunkCount + ", " +
                            "entities: " + entityCount, Util.GUPPIE_GREEN));
        }
    }

    public String getSize() {
        if (fileStatResult == null) return "";

        return BetterWorldStats.config().filesizeFormat.format(fileStatResult.sizeInGb);
    }

    public String getSpoofedSize() {
        if (fileStatResult == null) return "";

        return BetterWorldStats.config().filesizeFormat
                .format(fileStatResult.sizeInGb + BetterWorldStats.config().additionalSpoofFilesize);
    }

    public String getFolderCount() {
        if (fileStatResult == null) return "";

        return Integer.toString(fileStatResult.folderCount);
    }

    public String getFileCount() {
        if (fileStatResult == null) return "";

        return Integer.toString(fileStatResult.fileCount);
    }

    public String getChunkCount() {
        if (fileStatResult == null) return "";

        return CAN_GET_CHUNK_COUNT ? chunkCount.toString() : "unsupported";
    }

    public String getEntityCount() {
        if (fileStatResult == null) return "";

        return CAN_GET_ENTITY_COUNT ? entityCount.toString() : "unsupported";
    }

    private static class FileStatResult {

        public final double sizeInGb;
        public int fileCount, folderCount;

        protected FileStatResult(@NotNull Iterable<String> paths_to_scan) {
            this.fileCount = this.folderCount = 0;
            long byteSize = 0L;
            for (String path : paths_to_scan) {
                byteSize += this.getByteSize(new File(path));
            }
            this.sizeInGb = byteSize / 1048576.0D / 1000.0D;
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