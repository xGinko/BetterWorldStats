package me.xGinko.betterworldstats.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.Title;
import me.xGinko.betterworldstats.BetterWorldStats;

import java.io.File;
import java.text.DecimalFormat;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.*;

public class Config {

    private final ConfigFile config;
    public final String default_lang;
    public final DecimalFormat filesize_format;
    public final Set<String> paths_to_scan;
    public final TimeZone timeZone;
    public final boolean auto_lang, log_is_enabled;
    public final long filesize_update_period_millis, server_birth_time_millis;
    public final double additional_spoof_filesize;

    public Config() throws Exception {
        // Create plugin folder first if it does not exist yet
        File pluginFolder = BetterWorldStats.getInstance().getDataFolder();
        if (!pluginFolder.exists() && !pluginFolder.mkdir())
            BetterWorldStats.getLog().error("Failed to create plugin folder.");
        // Load config.yml with ConfigMaster
        this.config = ConfigFile.loadConfig(new File(pluginFolder, "config.yml"));

        // Title
        this.config.setTitle(new Title()
                .withWidth(92)
                .addSolidLine()
                .addLine("           ___      _   _         __      __       _    _ ___ _        _                           ")
                .addLine("          | _ ) ___| |_| |_ ___ _ \\ \\    / /__ _ _| |__| / __| |_ __ _| |_ ___                   ")
                .addLine("          | _ \\/ -_)  _|  _/ -_) '_\\ \\/\\/ / _ \\ '_| / _` \\__ \\  _/ _` |  _(_-<              ")
                .addLine("          |___/\\___|\\__|\\__\\___|_|  \\_/\\_/\\___/_| |_\\__,_|___/\\__\\__,_|\\__/__/          ")
                .addLine("")
                .addSolidLine());

        // Language
        this.default_lang = getString("language.default-language", "en_us",
                "The default language to be used if auto-lang is off or no matching language file was found.").toLowerCase();
        this.auto_lang = getBoolean("language.auto-language", true,
                "Enable / Disable locale based messages.");

        // Settings
        this.server_birth_time_millis = getLong("server-birth-epoch-unix-timestamp", System.currentTimeMillis(),
                "Use a tool like https://www.unixtimestamp.com/ to convert your server launch date to the correct format.\n" +
                        "This option expects you to enter the timestamp in millis. If you have issues with your server age being way too\n" +
                        "high, its probably because you entered the time in seconds and are therefore missing 3 zeros at the end.");
        ZoneId zoneId = ZoneId.systemDefault();
        try {
            zoneId = ZoneId.of(getString("time-zone", zoneId.getId(),
                    "The TimeZone (ZoneId) to use for scheduling restart times."));
        } catch (ZoneRulesException e) {
            BetterWorldStats.getLog().warn("Configured timezone could not be found. Using system default zone '"+zoneId+"'");
        } catch (DateTimeException e) {
            BetterWorldStats.getLog().warn("Configured timezone has an invalid format. Using system default zone '"+zoneId+"'");
        }
        this.timeZone = TimeZone.getTimeZone(zoneId);
        this.filesize_update_period_millis = getInt("filesize-update-period-in-seconds", 3600,
                "The update period at which the file size is checked.") * 1000L;
        this.filesize_format = new DecimalFormat(getString("filesize-format-pattern", "#.##"));
        this.paths_to_scan = new HashSet<>(getList("worlds", Arrays.asList(
                "./world/region",
                "./world_nether/DIM-1/region",
                "./world_the_end/DIM1/region"
        ), "The files to scan. The path you're in is the folder where your server.jar is located."));
        this.additional_spoof_filesize = getDouble("spoof-size", 0.0,
                "How many GB should be added on top of the actual filesize. Useful if you deleted useless chunks.");
        this.log_is_enabled = getBoolean("enable-console-log", false,
                "Whether to log to console when plugin updates filesize.");

        // Placeholders
        this.config.addComment("PlaceholderAPI placeholders:" +
                "\n %worldstats_size%" +
                "\n %worldstats_spoofsize%" +
                "\n %worldstats_players%" +
                "\n %worldstats_age_in_days%" +
                "\n %worldstats_age_in_months%" +
                "\n %worldstats_age_in_years%" +
                "\n %worldstats_file_count%" +
                "\n %worldstats_folder_count%" +
                "\n %worldstats_chunk_file_count%");
        this.config.addComment("These PAPI placeholders return the same values as in the command:" +
                "\n %worldstats_days%" +
                "\n %worldstats_months%" +
                "\n %worldstats_years%");
    }

    public void saveConfig() {
        try {
            this.config.save();
        } catch (Exception e) {
            BetterWorldStats.getLog().error("Failed to save config file!", e);
        }
    }

    public boolean getBoolean(String path, boolean def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getBoolean(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        this.config.addDefault(path, def);
        return this.config.getBoolean(path, def);
    }

    public String getString(String path, String def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getString(path, def);
    }

    public String getString(String path, String def) {
        this.config.addDefault(path, def);
        return this.config.getString(path, def);
    }

    public double getDouble(String path, double def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getDouble(path, def);
    }

    public double getDouble(String path, double def) {
        this.config.addDefault(path, def);
        return this.config.getDouble(path, def);
    }

    public int getInt(String path, int def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getInteger(path, def);
    }

    public int getInt(String path, int def) {
        this.config.addDefault(path, def);
        return this.config.getInteger(path, def);
    }
    
    public long getLong(String path, long def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getLong(path, def);
    }

    public List<String> getList(String path, List<String> def, String comment) {
        this.config.addDefault(path, def, comment);
        return this.config.getStringList(path);
    }

    public List<String> getList(String path, List<String> def) {
        this.config.addDefault(path, def);
        return this.config.getStringList(path);
    }
}