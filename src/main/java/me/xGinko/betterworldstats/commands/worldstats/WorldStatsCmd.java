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
        final String size = config.filesize_format.format(plugin.statistics.fileStats.getTrueSize());
        final String spoofsize = config.filesize_format.format(plugin.statistics.fileStats.getSpoofedSize());
        final String ageAsDays = plugin.statistics.serverAge.asDays().toString();
        final String ageAsMonths = plugin.statistics.serverAge.asMonths().toString();
        final String ageAsYears = plugin.statistics.serverAge.asYears().toString();
        final String fileCount = Integer.toString(plugin.statistics.fileStats.getFileCount());
        final String folderCount = Integer.toString(plugin.statistics.fileStats.getFolderCount());
        final String chunkFileCount = Integer.toString(plugin.statistics.fileStats.getChunkFileCount());

        for (String line : BetterWorldStats.getLang(sender).world_stats_message) {
            String prePopulated = line
                    .replace("%years%", years)
                    .replace("%months%", months)
                    .replace("%days%", days)
                    .replace("%players%", players)
                    .replace("%size%", size)
                    .replace("%spoofsize%", spoofsize)
                    .replace("%age_in_days%", ageAsDays)
                    .replace("%age_in_months%", ageAsMonths)
                    .replace("%age_in_years%", ageAsYears)
                    .replace("%file_count%", fileCount)
                    .replace("%folder_count%", folderCount)
                    .replace("%chunk_file_count%", chunkFileCount);
            sender.sendMessage(BetterWorldStats.foundPlaceholderAPI ? StringUtil.tryPopulateWithPAPI(prePopulated) : prePopulated);
        }

        return true;
    }
}