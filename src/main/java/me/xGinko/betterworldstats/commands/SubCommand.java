package me.xGinko.betterworldstats.commands;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    public abstract String getName();
    public abstract TextComponent getDescription();
    public abstract TextComponent getSyntax();
    public abstract void perform(CommandSender sender, String[] args);
}
