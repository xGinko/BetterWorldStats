package me.xGinko.betterworldstats;

import me.xGinko.betterworldstats.stats.FileStats;
import me.xGinko.betterworldstats.stats.MapAge;
import me.xGinko.betterworldstats.stats.Players;

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
