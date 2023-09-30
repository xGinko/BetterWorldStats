package me.xGinko.betterworldstats.commands.betterworldstats.subcommands;

import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class VersionSubCmd extends SubCommand {

    public VersionSubCmd() {}

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "Get the plugin version.";
    }

    @Override
    public String getSyntax() {
        return "/bws version";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission("betterws.version")) {
            PluginDescriptionFile pluginyml = BetterWorldStats.getInstance().getDescription();
            sender.sendMessage("\n");
            sender.sendMessage(
                    ChatColor.GOLD+pluginyml.getName()+" "+pluginyml.getVersion()+
                            ChatColor.GRAY+" by "+ChatColor.DARK_AQUA+pluginyml.getAuthors().get(0)
            );
            sender.sendMessage("\n");
        } else {
            sender.sendMessage(BetterWorldStats.getLang(sender).no_permission);
        }
    }
}
