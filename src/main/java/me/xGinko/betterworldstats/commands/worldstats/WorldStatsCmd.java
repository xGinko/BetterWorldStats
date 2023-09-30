package me.xGinko.betterworldstats.commands.worldstats;

import me.clip.placeholderapi.PlaceholderAPI;
import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.commands.BetterWorldStatsCommand;
import me.xGinko.betterworldstats.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class WorldStatsCmd implements BetterWorldStatsCommand {

    private final Config config;
    private final Calendar calendar;
    private final boolean isPAPIpresent;

    public WorldStatsCmd() {
        this.config = BetterWorldStats.getConfiguration();
        this.calendar = Calendar.getInstance();
        this.isPAPIpresent = BetterWorldStats.foundPAPI();
    }

    @Override
    public String label() {
        return "worldstats";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender.hasPermission("betterws.worldstats")) {
            calendar.setTimeInMillis(System.currentTimeMillis() - config.server_birth_time);
            int year = calendar.get(Calendar.YEAR) - 1970;
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH) - 1;

            if (year < 0) {
                year = 0;
                month = 0;
                day = 0;
            }

            final String years = Integer.toString(year);
            final String months = Integer.toString(month);
            final String days = Integer.toString(day);

            BetterWorldStats.getLang(sender).world_stats_message.forEach(line -> sender.sendMessage(formattedMessageLine(line, years, months, days)));
        } else {
            sender.sendMessage(BetterWorldStats.getLang(sender).no_permission);
        }
        return true;
    }

    private String formattedMessageLine(String line, String year, String month, String day) {
        final String placeholdersFilled = line
                .replace("%years%", year)
                .replace("%months%", month)
                .replace("%days%", day)
                .replace("%size%", config.filesize_display_format.format(BetterWorldStats.worldSize().get() + config.additional_spoofed_filesize))
                .replace("%players%", Integer.toString(BetterWorldStats.uniquePlayerCount().get()));
        return isPAPIpresent ? PlaceholderAPI.setPlaceholders(null, placeholdersFilled) : placeholdersFilled;
    }
}