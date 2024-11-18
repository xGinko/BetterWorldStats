package me.xginko.betterworldstats;

import me.xginko.betterworldstats.commands.betterworldstats.BetterWorldStatsCmd;
import me.xginko.betterworldstats.commands.worldstats.WorldStatsCmd;
import me.xginko.betterworldstats.config.Config;
import me.xginko.betterworldstats.config.LanguageCache;
import me.xginko.betterworldstats.hooks.BWSHook;
import me.xginko.betterworldstats.utils.Disableable;
import me.xginko.betterworldstats.utils.PluginPermission;
import me.xginko.betterworldstats.utils.Util;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.morepaperlib.commands.CommandRegistration;
import space.arim.morepaperlib.scheduling.GracefulScheduling;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

public final class BetterWorldStats extends JavaPlugin {

    private static BetterWorldStats instance;
    private static Statistics statistics;
    private static Config config;
    private static Map<String, LanguageCache> languageCacheMap;

    private static CommandRegistration commandRegistration;
    private static GracefulScheduling scheduling;
    private static BukkitAudiences audiences;
    private static ComponentLogger logger;
    private static Metrics bStats;

    @Override
    public void onEnable() {
        instance = this;
        MorePaperLib morePaperLib = new MorePaperLib(instance);
        commandRegistration = morePaperLib.commandRegistration();
        scheduling = morePaperLib.scheduling();
        audiences = BukkitAudiences.create(instance);
        logger = ComponentLogger.logger(getLogger().getName());
        bStats = new Metrics(instance, 17204);

        logger.info(Component.text("                                              ", Util.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("     ___      _   _                           ", Util.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("    | _ ) ___| |_| |_ ___ _ _                 ", Util.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("    | _ \\/ -_)  _|  _/ -_) '_|                ", Util.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("  __|___/\\___|\\__|\\__\\___|_|_ _        _      ", Util.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("  \\ \\    / /__ _ _| |__| / __| |_ __ _| |_ ___", Util.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("   \\ \\/\\/ / _ \\ '_| / _` \\__ \\  _/ _` |  _(_-<", Util.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("    \\_/\\_/\\___/_| |_\\__,_|___/\\__\\__,_|\\__/__/", Util.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("                                              ", Util.GUPPIE_GREEN_BOLD));

        logger.info("Loading translations");
        reloadLang();

        logger.info("Loading config");
        reloadConfiguration();

        logger.info("Registering permissions");
        PluginPermission.registerAll();

        logger.info("Registering commands");
        new BetterWorldStatsCmd().enable();
        new WorldStatsCmd().enable();

        logger.info("Done.");
    }

    @Override
    public void onDisable() {
        PluginPermission.unregisterAll();
        BWSHook.HOOKS.forEach(Disableable::disable);
        if (statistics != null) {
            statistics.disable();
            statistics = null;
        }
        if (languageCacheMap != null) {
            languageCacheMap.clear();
            languageCacheMap = null;
        }
        if (scheduling != null) {
            scheduling.cancelGlobalTasks();
            scheduling = null;
        }
        if (audiences != null) {
            audiences.close();
            audiences = null;
        }
        if (bStats != null) {
            bStats.shutdown();
            bStats = null;
        }
        commandRegistration = null;
        instance = null;
        config = null;
        logger = null;
    }

    public static @NotNull BetterWorldStats getInstance() {
        return instance;
    }

    public static @NotNull Statistics statistics() {
        return statistics;
    }

    public static @NotNull BukkitAudiences audiences() {
        return audiences;
    }

    public static @NotNull Config config() {
        return config;
    }

    public static @NotNull ComponentLogger logger() {
        return logger;
    }

    public static CommandRegistration commandRegistration() {
        return commandRegistration;
    }

    public static GracefulScheduling scheduling() {
        return scheduling;
    }

    public static @NotNull LanguageCache getLang(Locale locale) {
        return getLang(locale.toString().toLowerCase());
    }

    public static @NotNull LanguageCache getLang(CommandSender sender) {
        return getLang(audiences.sender(sender).pointers().get(Identity.LOCALE).orElse(config.defaultLang));
    }

    public static @NotNull LanguageCache getLang(String lang) {
        if (!config.autoLang) return languageCacheMap.get(config.defaultLang.toString().toLowerCase());
        return languageCacheMap.getOrDefault(lang.replace("-", "_"), languageCacheMap.get(config.defaultLang.toString().toLowerCase()));
    }

    public void reloadPlugin() {
        reloadLang();
        reloadConfiguration();
    }

    private void reloadConfiguration() {
        try {
            if (statistics != null)
                statistics.disable();
            config = new Config();
            statistics = new Statistics();
            BWSHook.reloadHooks();
            config.saveConfig();
        } catch (Exception e) {
            logger.error("Failed loading config!", e);
        }
    }

    public void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            for (String localeString : getAvailableTranslations()) {
                logger.info("Found language file for {}", localeString);
                languageCacheMap.put(localeString, new LanguageCache(localeString));
            }
        } catch (Throwable t) {
            logger.error("Error loading language files!", t);
        } finally {
            if (languageCacheMap.isEmpty()) {
                logger.error("Unable to load translations. Disabling.");
                getServer().getPluginManager().disablePlugin(this);
            } else {
                logger.info("Loaded {} translations", languageCacheMap.size());
            }
        }
    }

    private SortedSet<String> getAvailableTranslations() {
        try (final JarFile pluginJar = new JarFile(getFile())) {
            final File langDirectory = new File(getDataFolder() + "/lang");
            Files.createDirectories(langDirectory.toPath());
            final Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            return Stream.concat(pluginJar.stream().map(ZipEntry::getName), Arrays.stream(langDirectory.listFiles()).map(File::getName))
                    .map(langPattern::matcher)
                    .filter(Matcher::find)
                    .map(matcher -> matcher.group(1))
                    .collect(Collectors.toCollection(TreeSet::new));
        } catch (Throwable t) {
            logger.error("Failed querying for available translations!", t);
            return new TreeSet<>();
        }
    }
}