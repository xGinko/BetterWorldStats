package me.xginko.betterworldstats.stats;

import me.xginko.betterworldstats.BetterWorldStats;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class Players implements Listener {

    private final @NotNull AtomicInteger uniquePlayers;

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

    public String getUniqueJoins() {
        return uniquePlayers.toString();
    }
}
