package me.xginko.betterworldstats.utils;

import me.clip.placeholderapi.PlaceholderAPI;

public class PAPIUtil {
    public static String tryPopulate(String input) {
        try {
            return PlaceholderAPI.setPlaceholders(null, input);
        } catch (Throwable t) {
            return input;
        }
    }
}