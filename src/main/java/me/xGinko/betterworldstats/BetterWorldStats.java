package me.xGinko.betterworldstats;

import com.google.common.util.concurrent.AtomicDouble;
import me.xGinko.betterworldstats.commands.BetterWorldStatsCommand;
import me.xGinko.betterworldstats.config.Config;
import me.xGinko.betterworldstats.config.LanguageCache;
import me.xGinko.betterworldstats.modules.BetterWorldStatsModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BetterWorldStats extends JavaPlugin {

    private static BetterWorldStats instance;
    private static Config config;
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static Logger logger;
    private final static AtomicDouble worldSize = new AtomicDouble();
    private final static AtomicInteger uniquePlayerCount = new AtomicInteger();

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
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
        logger.info("Registering commands");
        BetterWorldStatsCommand.reloadCommands();
        logger.info("Loading Metrics");
        new Metrics(this, 17204);
        logger.info("Done.");
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
    public static boolean foundPAPI() {
        return instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }
    public static LanguageCache getLang(CommandSender commandSender) {
        return commandSender instanceof Player ? getLang(((Player) commandSender).getLocale()) : getLang(config.default_lang);
    }
    public static LanguageCache getLang(String lang) {
        return config.auto_lang ? languageCacheMap.getOrDefault(lang.replace("-", "_"), languageCacheMap.get(config.default_lang)) : languageCacheMap.get(config.default_lang);
    }

    public void reloadPlugin() {
        reloadLang();
        reloadConfiguration();
        BetterWorldStatsCommand.reloadCommands();
    }

    private void reloadConfiguration() {
        try {
            config = new Config();
            BetterWorldStatsModule.reloadModules();
            config.saveConfig();
        } catch (Exception e) {
            logger.severe("Failed while loading config! - " + e.getLocalizedMessage());
        }
    }

    public void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            File langDirectory = new File(getDataFolder() + "/lang");
            Files.createDirectories(langDirectory.toPath());
            for (String fileName : getDefaultLanguageFiles()) {
                String localeString = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
                logger.info("Found language file for " + localeString);
                LanguageCache langCache = new LanguageCache(localeString);
                languageCacheMap.put(localeString, langCache);
            }
            Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            for (File langFile : langDirectory.listFiles()) {
                Matcher langMatcher = langPattern.matcher(langFile.getName());
                if (langMatcher.find()) {
                    String localeString = langMatcher.group(1).toLowerCase();
                    if (!languageCacheMap.containsKey(localeString)) { // make sure it wasn't a default file that we already loaded
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
        try (JarFile pluginJar = new JarFile(this.getFile())) {
            Enumeration<JarEntry> entries = pluginJar.entries();
            while (entries.hasMoreElements()) {
                String path = entries.nextElement().getName();
                if (path.startsWith("lang/") && path.endsWith(".yml"))
                    languageFiles.add(path);
            }
        } catch (IOException e) {
            logger.severe("Failed getting default lang files! - "+e.getLocalizedMessage());
        }
        return languageFiles;
    }
}