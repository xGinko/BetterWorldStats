package me.xGinko.betterworldstats.commands;

import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.commands.betterws.BetterWSCmd;
import me.xGinko.betterworldstats.commands.worldstats.WorldStatsCmd;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public interface BetterWorldStatsCommand extends CommandExecutor {

    String label();
    @Override
    boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args);

    HashSet<BetterWorldStatsCommand> commands = new HashSet<>();
    static void reloadCommands() {
        commands.clear();

        commands.add(new BetterWSCmd());
        commands.add(new WorldStatsCmd());

        BetterWorldStats plugin = BetterWorldStats.getInstance();
        CommandMap commandMap = plugin.getServer().getCommandMap();
        for (BetterWorldStatsCommand command : commands) {
            plugin.getCommand(command.label()).unregister(commandMap);
            plugin.getCommand(command.label()).setExecutor(command);
        }
    }
}
