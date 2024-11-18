package me.xginko.betterworldstats.commands.betterworldstats.subcommands;

import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.commands.SubCommand;
import me.xginko.betterworldstats.utils.PluginPermission;
import me.xginko.betterworldstats.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class VersionSubCmd implements SubCommand {

    @Override
    public @NotNull String label() {
        return "version";
    }

    @Override
    public @NotNull TextComponent syntax() {
        return Component.text("/bws version").color(Util.GUPPIE_GREEN);
    }

    @Override
    public @NotNull TextComponent description() {
        return Component.text("Show the plugin version.").color(NamedTextColor.GRAY);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws CommandException, IllegalArgumentException {
        return Collections.emptyList();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission(PluginPermission.VERSION_CMD.get())) {
            Util.sendMessage(sender, BetterWorldStats.getLang(sender).noPermissionMsg(sender));
            return true;
        }

        final PluginDescriptionFile pluginYML = BetterWorldStats.getInstance().getDescription();

        Util.sendMessage(sender,
                Component.newline()
                .append(Component.text(pluginYML.getName() + " " + pluginYML.getVersion(), Util.GUPPIE_GREEN)
                                .clickEvent(ClickEvent.openUrl(pluginYML.getWebsite())))
                .append(Component.text(" by ", NamedTextColor.GRAY))
                .append(Component.text(String.join(", ", pluginYML.getAuthors()), TextColor.fromHexString("#00EDFF"))
                                .clickEvent(ClickEvent.openUrl("https://github.com/xGinko")))
                .append(Component.newline())
        );

        return true;
    }
}