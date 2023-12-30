package me.xGinko.betterworldstats.modules;

import me.xGinko.betterworldstats.BetterWorldStats;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UniquePlayerCounter implements BetterWorldStatsModule, Listener {

    protected UniquePlayerCounter() {}

    @Override
    public void enable() {
        BetterWorldStats plugin = BetterWorldStats.getInstance();
        BetterWorldStats.uniquePlayerCount.set(plugin.getServer().getOfflinePlayers().length);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            BetterWorldStats.uniquePlayerCount.getAndIncrement();
        }
    }
}
