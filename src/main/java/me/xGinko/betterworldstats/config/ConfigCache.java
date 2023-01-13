package me.xGinko.betterworldstats.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xGinko.betterworldstats.BetterWorldStats;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class ConfigCache {

    private ConfigFile config;
    private final File configFile;
    private final Logger logger;
    public final String default_lang;
    public final DecimalFormat filesize_display_format;
    public final HashSet<String> directories_to_scan = new HashSet<>();
    public final boolean auto_lang, log_is_enabled;
    public final long server_birth_time, filesize_update_period;
    public final double additional_spoofed_filesize;

    public ConfigCache() {
        BetterWorldStats plugin = BetterWorldStats.getInstance();
        configFile = new File(plugin.getDataFolder(), "config.yml");
        logger = plugin.getLogger();
        createFiles();
        loadConfig();

        this.default_lang = getString("language.default-language", "en-us", "The default language to be used if auto-lang is off or no matching language file was found.").replace("_", "-");
        this.auto_lang = getBoolean("language.auto-language", true, "Enable / Disable locale based messages.");

        this.server_birth_time = getLong("server-birth-epoch-unix-timestamp", System.currentTimeMillis(), "Use a tool like https://www.unixtimestamp.com/ to convert your server launch date to the correct format.");
        this.filesize_update_period = getInt("filesize-update-period-in-seconds", 3600, "The update period at which the file size is checked.") * 20L;
        this.filesize_display_format = new DecimalFormat(getString("filesize-format-pattern", "#.##"));
        directories_to_scan.addAll(getList("worlds", Arrays.asList(
                "./world/region",
                "./world_nether/DIM-1/region",
                "./world_the_end/DIM1/region"
        ), "The files to scan. The path you're in is the folder where your server.jar is located."));
        this.additional_spoofed_filesize = getDouble("spoof-size", 0.0, "How many GB should be added on top of the actual filesize. Useful if you deleted useless chunks.");
        this.log_is_enabled = getBoolean("enable-console-log", true, "Whether to log to console when plugin updates filesize.");

        config.addComment("PlaceholderAPI placeholders:\n %worldstats_size%\n %worldstats_spoof%\n %worldstats_players%\n %worldstats_ageindays%");
        config.addComment("These placeholders return the same values as the command:\n %worldstats_days%\n %worldstats_months%\n %worldstats_years%");
    }

    private void createFiles() {
        try {
            File parent = new File(configFile.getParent());
            if (!parent.exists()) {
                if (!parent.mkdir())
                    logger.severe("Unable to create plugin directory.");
            }
            if (!configFile.exists()) {
                if (!configFile.createNewFile())
                    logger.severe("Unable to create config file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        try {
            config = ConfigFile.loadConfig(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            config.save();
        } catch (IOException e) {
            logger.severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    public boolean getBoolean(String path, boolean def, String comment) {
        config.addDefault(path, def, comment);
        return config.getBoolean(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, def);
    }

    public String getString(String path, String def, String comment) {
        config.addDefault(path, def, comment);
        return config.getString(path, def);
    }

    public String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, def);
    }

    public double getDouble(String path, Double def, String comment) {
        config.addDefault(path, def, comment);
        return config.getDouble(path, def);
    }

    public double getDouble(String path, Double def) {
        config.addDefault(path, def);
        return config.getDouble(path, def);
    }

    public int getInt(String path, int def, String comment) {
        config.addDefault(path, def, comment);
        return config.getInteger(path, def);
    }

    public int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInteger(path, def);
    }
    
    public long getLong(String path, long def, String comment) {
        config.addDefault(path, def, comment);
        return config.getLong(path, def);
    }

    public List<String> getList(String path, List<String> def, String comment) {
        config.addDefault(path, def, comment);
        return config.getStringList(path);
    }

    public List<String> getList(String path, List<String> def) {
        config.addDefault(path, def);
        return config.getStringList(path);
    }
}
