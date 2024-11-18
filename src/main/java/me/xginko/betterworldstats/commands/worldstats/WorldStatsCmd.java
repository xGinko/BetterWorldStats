package me.xginko.betterworldstats.commands.worldstats;

import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.utils.Enableable;
import me.xginko.betterworldstats.utils.PluginPermission;
import me.xginko.betterworldstats.utils.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class WorldStatsCmd extends Command implements Enableable {

    public WorldStatsCmd() {
        super("worldstats", "Shows the statistics message", "/worldstats", Collections.emptyList());
    }

    @Override
    public void enable() {
        BetterWorldStats.commandRegistration().getServerCommandMap()
                .register(BetterWorldStats.getInstance().getDescription().getName().toLowerCase(), this);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws CommandException, IllegalArgumentException {
        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission(PluginPermission.WORLDSTATS_CMD.get())) {
            Util.sendMessage(sender, BetterWorldStats.getLang(sender).noPermissionMsg(sender));
            return true;
        }

        for (Component line : BetterWorldStats.getLang(sender).worldStatsMsg(
                sender,
                BetterWorldStats.statistics().birthCalendar.getYearsPart().toString(),
                BetterWorldStats.statistics().birthCalendar.getMonthsPart().toString(),
                BetterWorldStats.statistics().birthCalendar.getDaysPart().toString(),
                BetterWorldStats.statistics().playerStats.getUniqueJoins(),
                BetterWorldStats.statistics().worldStats.getSize(),
                BetterWorldStats.statistics().worldStats.getSpoofedSize(),
                BetterWorldStats.statistics().birthCalendar.asDays().toString(),
                BetterWorldStats.statistics().birthCalendar.asMonths().toString(),
                BetterWorldStats.statistics().birthCalendar.asYears().toString(),
                BetterWorldStats.statistics().worldStats.getFileCount(),
                BetterWorldStats.statistics().worldStats.getFolderCount(),
                BetterWorldStats.statistics().worldStats.getChunkCount(),
                BetterWorldStats.statistics().worldStats.getEntityCount()
        )) {
            Util.sendMessage(sender, line);
        }

        return true;
    }
}