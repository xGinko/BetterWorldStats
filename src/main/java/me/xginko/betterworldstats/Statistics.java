package me.xginko.betterworldstats;

import me.xginko.betterworldstats.stats.FileStats;
import me.xginko.betterworldstats.stats.MapAge;
import me.xginko.betterworldstats.stats.Players;

public class Statistics {

    public final MapAge mapAge;
    public final FileStats fileStats;
    public final Players players;

    public Statistics() {
        this.fileStats = new FileStats();
        this.mapAge = new MapAge();
        this.players = new Players();
    }
}
