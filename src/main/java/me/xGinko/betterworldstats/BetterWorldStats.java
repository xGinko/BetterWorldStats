package me.xGinko.betterworldstats;

import com.google.common.util.concurrent.AtomicDouble;
import me.xGinko.betterworldstats.commands.BetterWorldStatsCommand;
import me.xGinko.betterworldstats.config.Config;
import me.xGinko.betterworldstats.config.LanguageCache;
import me.xGinko.betterworldstats.modules.BetterWorldStatsModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BetterWorldStats extends JavaPlugin implements Listener {

    private static BetterWorldStats instance;
    private static Config config;
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static PAPIExpansion papiExpansion;
    private static Logger logger;
    private static final AtomicDouble worldSize = new AtomicDouble();
    private static final AtomicInteger uniquePlayerCount = new AtomicInteger();

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        // Fancy enable
        logger.info("                                                                                ");
        logger.info("  ___      _   _         __      __       _    _ ___ _        _                 ");
        logger.info(" | _ ) ___| |_| |_ ___ _ \\ \\    / /__ _ _| |__| / __| |_ __ _| |_ ___         ");
        logger.info(" | _ \\/ -_)  _|  _/ -_) '_\\ \\/\\/ / _ \\ '_| / _` \\__ \\  _/ _` |  _(_-<    ");
        logger.info(" |___/\\___|\\__|\\__\\___|_|  \\_/\\_/\\___/_| |_\\__,_|___/\\__\\__,_|\\__/__/");
        logger.info("                                                                                ");

        logger.info("Loading languages");
        reloadLang();

        logger.info("Loading config");
        reloadConfiguration();

        if (isPlaceholderAPIInstalled()) {
            logger.info("Found PlaceholderAPI, registering placeholders...");
            reloadPAPIExpansion();
        }

        logger.info("Registering commands");
        BetterWorldStatsCommand.reloadCommands();

        // Metrics
        logger.info("Loading Metrics");
        new Metrics(this, 17204);

        logger.info("Done.");
    }

    public void reloadPlugin() {
        reloadLang();
        reloadConfiguration();
        if (isPlaceholderAPIInstalled()) reloadPAPIExpansion();
        BetterWorldStatsCommand.reloadCommands();
    }

    private void reloadConfiguration() {
        try {
            config = new Config();
            BetterWorldStatsModule.reloadModules();
            config.saveConfig();
        } catch (Exception e) {
            logger.severe("Reload failed! - "+e.getLocalizedMessage());
        }
    }

    private void reloadPAPIExpansion() {
        if (papiExpansion != null) papiExpansion.unregister();
        papiExpansion = new PAPIExpansion();
        papiExpansion.register();
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
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error loading language files! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
        }
    }

    private Set<String> getDefaultLanguageFiles() {
        Set<String> languageFiles = new HashSet<>();
        try (JarFile jar = new JarFile(this.getFile())) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String path = entry.getName();
                if (path.startsWith("lang/") && path.endsWith(".yml")) {
                    languageFiles.add(path);
                }
            }

        } catch (IOException e) {
            logger.severe("Failed getting default lang files! - "+e.getLocalizedMessage());
        }
        return languageFiles;
    }

    public static LanguageCache getLang(String lang) {
        lang = lang.replace("-", "_");
        if (config.auto_lang) {
            return languageCacheMap.getOrDefault(lang, languageCacheMap.get(config.default_lang));
        } else {
            return languageCacheMap.get(config.default_lang);
        }
    }

    public static LanguageCache getLang(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            return getLang(((Player) commandSender).getLocale());
        } else {
            return getLang(config.default_lang);
        }
    }

    public static BetterWorldStats getInstance()  {
        return instance;
    }
    public static Config getConfiguration() {
        return config;
    }
    public static Logger getLog() {
        return logger;
    }
    public static AtomicDouble worldSize() {
        return worldSize;
    }
    public static AtomicInteger uniquePlayerCount() {
        return uniquePlayerCount;
    }
    public static boolean isPlaceholderAPIInstalled() {
        return instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }
}
