package me.xGinko.betterworldstats.commands.worldstats;

import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.commands.BetterWorldStatsCommand;
import me.xGinko.betterworldstats.config.Config;
import me.xGinko.betterworldstats.utils.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WorldStatsCmd implements BetterWorldStatsCommand {

    private final BetterWorldStats plugin;
    private final Config config;

    public WorldStatsCmd() {
        this.plugin = BetterWorldStats.getInstance();
        this.config = BetterWorldStats.getConfiguration();
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

        final String years = plugin.statistics.serverAge.getYearsPart().toString();
        final String months = plugin.statistics.serverAge.getMonthsPart().toString();
        final String days = plugin.statistics.serverAge.getDaysPart().toString();
        final String players = plugin.statistics.uniquePlayerCount.toString();
        final String size = config.filesize_format.format(plugin.statistics.fileSize.getTrueSize());
        final String spoofsize = config.filesize_format.format(plugin.statistics.fileSize.getSpoofedSize());

        for (String line : BetterWorldStats.getLang(sender).world_stats_message) {
            String prePopulated = line
                    .replace("%years%", years)
                    .replace("%months%", months)
                    .replace("%days%", days)
                    .replace("%players%", players)
                    .replace("%size%", size)
                    .replace("%spoofsize%", spoofsize);
            sender.sendMessage(BetterWorldStats.foundPlaceholderAPI ? StringUtil.tryPopulateWithPAPI(prePopulated) : prePopulated);
        }

        return true;
    }
}