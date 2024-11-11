package me.xginko.betterworldstats.commands.betterworldstats;

import me.xginko.betterworldstats.commands.BWSCmd;
import me.xginko.betterworldstats.commands.SubCmd;
import me.xginko.betterworldstats.commands.betterworldstats.subcommands.ReloadSubCmd;
import me.xginko.betterworldstats.commands.betterworldstats.subcommands.VersionSubCmd;
import me.xginko.betterworldstats.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BetterWorldStatsCmd implements BWSCmd, TabCompleter {

    private final @NotNull List<SubCmd> subCmds;
    private final @NotNull List<String> tabCompletes;

    public BetterWorldStatsCmd() {
        subCmds = Arrays.asList(new ReloadSubCmd(), new VersionSubCmd());
        tabCompletes = subCmds.stream().map(SubCmd::getName).collect(Collectors.toList());
    }

    @Override
    public String label() {
        return "betterws";
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return args.length == 1 ? tabCompletes : Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sendCommandOverview(sender);
            return true;
        }

        for (final SubCmd subCmd : subCmds) {
            if (args[0].equalsIgnoreCase(subCmd.getName())) {
                subCmd.perform(sender, args);
                return true;
            }
        }

        sendCommandOverview(sender);
        return true;
    }

    private void sendCommandOverview(CommandSender sender) {
        Util.sendMessage(sender, Component.text("-----------------------------------------------------").color(Util.GUPPIE_GREEN));
        Util.sendMessage(sender, Component.text("BetterWorldStats Commands").color(Util.GUPPIE_GREEN));
        Util.sendMessage(sender, Component.text("-----------------------------------------------------").color(Util.GUPPIE_GREEN));
        for (SubCmd subCmd : subCmds) Util.sendMessage(sender,
                subCmd.getSyntax().append(Component.text(" - ").color(NamedTextColor.DARK_GRAY)).append(subCmd.getDescription()));
        Util.sendMessage(sender, Component.text("-----------------------------------------------------").color(Util.GUPPIE_GREEN));
    }
}
