package me.xginko.betterworldstats.commands.worldstats;

import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.Statistics;
import me.xginko.betterworldstats.commands.BetterWorldStatsCommand;
import me.xginko.betterworldstats.config.Config;
import me.xginko.betterworldstats.utils.KyoriUtil;
import me.xginko.betterworldstats.utils.PAPIUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class WorldStatsCmd implements BetterWorldStatsCommand {

    private final Statistics statistics;
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
    public @Nullable List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("betterworldstats.worldstats")) {
            KyoriUtil.sendMessage(sender, BetterWorldStats.getLang(sender).no_permission);
            return true;
        }

        final TextReplacementConfig years = TextReplacementConfig.builder()
                .matchLiteral("%years%").replacement(statistics.mapAge.getYearsPart().toString())
                .build();
        final TextReplacementConfig months = TextReplacementConfig.builder()
                .matchLiteral("%months%").replacement(statistics.mapAge.getMonthsPart().toString())
                .build();
        final TextReplacementConfig days = TextReplacementConfig.builder()
                .matchLiteral("%days%").replacement(statistics.mapAge.getDaysPart().toString())
                .build();
        final TextReplacementConfig players = TextReplacementConfig.builder()
                .matchLiteral("%players%").replacement(Integer.toString(statistics.players.getUniqueJoins()))
                .build();
        final TextReplacementConfig fileSize = TextReplacementConfig.builder()
                .matchLiteral("%size%").replacement(config.filesize_format.format(statistics.fileStats.getTrueSize()))
                .build();
        final TextReplacementConfig spoofSize = TextReplacementConfig.builder()
                .matchLiteral("%spoofsize%").replacement(config.filesize_format.format(statistics.fileStats.getSpoofedSize()))
                .build();
        final TextReplacementConfig ageAsDays = TextReplacementConfig.builder()
                .matchLiteral("%age_in_days%").replacement(statistics.mapAge.asDays().toString())
                .build();
        final TextReplacementConfig ageAsMonths = TextReplacementConfig.builder()
                .matchLiteral("%age_in_months%").replacement(statistics.mapAge.asMonths().toString())
                .build();
        final TextReplacementConfig ageAsYears = TextReplacementConfig.builder()
                .matchLiteral("%age_in_years%").replacement(statistics.mapAge.asYears().toString())
                .build();
        final TextReplacementConfig fileCount = TextReplacementConfig.builder()
                .matchLiteral("%file_count%").replacement(Integer.toString(statistics.fileStats.getFileCount()))
                .build();
        final TextReplacementConfig folderCount = TextReplacementConfig.builder()
                .matchLiteral("%folder_count%").replacement(Integer.toString(statistics.fileStats.getFolderCount()))
                .build();
        final TextReplacementConfig chunkFileCount = TextReplacementConfig.builder()
                .matchLiteral("%chunk_file_count%").replacement(Integer.toString(statistics.fileStats.getChunkFileCount()))
                .build();

        for (Component line : BetterWorldStats.getLang(sender).world_stats_message) {
            KyoriUtil.sendMessage(sender, PAPIUtil.tryPopulate(line
                    .replaceText(years).replaceText(months).replaceText(days)
                    .replaceText(players).replaceText(fileSize).replaceText(spoofSize)
                    .replaceText(ageAsDays).replaceText(ageAsMonths).replaceText(ageAsYears)
                    .replaceText(fileCount).replaceText(folderCount).replaceText(chunkFileCount)
            ));
        }

        return true;
    }
}