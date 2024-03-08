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
    public static @NotNull Locale getLocale(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            try {
                final Player player = (Player) sender;
                final Optional<Locale> locale = BetterWorldStats.getAudiences().player(player).pointers().get(Identity.LOCALE);
                return locale.orElseGet(() -> Locale.forLanguageTag(player.getLocale().replace("_", "-")));
            } catch (Throwable ignored) {}
        }
        return BetterWorldStats.getConfiguration().default_lang;
    }

    public static @NotNull String translateChatColor(@NotNull String string) {
        string = string.replace("&0", "<black>");
        string = string.replace("&1", "<dark_blue>");
        string = string.replace("&2", "<dark_green>");
        string = string.replace("&3", "<dark_aqua>");
        string = string.replace("&4", "<dark_red>");
        string = string.replace("&5", "<dark_purple>");
        string = string.replace("&6", "<gold>");
        string = string.replace("&7", "<gray>");
        string = string.replace("&8", "<dark_gray>");
        string = string.replace("&9", "<blue>");
        string = string.replace("&a", "<green>");
        string = string.replace("&b", "<aqua>");
        string = string.replace("&c", "<red>");
        string = string.replace("&d", "<light_purple>");
        string = string.replace("&e", "<yellow>");
        string = string.replace("&f", "<white>");
        string = string.replace("&k", "<obfuscated>");
        string = string.replace("&l", "<bold>");
        string = string.replace("&m", "<strikethrough>");
        string = string.replace("&n", "<underlined>");
        string = string.replace("&o", "<italic>");
        string = string.replace("&r", "<reset>");
        return string;
    }
}
