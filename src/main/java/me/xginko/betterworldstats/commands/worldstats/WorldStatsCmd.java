package me.xginko.betterworldstats.commands.worldstats;

import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.Statistics;
import me.xginko.betterworldstats.commands.BWSCmd;
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

    public WorldStatsCmd() {
        this.statistics = BetterWorldStats.getStatistics();
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
                statistics.birthCalendar.getYearsPart().toString(),
                statistics.birthCalendar.getMonthsPart().toString(),
                statistics.birthCalendar.getDaysPart().toString(),
                statistics.playerStats.getUniqueJoins(),
                statistics.worldStats.getSize(),
                statistics.worldStats.getSpoofedSize(),
                statistics.birthCalendar.asDays().toString(),
                statistics.birthCalendar.asMonths().toString(),
                statistics.birthCalendar.asYears().toString(),
                statistics.worldStats.getFileCount(),
                statistics.worldStats.getFolderCount(),
                statistics.worldStats.getChunkCount(),
                statistics.worldStats.getEntityCount()
        )) {
            KyoriUtil.sendMessage(sender, line);
        }

        return true;
    }
}