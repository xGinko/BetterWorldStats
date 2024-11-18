package me.xginko.betterworldstats.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xginko.betterworldstats.BetterWorldStats;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PAPIExpansion extends PlaceholderExpansion implements BWSHook {

    @Override
    public String pluginName() {
        return "PlaceholderAPI";
    }

    @Override
    public boolean shouldEnable() {
        return BetterWorldStats.getInstance().getServer().getPluginManager().isPluginEnabled(pluginName());
    }

    @Override
    public void enable() {
        register();
    }

    @Override
    public void disable() {
        unregister();
    }

    @Override
    public @NotNull String getAuthor() {
        return BetterWorldStats.getInstance().getDescription().getAuthors().get(0);
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
                return BetterWorldStats.statistics().worldStats.getSize();
            case "spoofsize":
                return BetterWorldStats.statistics().worldStats.getSpoofedSize();
            case "file_count":
                return BetterWorldStats.statistics().worldStats.getFileCount();
            case "folder_count":
                return BetterWorldStats.statistics().worldStats.getFolderCount();
            case "chunk_count":
                return BetterWorldStats.statistics().worldStats.getChunkCount();
            case "entity_count":
                return BetterWorldStats.statistics().worldStats.getEntityCount();
            case "playerStats":
                return BetterWorldStats.statistics().playerStats.getUniqueJoins();
            case "days":
                return BetterWorldStats.statistics().birthCalendar.getDaysPart().toString();
            case "months":
                return BetterWorldStats.statistics().birthCalendar.getMonthsPart().toString();
            case "years":
                return BetterWorldStats.statistics().birthCalendar.getYearsPart().toString();
            case "age_in_days":
                return BetterWorldStats.statistics().birthCalendar.asDays().toString();
            case "age_in_months":
                return BetterWorldStats.statistics().birthCalendar.asMonths().toString();
            case "age_in_years":
                return BetterWorldStats.statistics().birthCalendar.asYears().toString();
            default:
                return null;
        }
    }
}