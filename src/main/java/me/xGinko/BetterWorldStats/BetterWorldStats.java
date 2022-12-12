package me.xGinko.BetterWorldStats;

import me.xGinko.BetterWorldStats.commands.BetterWSCmd;
import me.xGinko.BetterWorldStats.commands.WorldStatsCmd;
import me.xGinko.BetterWorldStats.config.ConfigCache;
import me.xGinko.BetterWorldStats.config.LanguageCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BetterWorldStats extends JavaPlugin {
    private static BetterWorldStats instance;
    private static ConfigCache configCache;
    private static HashMap<String, LanguageCache> languageCacheMap;
    public double fileSize;
    public int offlinePlayers;

    @Override
    public void onEnable() {
        instance = this;

        reloadBetterWorldStats();

        Logger logger = getLogger();

        offlinePlayers = Bukkit.getOfflinePlayers().length;

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) new PAPI().register();

        getCommand("betterws").setExecutor(new BetterWSCmd());
        getCommand("worldstats").setExecutor(new WorldStatsCmd());

        logger.info(ChatColor.DARK_GREEN + "Initialized.");
    }

    private long count() {
        final AtomicLong atomicLong = new AtomicLong(0L);
        for (String path : configCache.directoriesToScan) {
            for (File file : Objects.requireNonNull(new File(path).listFiles())) {
                if (file.isFile()) {
                    atomicLong.addAndGet(file.length());
                }
            }
        }
        return atomicLong.get();
    }

    public static BetterWorldStats getInstance()  { return instance; }

    public static ConfigCache getConfiguration() { return configCache; }

    public void reloadBetterWorldStats() {
        reloadLang();
        saveDefaultConfig();
        reloadConfig();
        configCache = new ConfigCache();

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.cancelTasks(this);
        scheduler.runTaskTimerAsynchronously(this, () -> fileSize = count() / 1048576.0D / 1000.0D, 0L, configCache.fileSizeUpdateDelay);
        scheduler.runTaskTimer(this, () -> {
            offlinePlayers = Bukkit.getOfflinePlayers().length;
            if (configCache.logIsEnabled) getLogger().info("Updated filesize asynchronously: "+fileSize);
        }, 1L, configCache.fileSizeUpdateDelay);

        configCache.saveConfig();
    }

    public void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            File langDirectory = new File(instance.getDataFolder()+ "/lang");
            Files.createDirectories(langDirectory.toPath());
            for (String fileName : getDefaultLanguageFiles()) {
                String localeString = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
                getLogger().info(String.format("Found language file for %s", localeString));
                LanguageCache langCache = new LanguageCache(localeString);
                languageCacheMap.put(localeString, langCache);
            }
            Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            for (File langFile : langDirectory.listFiles()) {
                Matcher langMatcher = langPattern.matcher(langFile.getName());
                if (langMatcher.find()) {
                    String localeString = langMatcher.group(1).toLowerCase();
                    if(!languageCacheMap.containsKey(localeString)) { // make sure it wasn't a default file that we already loaded
                        getLogger().info(String.format("Found language file for %s", localeString));
                        LanguageCache langCache = new LanguageCache(localeString);
                        languageCacheMap.put(localeString, langCache);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().severe("Error loading language files! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
        }
    }

    private Set<String> getDefaultLanguageFiles(){
        Reflections reflections = new Reflections("lang", Scanners.Resources);
        return reflections.getResources(Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)"));
    }

    public static LanguageCache getLang(String lang) {
        lang = lang.replace("-", "_");
        if (configCache.auto_lang) {
            return languageCacheMap.getOrDefault(lang, languageCacheMap.get(configCache.default_lang.toString().toLowerCase()));
        } else {
            return languageCacheMap.get(configCache.default_lang.toString().toLowerCase());
        }
    }

    public static LanguageCache getLang(Locale locale) {
        return getLang(locale.toString().toLowerCase());
    }

    public static LanguageCache getLang(CommandSender commandSender) {
        if (commandSender instanceof Player ) {
            Player player = (Player) commandSender;
            return getLang(player.getLocale());
        } else {
            return getLang(configCache.default_lang);
        }
    }
}
