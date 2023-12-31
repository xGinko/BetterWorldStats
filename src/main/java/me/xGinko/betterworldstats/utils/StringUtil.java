package me.xGinko.betterworldstats.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.xGinko.betterworldstats.BetterWorldStats;

public class StringUtil {
    public static String tryPopulateWithPAPI(String input) {
        return BetterWorldStats.foundPlaceholderAPI ? PlaceholderAPI.setPlaceholders(null, input) : input;
    }
}