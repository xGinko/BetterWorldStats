package me.xginko.betterworldstats.commands.worldstats;

import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.Statistics;
import me.xginko.betterworldstats.commands.BWSCmd;
import me.xginko.betterworldstats.config.Config;
import me.xginko.betterworldstats.utils.KyoriUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class WorldStatsCmd implements BWSCmd {

    private final @NotNull Statistics statistics;
    private final @NotNull Config config;

    public WorldStatsCmd() {
        this.statistics = BetterWorldStats.getStatistics();
        this.config = BetterWorldStats.getConfiguration();
    }

    @Override
    public String label() {
        return "worldstats";
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("betterworldstats.worldstats")) {
            KyoriUtil.sendMessage(sender, BetterWorldStats.getLang(sender).noPermissionMsg(sender));
            return true;
        }

        for (final Component line : BetterWorldStats.getLang(sender).worldStatsMsg(
                sender,
                statistics.mapAge.getYearsPart().toString(),
                statistics.mapAge.getMonthsPart().toString(),
                statistics.mapAge.getDaysPart().toString(),
                statistics.players.getUniqueJoins(),
                statistics.fileStats.getSize(),
                statistics.fileStats.getSpoofedSize(),
                statistics.mapAge.asDays().toString(),
                statistics.mapAge.asMonths().toString(),
                statistics.mapAge.asYears().toString(),
                statistics.fileStats.getFileCount(),
                statistics.fileStats.getFolderCount(),
                statistics.fileStats.getChunkFileCount()
        )) {
            KyoriUtil.sendMessage(sender, line);
        }

        return true;
    }
}