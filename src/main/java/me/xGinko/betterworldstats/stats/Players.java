package me.xGinko.betterworldstats.stats;

import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.Statistics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class Players extends Statistics implements Listener {

    private final AtomicInteger uniquePlayers;

    public Players() {
        BetterWorldStats plugin = BetterWorldStats.getInstance();
        this.uniquePlayers = new AtomicInteger(plugin.getServer().getOfflinePlayers().length);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            this.uniquePlayers.getAndIncrement();
        }
    }

    public int getUniqueJoins() {
        return this.uniquePlayers.get();
    }
}
