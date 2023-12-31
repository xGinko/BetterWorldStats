package me.xGinko.betterworldstats.utils;

import me.clip.placeholderapi.PlaceholderAPI;

public class StringUtil {

    public static String tryPopulateWithPAPI(String input) {
        return PlaceholderAPI.setPlaceholders(null, input);
    }
}