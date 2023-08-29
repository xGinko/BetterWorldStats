package me.xGinko.betterworldstats.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xGinko.betterworldstats.BetterWorldStats;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Config {

    private final ConfigFile config;
    public final String default_lang;
    public final DecimalFormat filesize_display_format;
    public final HashSet<String> directories_to_scan = new HashSet<>();
    public final boolean auto_lang, log_is_enabled;
    public final long server_birth_time, filesize_update_period_seconds;
    public final double additional_spoofed_filesize;

    public Config() throws Exception {
        this.config = loadConfig(new File(BetterWorldStats.getInstance().getDataFolder(), "config.yml"));
        createTitle();

        this.default_lang = getString("language.default-language", "en_us", "The default language to be used if auto-lang is off or no matching language file was found.").toLowerCase();
        this.auto_lang = getBoolean("language.auto-language", true, "Enable / Disable locale based messages.");

        this.server_birth_time = getLong("server-birth-epoch-unix-timestamp", System.currentTimeMillis(), "Use a tool like https://www.unixtimestamp.com/ to convert your server launch date to the correct format.");
        this.filesize_update_period_seconds = getInt("filesize-update-period-in-seconds", 3600, "The update period at which the file size is checked.");
        this.filesize_display_format = new DecimalFormat(getString("filesize-format-pattern", "#.##"));
        directories_to_scan.addAll(getList("worlds", Arrays.asList(
                "./world/region",
                "./world_nether/DIM-1/region",
                "./world_the_end/DIM1/region"
        ), "The files to scan. The path you're in is the folder where your server.jar is located."));
        this.additional_spoofed_filesize = getDouble("spoof-size", 0.0, "How many GB should be added on top of the actual filesize. Useful if you deleted useless chunks.");
        this.log_is_enabled = getBoolean("enable-console-log", true, "Whether to log to console when plugin updates filesize.");

        config.addComment("PlaceholderAPI placeholders:\n %worldstats_size%\n %worldstats_spoofsize%\n %worldstats_players%\n %worldstats_ageindays%");
        config.addComment("These placeholders return the same values as the command:\n %worldstats_days%\n %worldstats_months%\n %worldstats_years%");
    }

    private ConfigFile loadConfig(File ymlFile) throws Exception {
        File parent = new File(ymlFile.getParent());
        if (!parent.exists())
            if (!parent.mkdir())
                BetterWorldStats.getLog().severe("Unable to create plugin config directory.");
        if (!ymlFile.exists())
            ymlFile.createNewFile(); // Result can be ignored because this method only returns false if the file already exists
        return ConfigFile.loadConfig(ymlFile);
    }

    public void saveConfig() {
        try {
            config.save();
        } catch (Exception e) {
            BetterWorldStats.getLog().severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    private void createTitle() {
        config.addDefault("language", null);
        config.addComment("language", "                                                                                ");
        config.addComment("language", "  ___      _   _         __      __       _    _ ___ _        _                 ");
        config.addComment("language", " | _ ) ___| |_| |_ ___ _ \\ \\    / /__ _ _| |__| / __| |_ __ _| |_ ___         ");
        config.addComment("language", " | _ \\/ -_)  _|  _/ -_) '_\\ \\/\\/ / _ \\ '_| / _` \\__ \\  _/ _` |  _(_-<    ");
        config.addComment("language", " |___/\\___|\\__|\\__\\___|_|  \\_/\\_/\\___/_| |_\\__,_|___/\\__\\__,_|\\__/__/");
        config.addComment("language", "                                                                                ");
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
