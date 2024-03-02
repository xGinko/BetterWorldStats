package me.xginko.betterworldstats.utils;

import me.xginko.betterworldstats.BetterWorldStats;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

public class KyoriUtil {

    public static void sendMessage(@NotNull CommandSender sender, @NotNull Component message) {
        BetterWorldStats.getAudiences().sender(sender).sendMessage(message);
    }

    @SuppressWarnings("deprecation")
    public static Locale getLocale(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final Optional<Locale> locale = BetterWorldStats.getAudiences().player(player).pointers().get(Identity.LOCALE);
            if (locale.isPresent())
                return locale.get();
            try {
                return Locale.forLanguageTag(player.getLocale().replace("_", "-"));
            } catch (Throwable ignored) {}
        }
        return BetterWorldStats.getConfiguration().default_lang;
    }

    public static @NotNull String altColorCodesToMiniMessageTags(@NotNull Character symbol, @NotNull String string) {
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
