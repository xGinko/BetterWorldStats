package me.xGinko.betterworldstats.commands.betterws;

import me.xGinko.betterworldstats.commands.betterws.subcommands.*;
import me.xGinko.betterworldstats.commands.BetterWorldStatsCommand;
import me.xGinko.betterworldstats.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BetterWSCmd implements BetterWorldStatsCommand, TabCompleter {

    private final List<SubCommand> subCommands = new ArrayList<>();
    private final List<String> tabCompletes = new ArrayList<>();

    public BetterWSCmd() {
        subCommands.add(new ReloadSubCmd());
        subCommands.add(new VersionSubCmd());
        for (SubCommand subCommand : subCommands) {
            tabCompletes.add(subCommand.getName());
        }
    }

    @Override
    public String label() {
        return "betterws";
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            return tabCompletes;
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            boolean cmdExists = false;
            for (SubCommand subCommand : subCommands) {
                if (args[0].equalsIgnoreCase(subCommand.getName())) {
                    subCommand.perform(sender, args);
                }
            }
            if (!cmdExists) showCommandOverview(sender);
        } else {
            showCommandOverview(sender);
        }
        return true;
    }

    private void showCommandOverview(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_AQUA+"----------------------------------------------------");
        sender.sendMessage(ChatColor.AQUA+"BetterWorldStats Commands ");
        sender.sendMessage(ChatColor.DARK_AQUA+"----------------------------------------------------");
        for (SubCommand subCommand : subCommands) {
            sender.sendMessage(ChatColor.AQUA + subCommand.getSyntax() + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + subCommand.getDescription());
        }
        sender.sendMessage(ChatColor.DARK_AQUA+"----------------------------------------------------");
    }
}
