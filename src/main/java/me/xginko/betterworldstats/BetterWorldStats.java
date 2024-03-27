package me.xginko.betterworldstats;

import me.xginko.betterworldstats.commands.BWSCmd;
import me.xginko.betterworldstats.config.Config;
import me.xginko.betterworldstats.config.LanguageCache;
import me.xginko.betterworldstats.hooks.BWSHook;
import me.xginko.betterworldstats.utils.KyoriUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class BetterWorldStats extends JavaPlugin {

    private static BetterWorldStats instance;
    private static Map<String, LanguageCache> languageCacheMap;
    private static Config config;
    private static Statistics statistics;
    private static BukkitAudiences audiences;
    private static ComponentLogger logger;
    private static Metrics bStats;

    @Override
    public void onEnable() {
        instance = this;
        audiences = BukkitAudiences.create(this);
        logger = ComponentLogger.logger(getLogger().getName());
        bStats = new Metrics(this, 17204);

        logger.info(Component.text("                                              ").style(KyoriUtil.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("     ___      _   _                           ").style(KyoriUtil.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("    | _ ) ___| |_| |_ ___ _ _                 ").style(KyoriUtil.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("    | _ \\/ -_)  _|  _/ -_) '_|                ").style(KyoriUtil.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("  __|___/\\___|\\__|\\__\\___|_|_ _        _      ").style(KyoriUtil.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("  \\ \\    / /__ _ _| |__| / __| |_ __ _| |_ ___").style(KyoriUtil.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("   \\ \\/\\/ / _ \\ '_| / _` \\__ \\  _/ _` |  _(_-<").style(KyoriUtil.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("    \\_/\\_/\\___/_| |_\\__,_|___/\\__\\__,_|\\__/__/").style(KyoriUtil.GUPPIE_GREEN_BOLD));
        logger.info(Component.text("                                              ").style(KyoriUtil.GUPPIE_GREEN_BOLD));

        logger.info("Loading languages");
        reloadLang();
        logger.info("Loading config");
        reloadConfiguration();
        logger.info("Registering commands");
        BWSCmd.reloadCommands();
        logger.info("Done.");
    }

    @Override
    public void onDisable() {
        BWSHook.HOOKS.forEach(BWSHook::unHook);
        if (statistics != null) {
            statistics.shutdown();
            statistics = null;
        }
        if (audiences != null) {
            audiences.close();
            audiences = null;
        }
        if (bStats != null) {
            bStats.shutdown();
            bStats = null;
        }
        config = null;
        languageCacheMap = null;
        logger = null;
        instance = null;
    }

    public static @NotNull BetterWorldStats getInstance() {
        return instance;
    }

    public static @NotNull Statistics getStatistics() {
        return statistics;
    }

    public static @NotNull BukkitAudiences getAudiences() {
        return audiences;
    }

    public static @NotNull Config getConfiguration() {
        return config;
    }

    public static @NotNull ComponentLogger getLog() {
        return logger;
    }

    public static @NotNull LanguageCache getLang(Locale locale) {
        return getLang(locale.toString().toLowerCase());
    }

    public static @NotNull LanguageCache getLang(CommandSender commandSender) {
        return getLang(KyoriUtil.getLocale(commandSender));
    }

    public static @NotNull LanguageCache getLang(String lang) {
        if (!config.auto_lang) return languageCacheMap.get(config.default_lang.toString().toLowerCase());
        return languageCacheMap.getOrDefault(lang.replace("-", "_"), languageCacheMap.get(config.default_lang.toString().toLowerCase()));
    }

    public void reloadPlugin() {
        reloadLang();
        reloadConfiguration();
        BWSCmd.reloadCommands();
    }

    private void reloadConfiguration() {
        try {
            if (statistics != null)
                statistics.shutdown();
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
            File langDirectory = new File(getDataFolder() + "/lang");
            Files.createDirectories(langDirectory.toPath());
            SortedSet<String> locales = new TreeSet<>();
            locales.addAll(getDefaultLocales(getFile()));
            locales.addAll(getPresentLocales(langDirectory));
            for (String localeString : locales) {
                logger.info("Found language file for " + localeString);
                languageCacheMap.put(localeString, new LanguageCache(localeString));
            }
        } catch (Throwable t) {
            logger.error("Error loading language files!", t);
        } finally {
            if (languageCacheMap.isEmpty()) {
                logger.error("Unable to load translations. Disabling.");
                getServer().getPluginManager().disablePlugin(this);
            } else {
                logger.info("Loaded " + languageCacheMap.size() + " translations");
            }
        }
    }

    private static final Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);

    private @NotNull Set<String> getDefaultLocales(File jarFile) {
        try (final JarFile pluginJarFile = new JarFile(jarFile)) {
            return pluginJarFile.stream()
                    .map(zipEntry -> {
                        Matcher matcher = langPattern.matcher(zipEntry.getName());
                        return matcher.find() ? matcher.group(1) : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (Throwable t) {
            logger.error("Failed getting default lang files!", t);
            return Collections.emptySet();
        }
    }

    private @NotNull Set<String> getPresentLocales(File folder) {
        try {
            return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                    .map(file -> {
                        Matcher matcher = langPattern.matcher(file.getName());
                        return matcher.find() ? matcher.group(1) : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (Throwable t) {
            logger.error("Failed getting lang files from plugin folder!", t);
            return Collections.emptySet();
        }
    }
}