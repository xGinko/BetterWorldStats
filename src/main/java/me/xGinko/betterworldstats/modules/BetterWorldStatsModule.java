package me.xGinko.betterworldstats.modules;

import me.xGinko.betterworldstats.BetterWorldStats;

import java.util.HashSet;

public interface BetterWorldStatsModule {

    void enable();
    void disable();

    HashSet<BetterWorldStatsModule> modules = new HashSet<>();

    static void reloadModules() {
        modules.forEach(BetterWorldStatsModule::disable);
        modules.clear();

        modules.add(new UniquePlayerCounter());
        modules.add(new WorldSizeCheck());
        if (BetterWorldStats.foundPAPI())
            modules.add(new PAPIExpansion());

        modules.forEach(BetterWorldStatsModule::enable);
    }
}
