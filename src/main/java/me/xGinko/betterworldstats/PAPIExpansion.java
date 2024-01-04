package me.xGinko.betterworldstats;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xGinko.betterworldstats.config.Config;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIExpansion extends PlaceholderExpansion {

    private final WorldStats statistics;
    private final Config config;

    PAPIExpansion() {
        this.statistics = BetterWorldStats.getStatistics();
        this.config = BetterWorldStats.getConfiguration();
        this.register();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return "xGinko";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "worldstats";
    }

    @Override
    public @NotNull String getVersion() {
        return BetterWorldStats.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        switch (identifier) {
            case "size":
                return config.filesize_format.format(statistics.fileStats.getTrueSize());
            case "spoofsize":
                return config.filesize_format.format(statistics.fileStats.getSpoofedSize());
            case "file_count":
                return Integer.toString(statistics.fileStats.getFileCount());
            case "folder_count":
                return Integer.toString(statistics.fileStats.getFolderCount());
            case "chunk_file_count":
                return Integer.toString(statistics.fileStats.getChunkFileCount());
            case "players":
                return statistics.uniquePlayerCount.toString();
            case "days":
                return statistics.mapAge.getDaysPart().toString();
            case "months":
                return statistics.mapAge.getMonthsPart().toString();
            case "years":
                return statistics.mapAge.getYearsPart().toString();
            case "age_in_days":
                return statistics.mapAge.asDays().toString();
            case "age_in_months":
                return statistics.mapAge.asMonths().toString();
            case "age_in_years":
                return statistics.mapAge.asYears().toString();
            default:
                return null;
        }
    }
}