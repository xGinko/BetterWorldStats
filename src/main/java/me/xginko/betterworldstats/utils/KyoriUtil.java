package me.xginko.betterworldstats.utils;

import me.xginko.betterworldstats.BetterWorldStats;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class KyoriUtil {

    public static void sendMessage(CommandSender sender, Component message) {
        BetterWorldStats.getAudiences().sender(sender).sendMessage(message);
    }

    public static Locale getLocale(Player player) {
        return BetterWorldStats.getAudiences().player(player).pointers().getOrDefault(Identity.LOCALE, getFallbackLocale(player));
    }

    @SuppressWarnings("deprecation")
    private static Locale getFallbackLocale(Player player) {
        try {
            return Locale.forLanguageTag(player.getLocale().replace("_", "-"));
        } catch (Throwable t) {
            return Locale.US;
        }
    }

    public static String altColorCodesToMiniMessageTags(Character symbol, String string) {
        string = string.replace(symbol + "0", "<black>");
        string = string.replace(symbol + "1", "<dark_blue>");
        string = string.replace(symbol + "2", "<dark_green>");
        string = string.replace(symbol + "3", "<dark_aqua>");
        string = string.replace(symbol + "4", "<dark_red>");
        string = string.replace(symbol + "5", "<dark_purple>");
        string = string.replace(symbol + "6", "<gold>");
        string = string.replace(symbol + "7", "<gray>");
        string = string.replace(symbol + "8", "<dark_gray>");
        string = string.replace(symbol + "9", "<blue>");
        string = string.replace(symbol + "a", "<green>");
        string = string.replace(symbol + "b", "<aqua>");
        string = string.replace(symbol + "c", "<red>");
        string = string.replace(symbol + "d", "<light_purple>");
        string = string.replace(symbol + "e", "<yellow>");
        string = string.replace(symbol + "f", "<white>");
        string = string.replace(symbol + "k", "<obfuscated>");
        string = string.replace(symbol + "l", "<bold>");
        string = string.replace(symbol + "m", "<strikethrough>");
        string = string.replace(symbol + "n", "<underlined>");
        string = string.replace(symbol + "o", "<italic>");
        string = string.replace(symbol + "r", "<reset>");
        return string;
    }
}
