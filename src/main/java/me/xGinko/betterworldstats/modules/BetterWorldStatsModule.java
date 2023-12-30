package me.xGinko.betterworldstats.modules;

import me.clip.placeholderapi.PlaceholderAPI;
import me.xGinko.betterworldstats.BetterWorldStats;

import java.util.HashSet;

public interface BetterWorldStatsModule {

    void enable();
    void disable();

    HashSet<BetterWorldStatsModule> modules = new HashSet<>(3);

    static void reloadModules() {
        modules.forEach(BetterWorldStatsModule::disable);
        modules.clear();

        modules.add(new UniquePlayerCounter());
        modules.add(new WorldSizeCheck());
        if (BetterWorldStats.foundPlaceholderAPI)
            modules.add(new PAPIExpansion());

        modules.forEach(BetterWorldStatsModule::enable);
    }

    static String tryPopulateWithPAPI(String input) {
        return !BetterWorldStats.foundPlaceholderAPI ? input : PlaceholderAPI.setPlaceholders(null, input);
    }
}