package me.xGinko.betterworldstats.commands.subcommands;

import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.commands.SubCommand;
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
            sender.sendMessage(ChatColor.AQUA + "BetterWorldStats v" + BetterWorldStats.getInstance().getDescription().getVersion() + ChatColor.DARK_AQUA + " by xGinko");
        } else {
            sender.sendMessage(ChatColor.RED + BetterWorldStats.getLang(sender).no_permissions);
        }
    }
}
