package me.xGinko.betterworldstats.config;

import me.xGinko.betterworldstats.BetterWorldStats;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class ConfigCache {
    private final Logger logger;
    private FileConfiguration config;
    public final String default_lang;
    public final DecimalFormat fileSizeFormat;
    public final HashSet<String> directoriesToScan = new HashSet<>();
    public final boolean auto_lang, logIsEnabled;
    public final long serverBirthTime, fileSizeUpdateDelay;
    public final double spoofSize;

    private final File configPath;
    public ConfigCache() {
        BetterWorldStats plugin = BetterWorldStats.getInstance();
        logger = plugin.getLogger();
        config = plugin.getConfig();
        configPath = new File(plugin.getDataFolder(), "config.yml");

        this.default_lang = getString("language.default-language", "en-us").replace("_", "-");
        this.auto_lang = getBoolean("language.auto-language", true);

        this.serverBirthTime = getLong("server-birth-epoch-unix-timestamp", System.currentTimeMillis());
        this.fileSizeUpdateDelay = getInt("filesize-update-period-in-seconds", 3600) * 20L;
        this.fileSizeFormat = new DecimalFormat(getString("filesize-format-pattern", "#.##"));
        directoriesToScan.addAll(getList("worlds", Arrays.asList(
                "./world/region",
                "./world_nether/DIM-1/region",
                "./world_the_end/DIM1/region"
        )));
        this.spoofSize = getDouble("spoof-size", 0.0);
        this.logIsEnabled = getBoolean("enable-console-log", true);
    }

    public void saveConfig() {
        try {
            config.save(configPath);
            config = BetterWorldStats.getInstance().getConfig();
        } catch (IOException e) {
            logger.severe("Failed to save configuration file! - " + e.getLocalizedMessage());
        }
    }

    public boolean getBoolean(String path, boolean def) {
        if (config.isSet(path)) return config.getBoolean(path, def);
        config.set(path, def);
        return def;
    }

    public String getString(String path, String def) {
        if (config.isSet(path)) return config.getString(path, def);
        config.set(path, def);
        return def;
    }

    public double getDouble(String path, Double def) {
        if (config.isSet(path)) return config.getDouble(path, def);
        config.set(path, def);
        return def;
    }

    public int getInt(String path, int def) {
        if (config.isSet(path)) return config.getInt(path, def);
        config.set(path, def);
        return def;
    }

    public List<String> getList(String path, List<String> def) {
        if (config.isSet(path)) return config.getStringList(path);
        config.set(path, def);
        return def;
    }

    public long getLong(String path, long def) {
        if (config.isSet(path)) return config.getLong(path);
        config.set(path, def);
        return def;
    }
}
