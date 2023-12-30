package me.xGinko.betterworldstats.commands.worldstats;

import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.commands.BetterWorldStatsCommand;
import me.xGinko.betterworldstats.config.Config;
import me.xGinko.betterworldstats.modules.BetterWorldStatsModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class WorldStatsCmd implements BetterWorldStatsCommand {

    private final Config config;
    private final Calendar calendar;

    public WorldStatsCmd() {
        this.config = BetterWorldStats.getConfiguration();
        this.calendar = Calendar.getInstance();
    }

    @Override
    public String label() {
        return "worldstats";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("betterworldstats.worldstats")) {
            sender.sendMessage(BetterWorldStats.getLang(sender).no_permission);
            return true;
        }

        this.calendar.setTimeInMillis(System.currentTimeMillis() - config.server_birth_time);

        final String years = Integer.toString(Math.max(this.calendar.get(Calendar.YEAR) - 1970, 0));
        final String months = Integer.toString(Math.max(this.calendar.get(Calendar.MONTH), 0));
        final String days = Integer.toString(Math.max(this.calendar.get(Calendar.DAY_OF_MONTH) - 1, 0));
        final String size = config.filesize_display_format.format(BetterWorldStats.worldSize.get() + config.additional_spoofed_filesize);
        final String players = BetterWorldStats.uniquePlayerCount.toString();

        for (String line : BetterWorldStats.getLang(sender).world_stats_message) {
            sender.sendMessage(BetterWorldStatsModule.tryPopulateWithPAPI(line
                    .replaceAll("%years%", years)
                    .replaceAll("%months%", months)
                    .replaceAll("%days%", days)
                    .replaceAll("%size%", size)
                    .replaceAll("%players%", players)
            ));
        }

        return true;
    }
}