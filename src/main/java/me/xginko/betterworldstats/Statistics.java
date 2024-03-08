package me.xginko.betterworldstats;

import me.xginko.betterworldstats.stats.FileStats;
import me.xginko.betterworldstats.stats.MapAge;
import me.xginko.betterworldstats.stats.Players;
import org.jetbrains.annotations.NotNull;

public class Statistics {

    public final @NotNull MapAge mapAge;
    public final @NotNull FileStats fileStats;
    public final @NotNull Players players;

    public Statistics() {
        this.fileStats = new FileStats();
        this.mapAge = new MapAge();
        this.players = new Players();
    }
}
