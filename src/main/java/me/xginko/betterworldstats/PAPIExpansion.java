package me.xginko.betterworldstats;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xginko.betterworldstats.config.Config;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PAPIExpansion extends PlaceholderExpansion {

    private final @NotNull Statistics statistics;

    PAPIExpansion() {
        this.statistics = BetterWorldStats.getStatistics();
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
    @SuppressWarnings({"deprecation"})
    public @NotNull String getVersion() {
        return BetterWorldStats.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        switch (identifier) {
            case "size":
                return statistics.fileStats.getSize();
            case "spoofsize":
                return statistics.fileStats.getSpoofedSize();
            case "file_count":
                return statistics.fileStats.getFileCount();
            case "folder_count":
                return statistics.fileStats.getFolderCount();
            case "chunk_count":
                return statistics.fileStats.getChunkCount();
            case "entity_count":
                return statistics.fileStats.getEntityCount();
            case "players":
                return statistics.players.getUniqueJoins();
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