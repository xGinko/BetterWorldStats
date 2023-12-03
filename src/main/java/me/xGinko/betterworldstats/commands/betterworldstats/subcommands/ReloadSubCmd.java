package me.xGinko.betterworldstats.commands.betterworldstats.subcommands;

import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCmd extends SubCommand {

    public ReloadSubCmd() {}

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload the plugin configuration.";
    }

    @Override
    public String getSyntax() {
        return "/bws reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission("betterworldstats.reload")) {
            sender.sendMessage(ChatColor.WHITE + "Reloading BetterWorldStats...");
            BetterWorldStats.getInstance().reloadPlugin();
            sender.sendMessage(ChatColor.GREEN + "Reload complete.");
        } else {
            sender.sendMessage(BetterWorldStats.getLang(sender).no_permission);
        }
    }
}