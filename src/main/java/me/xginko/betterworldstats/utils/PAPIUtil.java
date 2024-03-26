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
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();
            final String parsedPlaceholder = tryParse(sender, '%' + papiPlaceholder + '%');
            return Tag.selfClosingInserting(LegacyComponentSerializer.legacySection().deserialize(parsedPlaceholder));
        });
    }
}