package me.xGinko.BetterWorldStats;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xGinko.BetterWorldStats.config.ConfigCache;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class PAPI extends PlaceholderExpansion {
    private final BetterWorldStats plugin;
    private final ConfigCache configCache;
    private final Calendar calendar;

    public PAPI() {
        this.plugin = BetterWorldStats.getInstance();
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
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (identifier.equals("size")) {
            return String.valueOf(configCache.fileSizeFormat.format(plugin.fileSize));
        }
        if (identifier.equals("spoof")) {
            return String.valueOf(configCache.fileSizeFormat.format(plugin.fileSize + configCache.spoofSize));
        }
        if (identifier.equals("players")) {
            return String.valueOf(plugin.offlinePlayers);
        }
        if (identifier.equals("ageindays")) {
            return String.valueOf(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - configCache.serverBirthTime));
        }
        if (identifier.equals("days")) {
            calendar.setTimeInMillis(System.currentTimeMillis() - configCache.serverBirthTime);
            return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        }
        if (identifier.equals("months")) {
            calendar.setTimeInMillis(System.currentTimeMillis() - configCache.serverBirthTime);
            return String.valueOf(calendar.get(Calendar.MONTH));
        }
        if (identifier.equals("years")) {
            calendar.setTimeInMillis(System.currentTimeMillis() - configCache.serverBirthTime);
            return String.valueOf(calendar.get(Calendar.YEAR) - 1970);
        }
        return null;
    }
}
