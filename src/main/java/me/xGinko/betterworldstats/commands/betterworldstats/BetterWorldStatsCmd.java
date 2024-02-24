package me.xGinko.betterworldstats.commands.betterworldstats;

import me.xGinko.betterworldstats.commands.BetterWorldStatsCommand;
import me.xGinko.betterworldstats.commands.SubCommand;
import me.xGinko.betterworldstats.commands.betterworldstats.subcommands.ReloadSubCmd;
import me.xGinko.betterworldstats.commands.betterworldstats.subcommands.VersionSubCmd;
import me.xGinko.betterworldstats.utils.KyoriUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BetterWorldStatsCmd implements BetterWorldStatsCommand, TabCompleter {

    private final List<SubCommand> subCommands;
    private final List<String> tabCompletes;

    public BetterWorldStatsCmd() {
        subCommands = Arrays.asList(new ReloadSubCmd(), new VersionSubCmd());
        tabCompletes = subCommands.stream().map(SubCommand::getName).collect(Collectors.toList());
    }

    @Override
    public String label() {
        return "betterws";
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return args.length == 1 ? tabCompletes : Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sendCommandOverview(sender);
            return true;
        }

        for (final SubCommand subCommand : subCommands) {
            if (args[0].equalsIgnoreCase(subCommand.getName())) {
                subCommand.perform(sender, args);
                return true;
            }
        }

        sendCommandOverview(sender);
        return true;
    }

    private void sendCommandOverview(CommandSender sender) {
        KyoriUtil.sendMessage(sender, Component.text("-----------------------------------------------------").color(NamedTextColor.DARK_AQUA));
        KyoriUtil.sendMessage(sender, Component.text("BetterWorldStats Commands").color(NamedTextColor.AQUA));
        KyoriUtil.sendMessage(sender, Component.text("-----------------------------------------------------").color(NamedTextColor.DARK_AQUA));
        for (SubCommand subCommand : subCommands) {
            KyoriUtil.sendMessage(sender,
                    subCommand.getSyntax().append(Component.text(" - ").color(NamedTextColor.DARK_GRAY)).append(subCommand.getDescription()));
        }
    }
}
