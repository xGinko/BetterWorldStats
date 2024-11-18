package me.xginko.betterworldstats.commands;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SubCommand {

    @NotNull String label();
    @NotNull TextComponent syntax();
    @NotNull TextComponent description();

    @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws CommandException, IllegalArgumentException;
    boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args);

}
