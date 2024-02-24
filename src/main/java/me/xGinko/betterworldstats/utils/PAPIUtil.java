package me.xGinko.betterworldstats.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class PAPIUtil {
    private static final PlainTextComponentSerializer SERIALIZER = PlainTextComponentSerializer.plainText();

    public static Component tryPopulate(Component input) {
        try {
            return Component.text(PlaceholderAPI.setPlaceholders(null, SERIALIZER.serialize(input)))
                    .style(input.style());
        } catch (Throwable t) {
            return input;
        }
    }
}