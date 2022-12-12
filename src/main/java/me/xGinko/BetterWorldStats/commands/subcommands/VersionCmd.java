package me.xGinko.BetterWorldStats.commands.subcommands;

import me.xGinko.BetterWorldStats.BetterWorldStats;
import me.xGinko.BetterWorldStats.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class VersionCmd extends SubCommand {
    @Override
    public String getName() {return "version";}
    @Override
    public String getDescription() {return "Get the plugin version.";}
    @Override
    public String getSyntax() {return "/betterws version";}
    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission("betterws.version")) {
            sender.sendMessage(ChatColor.RED + "BetterWorldStats v" + BetterWorldStats.getInstance().getDescription().getVersion() + ChatColor.BLUE + " by xGinko");
        } else {
            sender.sendMessage(ChatColor.RED + BetterWorldStats.getLang(sender).noPermissions);
        }
    }
}
