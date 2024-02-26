package me.xginko.betterworldstats.commands.worldstats;

import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.Statistics;
import me.xginko.betterworldstats.commands.BWSCmd;
import me.xginko.betterworldstats.config.Config;
import me.xginko.betterworldstats.utils.KyoriUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class WorldStatsCmd implements BWSCmd {

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
            KyoriUtil.sendMessage(sender, BetterWorldStats.getLang(sender).noPermissionMsg());
            return true;
        }

        for (Component line : BetterWorldStats.getLang(sender).worldStatsMsg(
                statistics.mapAge.getYearsPart().toString(),
                statistics.mapAge.getMonthsPart().toString(),
                statistics.mapAge.getDaysPart().toString(),
                Integer.toString(statistics.players.getUniqueJoins()),
                config.filesize_format.format(statistics.fileStats.getTrueSize()),
                config.filesize_format.format(statistics.fileStats.getSpoofedSize()),
                statistics.mapAge.asDays().toString(),
                statistics.mapAge.asMonths().toString(),
                statistics.mapAge.asYears().toString(),
                Integer.toString(statistics.fileStats.getFileCount()),
                Integer.toString(statistics.fileStats.getFolderCount()),
                Integer.toString(statistics.fileStats.getChunkFileCount())
        )) {
            KyoriUtil.sendMessage(sender, line);
        }

        return true;
    }
}