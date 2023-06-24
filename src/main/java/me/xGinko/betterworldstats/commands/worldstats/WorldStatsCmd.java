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
        this.isPAPIpresent = BetterWorldStats.isPlaceholderAPIInstalled();
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

            for (String line : BetterWorldStats.getLang(sender).world_stats_message) {
                sender.sendMessage(formattedMessageLine(line, year, month, day));
            }
        } else {
            sender.sendMessage(BetterWorldStats.getLang(sender).no_permission);
        }
        return true;
    }

    private String formattedMessageLine(String line, int year, int month, int day) {
        final String parsedLine = ChatColor.translateAlternateColorCodes('&', line)
                .replace("%years%", String.valueOf(year))
                .replace("%months%", String.valueOf(month))
                .replace("%days%", String.valueOf(day))
                .replace("%size%", config.filesize_display_format.format(BetterWorldStats.getWorldFileSize() + config.additional_spoofed_filesize))
                .replace("%players%", String.valueOf(BetterWorldStats.getUniquePlayers()));
        return isPAPIpresent ? PlaceholderAPI.setPlaceholders(null, parsedLine) : parsedLine;
    }
}
