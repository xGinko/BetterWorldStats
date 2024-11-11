package me.xginko.betterworldstats.stats;

import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.utils.Disableable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerStats implements Listener, Disableable {

    private final @NotNull AtomicInteger uniquePlayers;

    public PlayerStats() {
        BetterWorldStats plugin = BetterWorldStats.getInstance();
        this.uniquePlayers = new AtomicInteger(plugin.getServer().getOfflinePlayers().length);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            uniquePlayers.getAndIncrement();
        }
    }

    public String getUniqueJoins() {
        return uniquePlayers.toString();
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }
}
