package me.xGinko.betterworldstats;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xGinko.betterworldstats.config.Config;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIExpansion extends PlaceholderExpansion {

    private final BetterWorldStats plugin;
    private final Config config;

    PAPIExpansion() {
        this.plugin = BetterWorldStats.getInstance();
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
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        switch (identifier) {
            case "size":
                return config.filesize_format.format(plugin.statistics.fileSize.getTrueSize());
            case "spoofsize":
                return config.filesize_format.format(plugin.statistics.fileSize.getSpoofedSize());
            case "players":
                return plugin.statistics.uniquePlayerCount.toString();
            case "days":
                return plugin.statistics.serverAge.getDaysPart().toString();
            case "months":
                return plugin.statistics.serverAge.getMonthsPart().toString();
            case "years":
                return plugin.statistics.serverAge.getYearsPart().toString();
            case "age_in_days":
                return plugin.statistics.serverAge.asDays().toString();
            case "age_in_months":
                return plugin.statistics.serverAge.asMonths().toString();
            case "age_in_years":
                return plugin.statistics.serverAge.asYears().toString();
            default:
                return null;
        }
    }
}