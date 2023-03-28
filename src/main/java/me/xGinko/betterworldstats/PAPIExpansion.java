package me.xGinko.betterworldstats;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xGinko.betterworldstats.config.ConfigCache;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class PAPIExpansion extends PlaceholderExpansion {

    private final ConfigCache configCache;
    private final Calendar calendar;

    public PAPIExpansion() {
        this.configCache = BetterWorldStats.getConfiguration();
        this.calendar = Calendar.getInstance();
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
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        switch (identifier) {
            case "size":
                return String.valueOf(configCache.filesize_display_format.format(BetterWorldStats.getFileSize()));
            case "spoofsize":
                return String.valueOf(configCache.filesize_display_format.format(BetterWorldStats.getFileSize() + configCache.additional_spoofed_filesize));
            case "players":
                return String.valueOf(BetterWorldStats.getUniquePlayers());
            case "ageindays":
                return String.valueOf(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - configCache.server_birth_time));
            case "days":
                calendar.setTimeInMillis(System.currentTimeMillis() - configCache.server_birth_time);
                return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH) - 1);
            case "months":
                calendar.setTimeInMillis(System.currentTimeMillis() - configCache.server_birth_time);
                return String.valueOf(calendar.get(Calendar.MONTH));
            case "years":
                calendar.setTimeInMillis(System.currentTimeMillis() - configCache.server_birth_time);
                return String.valueOf(calendar.get(Calendar.YEAR) - 1970);
            default:
                return null;
        }
    }
}
