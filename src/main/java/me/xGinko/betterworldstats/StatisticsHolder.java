package me.xGinko.betterworldstats;

import me.xGinko.betterworldstats.config.Config;
import me.xGinko.betterworldstats.stats.FileStats;
import me.xGinko.betterworldstats.stats.ServerAge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsHolder implements Listener {

    public final ServerAge serverAge;
    public final FileStats fileStats;
    public final AtomicInteger uniquePlayerCount;

    public StatisticsHolder() {
        this.fileStats = new FileStats();
        Config config = BetterWorldStats.getConfiguration();
        this.serverAge = new ServerAge(config.server_birth_time_millis);
        BetterWorldStats plugin = BetterWorldStats.getInstance();
        this.uniquePlayerCount = new AtomicInteger(plugin.getServer().getOfflinePlayers().length);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            this.uniquePlayerCount.getAndIncrement();
        }
    }
}
