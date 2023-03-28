package me.xGinko.betterworldstats.commands;

import me.xGinko.betterworldstats.commands.subcommands.ReloadCmd;
import me.xGinko.betterworldstats.commands.subcommands.VersionCmd;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BetterWSCmd implements CommandExecutor, TabCompleter {

    private final List<SubCommand> subCommands = new ArrayList<>();
    private final List<String> tabCompletes = new ArrayList<>();

    public BetterWSCmd() {
        subCommands.add(new ReloadCmd());
        subCommands.add(new VersionCmd());
        for (SubCommand subCommand : subCommands) {
            tabCompletes.add(subCommand.getName());
        }
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            return tabCompletes;
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            for (SubCommand subCommand : subCommands) {
                if (args[0].equalsIgnoreCase(subCommand.getName())) {
                    subCommand.perform(sender, args);
                }
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA+"----------------------------------------------------");
            sender.sendMessage(ChatColor.AQUA+"BetterWorldStats Commands ");
            sender.sendMessage(ChatColor.DARK_AQUA+"----------------------------------------------------");
            for (SubCommand subCommand : subCommands) {
                sender.sendMessage(ChatColor.AQUA + subCommand.getSyntax() + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + subCommand.getDescription());
            }
            sender.sendMessage(ChatColor.DARK_AQUA+"----------------------------------------------------");
        }
        return true;
    }
}
