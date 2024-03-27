package me.xginko.betterworldstats;

import me.xginko.betterworldstats.stats.WorldStats;
import me.xginko.betterworldstats.stats.BirthCalendar;
import me.xginko.betterworldstats.stats.PlayerStats;
import org.jetbrains.annotations.NotNull;

public final class Statistics {

    public final @NotNull BirthCalendar birthCalendar;
    public final @NotNull WorldStats worldStats;
    public final @NotNull PlayerStats playerStats;

    public Statistics() {
        this.worldStats = new WorldStats();
        this.birthCalendar = new BirthCalendar();
        this.playerStats = new PlayerStats();
    }

    public void shutdown() {
        playerStats.disable();
    }
}
