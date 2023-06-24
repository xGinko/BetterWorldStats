package me.xGinko.betterworldstats.modules;

import java.util.HashSet;

public interface BetterWorldStatsModule {

    void enable();
    void disable();

    HashSet<BetterWorldStatsModule> modules = new HashSet<>();

    static void reloadModules() {
        modules.clear();
        modules.forEach(BetterWorldStatsModule::disable);

        modules.add(new UniquePlayerCounter());
        modules.add(new WorldSizeCheck());

        modules.forEach(BetterWorldStatsModule::enable);
    }
}
