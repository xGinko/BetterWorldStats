package me.xGinko.betterworldstats;

import me.xGinko.betterworldstats.commands.BetterWSCmd;
import me.xGinko.betterworldstats.commands.WorldStatsCmd;
import me.xGinko.betterworldstats.config.ConfigCache;
import me.xGinko.betterworldstats.config.LanguageCache;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BetterWorldStats extends JavaPlugin implements Listener {

    private static BetterWorldStats instance;
    private static ConfigCache configCache;
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static Logger logger;
    private static boolean PapiIsEnabled = false;
    private static double fileSize;
    private static int uniquePlayers;

    @Override
    public void onEnable() {
        instance = this;
        uniquePlayers = getServer().getOfflinePlayers().length;
        logger = getLogger();
        logger.info(ChatColor.AQUA + "Reading config");
        reloadBetterWorldStats();
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PapiIsEnabled = true;
            logger.info(ChatColor.AQUA + "Found PlaceholderAPI, registering placeholders...");
            new PAPIExpansion().register();
        }
        logger.info(ChatColor.AQUA + "Registering commands");
        getCommand("betterws").setExecutor(new BetterWSCmd());
        getCommand("worldstats").setExecutor(new WorldStatsCmd());

        // Metrics
        logger.info("Loading Metrics");
        new Metrics(this, 17204);

        logger.info(ChatColor.AQUA + "Done.");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            uniquePlayers = getServer().getOfflinePlayers().length;
        }
    }

    public void reloadBetterWorldStats() {
        reloadLang();
        configCache = new ConfigCache();
        configCache.saveConfig();

        HandlerList.unregisterAll((Plugin) this);
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.cancelTasks(this);

        uniquePlayers = getServer().getOfflinePlayers().length;
        scheduler.runTaskTimerAsynchronously(this, () -> {
            fileSize = count() / 1048576.0D / 1000.0D;
            if (configCache.log_is_enabled) {
                logger.info("Updated filesize (" + configCache.filesize_display_format.format(fileSize) + "GB) asynchronously. Unique player joins: " + uniquePlayers);
            }
        }, 0L, configCache.filesize_update_period);

        getServer().getPluginManager().registerEvents(this, this);
    }

    public void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            File langDirectory = new File(instance.getDataFolder()+ "/lang");
            Files.createDirectories(langDirectory.toPath());
            for (String fileName : getDefaultLanguageFiles()) {
                String localeString = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
                logger.info(String.format("Found language file for %s", localeString));
                LanguageCache langCache = new LanguageCache(localeString);
                languageCacheMap.put(localeString, langCache);
            }
            Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            for (File langFile : langDirectory.listFiles()) {
                Matcher langMatcher = langPattern.matcher(langFile.getName());
                if (langMatcher.find()) {
                    String localeString = langMatcher.group(1).toLowerCase();
                    if(!languageCacheMap.containsKey(localeString)) { // make sure it wasn't a default file that we already loaded
                        logger.info(String.format("Found language file for %s", localeString));
                        LanguageCache langCache = new LanguageCache(localeString);
                        languageCacheMap.put(localeString, langCache);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Error loading language files! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
        }
    }

    private long count() {
        final AtomicLong atomicLong = new AtomicLong(0L);
        for (String path : configCache.directories_to_scan) {
            for (File file : Objects.requireNonNull(new File(path).listFiles())) {
                if (file.isFile()) {
                    atomicLong.addAndGet(file.length());
                }
            }
        }
        return atomicLong.get();
    }

    private Set<String> getDefaultLanguageFiles(){
        Reflections reflections = new Reflections("lang", Scanners.Resources);
        return reflections.getResources(Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)"));
    }

    public static LanguageCache getLang(String lang) {
        lang = lang.replace("-", "_");
        if (configCache.auto_lang) {
            return languageCacheMap.getOrDefault(lang, languageCacheMap.get(configCache.default_lang.toLowerCase()));
        } else {
            return languageCacheMap.get(configCache.default_lang.toLowerCase());
        }
    }

    public static LanguageCache getLang(CommandSender commandSender) {
        if (commandSender instanceof Player ) {
            Player player = (Player) commandSender;
            return getLang(player.getLocale());
        } else {
            return getLang(configCache.default_lang);
        }
    }

    public static BetterWorldStats getInstance()  {
        return instance;
    }

    public static ConfigCache getConfiguration() {
        return configCache;
    }

    public static double getFileSize() {
        return fileSize;
    }

    public static int getUniquePlayers() {
        return uniquePlayers;
    }

    public static boolean isPAPIEnabled() {
        return PapiIsEnabled;
    }

}
