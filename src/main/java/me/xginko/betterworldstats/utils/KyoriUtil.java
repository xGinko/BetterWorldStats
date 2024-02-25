package me.xginko.betterworldstats.utils;

import me.xginko.betterworldstats.BetterWorldStats;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class KyoriUtil {

    public static void sendMessage(CommandSender sender, Component message) {
        BetterWorldStats.getAudiences().sender(sender).sendMessage(message);
    }

    public static String translateChatColor(Character symbol, String string) {
        string = string.replaceAll(symbol + "0", "<black>");
        string = string.replaceAll(symbol + "1", "<dark_blue>");
        string = string.replaceAll(symbol + "2", "<dark_green>");
        string = string.replaceAll(symbol + "3", "<dark_aqua>");
        string = string.replaceAll(symbol + "4", "<dark_red>");
        string = string.replaceAll(symbol + "5", "<dark_purple>");
        string = string.replaceAll(symbol + "6", "<gold>");
        string = string.replaceAll(symbol + "7", "<gray>");
        string = string.replaceAll(symbol + "8", "<dark_gray>");
        string = string.replaceAll(symbol + "9", "<blue>");
        string = string.replaceAll(symbol + "a", "<green>");
        string = string.replaceAll(symbol + "b", "<aqua>");
        string = string.replaceAll(symbol + "c", "<red>");
        string = string.replaceAll(symbol + "d", "<light_purple>");
        string = string.replaceAll(symbol + "e", "<yellow>");
        string = string.replaceAll(symbol + "f", "<white>");
        string = string.replaceAll(symbol + "k", "<obfuscated>");
        string = string.replaceAll(symbol + "l", "<bold>");
        string = string.replaceAll(symbol + "m", "<strikethrough>");
        string = string.replaceAll(symbol + "n", "<underlined>");
        string = string.replaceAll(symbol + "o", "<italic>");
        string = string.replaceAll(symbol + "r", "<reset>");
        return string;
    }
}
