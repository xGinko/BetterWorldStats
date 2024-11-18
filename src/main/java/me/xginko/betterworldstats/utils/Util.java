package me.xginko.betterworldstats.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.xginko.betterworldstats.BetterWorldStats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Util {

    public static final TextColor GUPPIE_GREEN = TextColor.color(0,255,128);
    public static final Style GUPPIE_GREEN_BOLD = Style.style(GUPPIE_GREEN, TextDecoration.BOLD);

    public static void sendMessage(@NotNull CommandSender sender, @NotNull Component message) {
        BetterWorldStats.audiences().sender(sender).sendMessage(message);
    }

    public static @NotNull String replaceAmpersand(@NotNull String string) {
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

    public static boolean hasMethod(Class<?> holderClass, String methodName, Class<?>... parameterClasses) {
        try {
            holderClass.getMethod(methodName, parameterClasses);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static @NotNull String tryParse(@Nullable CommandSender sender, @NotNull String input) {
        try {
            return sender instanceof Player ? PlaceholderAPI.setPlaceholders((Player)sender, input) : PlaceholderAPI.setPlaceholders(null, input);
        } catch (Throwable t) {
            return input;
        }
    }

    public static @NotNull TagResolver papiTagResolver(@Nullable CommandSender sender) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> Tag.selfClosingInserting(
                LegacyComponentSerializer.legacySection().deserialize(
                        tryParse(sender, '%' + argumentQueue.popOr("papi tag requires an argument").value() + '%')
                )
        ));
    }
}
