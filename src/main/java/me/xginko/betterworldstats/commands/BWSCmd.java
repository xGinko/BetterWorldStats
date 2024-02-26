package me.xginko.betterworldstats.commands;

import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.commands.betterworldstats.BetterWorldStatsCmd;
import me.xginko.betterworldstats.commands.worldstats.WorldStatsCmd;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.TabCompleter;

import java.util.HashSet;
import java.util.Set;

public interface BWSCmd extends CommandExecutor, TabCompleter {

    String label();

    Set<BWSCmd> commands = new HashSet<>();
    static void reloadCommands() {
        commands.clear();

        commands.add(new BetterWorldStatsCmd());
        commands.add(new WorldStatsCmd());

        BetterWorldStats plugin = BetterWorldStats.getInstance();
        CommandMap commandMap = plugin.getServer().getCommandMap();
        for (BWSCmd command : commands) {
            plugin.getCommand(command.label()).unregister(commandMap);
            plugin.getCommand(command.label()).setExecutor(command);
        }
    }
}
