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
        if (!sender.hasPermission("betterworldstats.version")) {
            sender.sendMessage(BetterWorldStats.getLang(sender).no_permission);
            return;
        }

        final PluginDescriptionFile pluginYML = BetterWorldStats.getInstance().getDescription();
        sender.sendMessage("\n" +
                ChatColor.GOLD+pluginYML.getName()+" "+pluginYML.getVersion()+
                ChatColor.GRAY+" by "+ChatColor.DARK_AQUA+pluginYML.getAuthors().get(0)
                + "\n");
    }
}