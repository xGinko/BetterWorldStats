package me.xGinko.BetterWorldStats.commands.subcommands;

import me.xGinko.BetterWorldStats.BetterWorldStats;
import me.xGinko.BetterWorldStats.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCmd extends SubCommand {
    @Override
    public String getName() {return "reload";}
    @Override
    public String getDescription() {return "Reload the plugin configuration.";}
    @Override
    public String getSyntax() {return "/betterws reload";}
    @Override
    public void perform(@NotNull CommandSender sender, String[] args) {
        if (sender.hasPermission("betterws.reload")) {
            sender.sendMessage(ChatColor.RED + "Reloading BetterWorldStats...");
            BetterWorldStats.getInstance().reloadBetterWorldStats();
            sender.sendMessage(ChatColor.GREEN + "Reload complete.");
        } else {
            sender.sendMessage(ChatColor.RED + BetterWorldStats.getLang(sender).noPermissions);
        }
    }
}
