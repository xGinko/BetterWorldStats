package me.xGinko.betterworldstats.commands.worldstats;

import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.WorldStats;
import me.xGinko.betterworldstats.commands.BetterWorldStatsCommand;
import me.xGinko.betterworldstats.config.Config;
import me.xGinko.betterworldstats.utils.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WorldStatsCmd implements BetterWorldStatsCommand {

    private final WorldStats statistics;
    private final Config config;

    public WorldStatsCmd() {
        this.statistics = BetterWorldStats.getStatistics();
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

        final String years = statistics.mapAge.getYearsPart().toString();
        final String months = statistics.mapAge.getMonthsPart().toString();
        final String days = statistics.mapAge.getDaysPart().toString();
        final String players = Integer.toString(statistics.players.getUniqueJoins());
        final String size = config.filesize_format.format(statistics.fileStats.getTrueSize());
        final String spoofedSize = config.filesize_format.format(statistics.fileStats.getSpoofedSize());
        final String ageAsDays = statistics.mapAge.asDays().toString();
        final String ageAsMonths = statistics.mapAge.asMonths().toString();
        final String ageAsYears = statistics.mapAge.asYears().toString();
        final String fileCount = Integer.toString(statistics.fileStats.getFileCount());
        final String folderCount = Integer.toString(statistics.fileStats.getFolderCount());
        final String chunkFileCount = Integer.toString(statistics.fileStats.getChunkFileCount());

        for (String line : BetterWorldStats.getLang(sender).world_stats_message) {
            String prePopulated = line
                    .replace("%years%", years)
                    .replace("%months%", months)
                    .replace("%days%", days)
                    .replace("%players%", players)
                    .replace("%size%", size)
                    .replace("%spoofsize%", spoofedSize)
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