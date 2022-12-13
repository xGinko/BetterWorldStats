package me.xGinko.BetterWorldStats.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.xGinko.BetterWorldStats.BetterWorldStats;
import me.xGinko.BetterWorldStats.config.ConfigCache;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import java.util.Calendar;

public class WorldStatsCmd implements CommandExecutor, Listener {
    private final BetterWorldStats plugin;
    private final ConfigCache configCache;
    private final Calendar calendar;

    public WorldStatsCmd() {
        this.plugin = BetterWorldStats.getInstance();
        this.configCache = BetterWorldStats.getConfiguration();
        this.calendar = Calendar.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("betterws.worldstats")) return true;

        calendar.setTimeInMillis(System.currentTimeMillis() - configCache.serverBirthTime);
        int year = calendar.get(Calendar.YEAR) - 1970;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH) - 1;

        if (year < 0) {
            year = 0;
            month = 0;
            day = 0;
        }

        String yearAsString = String.valueOf(year);
        String monthAsString = String.valueOf(month);
        String dayAsString = String.valueOf(day);

        for (String line : BetterWorldStats.getLang(sender).worldStatsMessage) {
            sender.sendMessage(format(line, yearAsString, monthAsString, dayAsString));
        }

        return true;
    }

    private String format(String line, String year, String month, String day) {
        final String parsedLine = ChatColor.translateAlternateColorCodes('&', line)
                .replace("%years%", year)
                .replace("%months%", month)
                .replace("%days%", day)
                .replace("%size%", configCache.fileSizeFormat.format(plugin.fileSize))
                .replace("%spoof%", configCache.fileSizeFormat.format(plugin.fileSize + configCache.spoofSize))
                .replace("%players%", String.valueOf(plugin.offlinePlayers));
        return plugin.papiIsEnabled ? PlaceholderAPI.setPlaceholders(null, parsedLine) : parsedLine;
    }
}
