package me.xGinko.BetterWorldStats.commands;

import me.xGinko.BetterWorldStats.commands.subcommands.ReloadCmd;
import me.xGinko.BetterWorldStats.commands.subcommands.VersionCmd;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BetterWSCmd implements CommandExecutor, TabCompleter {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();
    private final List<String> tabCompletes = new ArrayList<>();
    public BetterWSCmd() {
        subcommands.add(new ReloadCmd());
        subcommands.add(new VersionCmd());
        for (int i=0; i<getSubcommands().size(); i++) {
            tabCompletes.add(getSubcommands().get(i).getName());
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
            for (int i = 0; i < getSubcommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {
                    getSubcommands().get(i).perform(sender, args);
                }
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA+"----------------------------------------------------");
            sender.sendMessage(ChatColor.AQUA+"BetterWorldStats Commands ");
            sender.sendMessage(ChatColor.DARK_AQUA+"----------------------------------------------------");
            for (int i = 0; i < getSubcommands().size(); i++) {
                sender.sendMessage(ChatColor.AQUA+getSubcommands().get(i).getSyntax() + ChatColor.DARK_GRAY+" - " + ChatColor.GRAY+getSubcommands().get(i).getDescription());
            }
            sender.sendMessage(ChatColor.DARK_AQUA+"----------------------------------------------------");
        }
        return true;
    }
    public ArrayList<SubCommand> getSubcommands() {return subcommands;}
}
