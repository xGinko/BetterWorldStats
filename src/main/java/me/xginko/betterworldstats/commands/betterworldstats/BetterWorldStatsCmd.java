package me.xginko.betterworldstats.commands.betterworldstats;

import com.google.common.collect.ImmutableList;
import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.commands.SubCommand;
import me.xginko.betterworldstats.commands.betterworldstats.subcommands.ReloadSubCmd;
import me.xginko.betterworldstats.commands.betterworldstats.subcommands.VersionSubCmd;
import me.xginko.betterworldstats.utils.Enableable;
import me.xginko.betterworldstats.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BetterWorldStatsCmd extends Command implements Enableable {

    private final @NotNull List<SubCommand> subCommands;
    private final @NotNull List<String> tabCompletes;

    public BetterWorldStatsCmd() {
        super(
                "betterworldstats",
                "Reload the plugin or get its version",
                "/betterworldstats [ reload, version ]",
                ImmutableList.of("bws", "betterws")
        );
        subCommands = ImmutableList.of(new ReloadSubCmd(), new VersionSubCmd());
        tabCompletes = subCommands.stream().map(SubCommand::label).collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    @Override
    public void enable() {
        BetterWorldStats.commandRegistration().getServerCommandMap()
                .register(BetterWorldStats.getInstance().getDescription().getName().toLowerCase(), this);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws CommandException, IllegalArgumentException {
        if (args.length == 1) {
            return tabCompletes;
        }

        if (args.length >= 2) {
            for (final SubCommand subCommand : subCommands) {
                if (args[0].equalsIgnoreCase(subCommand.label())) {
                    return subCommand.tabComplete(sender, alias, args);
                }
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length >= 1) {
            for (final SubCommand subCommand : subCommands) {
                if (args[0].equalsIgnoreCase(subCommand.label())) {
                    return subCommand.execute(sender, commandLabel, args);
                }
            }
        }

        return commandOverview(sender);
    }

    private boolean commandOverview(CommandSender sender) {
        Util.sendMessage(sender, Component.text("-----------------------------------------------------", Util.GUPPIE_GREEN));
        Util.sendMessage(sender, Component.text("BetterWorldStats Commands", Util.GUPPIE_GREEN));
        Util.sendMessage(sender, Component.text("-----------------------------------------------------", Util.GUPPIE_GREEN));
        for (final SubCommand subCmd : subCommands) {
            Util.sendMessage(sender, subCmd.syntax().append(Component.text(" - ", NamedTextColor.GRAY)).append(subCmd.description()));
        }
        Util.sendMessage(sender, Component.text("-----------------------------------------------------", Util.GUPPIE_GREEN));
        return true;
    }
}
