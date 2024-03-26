package me.xginko.betterworldstats.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PAPIUtil {

    private static @NotNull String tryParse(@Nullable CommandSender sender, @NotNull String input) {
        try {
            if (sender instanceof Player) {
                return PlaceholderAPI.setPlaceholders((Player) sender, input);
            } else {
                return PlaceholderAPI.setPlaceholders(null, input);
            }
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