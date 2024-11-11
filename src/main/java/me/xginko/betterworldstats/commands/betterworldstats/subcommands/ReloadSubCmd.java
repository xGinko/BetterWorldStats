package me.xginko.betterworldstats.commands.betterworldstats.subcommands;

import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.commands.SubCmd;
import me.xginko.betterworldstats.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

public class ReloadSubCmd extends SubCmd {

    public ReloadSubCmd() {}

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public TextComponent getDescription() {
        return Component.text("Reload the plugin configuration.").color(NamedTextColor.GRAY);
    }

    @Override
    public TextComponent getSyntax() {
        return Component.text("/bws reload").color(Util.GUPPIE_GREEN);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("betterworldstats.reload")) {
            Util.sendMessage(sender, BetterWorldStats.getLang(sender).noPermissionMsg(sender));
            return;
        }

        Util.sendMessage(sender, Component.text("Reloading BetterWorldStats...").color(NamedTextColor.WHITE));
        CompletableFuture.runAsync(() -> {
            BetterWorldStats.getInstance().reloadPlugin();
            Util.sendMessage(sender, Component.text("Reload complete.").color(NamedTextColor.GREEN));
        });
    }
}