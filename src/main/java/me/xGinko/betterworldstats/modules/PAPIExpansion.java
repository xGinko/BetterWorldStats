package me.xGinko.betterworldstats.modules;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.config.Config;
import me.xGinko.betterworldstats.modules.BetterWorldStatsModule;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class PAPIExpansion extends PlaceholderExpansion implements BetterWorldStatsModule {

    private final Config config;
    private final Calendar calendar;

    protected PAPIExpansion() {
        this.config = BetterWorldStats.getConfiguration();
        this.calendar = Calendar.getInstance();
    }

    @Override
    public void enable() {
        this.register();
    }

    @Override
    public void disable() {
        this.unregister();
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
                return config.filesize_display_format.format(BetterWorldStats.worldSize().get());
            case "spoofsize":
                return config.filesize_display_format.format(BetterWorldStats.worldSize().get() + config.additional_spoofed_filesize);
            case "players":
                return Integer.toString(BetterWorldStats.uniquePlayerCount().get());
            case "ageindays":
                return Long.toString(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - config.server_birth_time));
            case "days":
                calendar.setTimeInMillis(System.currentTimeMillis() - config.server_birth_time);
                return Integer.toString(calendar.get(Calendar.DAY_OF_MONTH) - 1);
            case "months":
                calendar.setTimeInMillis(System.currentTimeMillis() - config.server_birth_time);
                return Integer.toString(calendar.get(Calendar.MONTH));
            case "years":
                calendar.setTimeInMillis(System.currentTimeMillis() - config.server_birth_time);
                return Integer.toString(calendar.get(Calendar.YEAR) - 1970);
            default:
                return null;
        }
    }
}
