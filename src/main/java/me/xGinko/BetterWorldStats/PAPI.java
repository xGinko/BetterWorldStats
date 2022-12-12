package me.xGinko.BetterWorldStats;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xGinko.BetterWorldStats.config.ConfigCache;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PAPI extends PlaceholderExpansion {
    private final BetterWorldStats plugin;
    private final ConfigCache configCache;

    public PAPI() {
        this.plugin = BetterWorldStats.getInstance();
        this.configCache = BetterWorldStats.getConfiguration();
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
        if (identifier.equals("days")) {
            return String.valueOf(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - configCache.serverBirthTime));
        }
        return null;
    }
}
