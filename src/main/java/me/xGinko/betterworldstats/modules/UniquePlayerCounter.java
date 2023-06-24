package me.xGinko.betterworldstats.modules;

import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.commands.BetterWorldStatsCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UniquePlayerCounter implements BetterWorldStatsModule, Listener {

    private final BetterWorldStats plugin;

    protected UniquePlayerCounter() {
        this.plugin = BetterWorldStats.getInstance();
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            BetterWorldStats.setUniquePlayers(plugin.getServer().getOfflinePlayers().length);
        }
    }
}
