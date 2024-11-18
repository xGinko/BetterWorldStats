package me.xginko.betterworldstats.commands.betterworldstats.subcommands;

import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.commands.SubCommand;
import me.xginko.betterworldstats.utils.PluginPermission;
import me.xginko.betterworldstats.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ReloadSubCmd implements SubCommand {

    @Override
    public @NotNull String label() {
        return "reload";
    }

    @Override
    public @NotNull TextComponent syntax() {
        return Component.text("/bws reload").color(Util.GUPPIE_GREEN);
    }

    @Override
    public @NotNull TextComponent description() {
        return Component.text("Reload the plugin configuration.").color(NamedTextColor.GRAY);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws CommandException, IllegalArgumentException {
        return Collections.emptyList();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission(PluginPermission.RELOAD_CMD.get())) {
            Util.sendMessage(sender, BetterWorldStats.getLang(sender).noPermissionMsg(sender));
            return true;
        }

        Util.sendMessage(sender, Component.text("Reloading BetterWorldStats...").color(NamedTextColor.WHITE));
        BetterWorldStats.scheduling().asyncScheduler().run(() -> {
            BetterWorldStats.getInstance().reloadPlugin();
            Util.sendMessage(sender, Component.text("Reload complete.").color(Util.GUPPIE_GREEN));
        });

        return true;
    }
}