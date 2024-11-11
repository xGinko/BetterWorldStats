package me.xginko.betterworldstats;

import me.xginko.betterworldstats.stats.WorldStats;
import me.xginko.betterworldstats.stats.BirthCalendar;
import me.xginko.betterworldstats.stats.PlayerStats;
import me.xginko.betterworldstats.utils.Disableable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class Statistics implements Disableable {

    public final @NotNull BirthCalendar birthCalendar;
    public final @NotNull WorldStats worldStats;
    public final @NotNull PlayerStats playerStats;

    public Statistics() {
        this.worldStats = new WorldStats();
        this.birthCalendar = new BirthCalendar();
        this.playerStats = new PlayerStats();
    }

    @Override
    public void disable() {
        playerStats.disable();
    }

    public CompletableFuture<Statistics> get() {
        return worldStats.get().thenApply(stats -> this);
    }
}
