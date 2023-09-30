package me.xGinko.betterworldstats.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xGinko.betterworldstats.BetterWorldStats;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageCache {

    private final ConfigFile langFile;
    public final String no_permission;
    public final List<String> world_stats_message;

    public LanguageCache(String lang) throws Exception {
        this.langFile = loadLang(new File(BetterWorldStats.getInstance().getDataFolder() + File.separator + "lang", lang + ".yml"));

        langFile.addComment("Command Placeholders:\n" + "%size% | %players% | %years% | %months% | %days%");

        this.world_stats_message = getStringListTranslation("stats-message", Arrays.asList(
                "&3-----------------------------------------------------",
                " &7The server has spawned &6%players% player(s)&7 at least once",
                " &7The map is &6%years% year(s)&7, &6%month% month(s)&7 and &6%days% day(s)&7 old",
                " &7The world (with compression) is a total of &6%size% GB",
                "&3-----------------------------------------------------"
        ));
        this.no_permission = getStringTranslation("no-permission", "You don't have permission to use this command.");

        try {
            langFile.save();
        } catch (Exception e) {
            BetterWorldStats.getLog().severe("Failed to save language file: "+ langFile.getFile().getName() +" - " + e.getLocalizedMessage());
        }
    }

    private ConfigFile loadLang(File ymlFile) throws Exception {
        File parent = new File(ymlFile.getParent());
        if (!parent.exists())
            if (!parent.mkdir())
                BetterWorldStats.getLog().severe("Unable to create lang directory.");
        if (!ymlFile.exists())
            ymlFile.createNewFile(); // Result can be ignored because this method only returns false if the file already exists
        return ConfigFile.loadConfig(ymlFile);
    }

    private String getStringTranslation(String path, String defaultTranslation) {
        langFile.addDefault(path, defaultTranslation);
        return ChatColor.translateAlternateColorCodes('&', langFile.getString(path, defaultTranslation));
    }

    private String getStringTranslation(String path, String defaultTranslation, String comment) {
        langFile.addDefault(path, defaultTranslation, comment);
        return ChatColor.translateAlternateColorCodes('&', langFile.getString(path, defaultTranslation));
    }

    private List<String> getStringListTranslation(String path, List<String> defaultTranslation) {
        langFile.addDefault(path, defaultTranslation);
        return langFile.getStringList(path).stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
    }

    private List<String> getStringListTranslation(String path, List<String> defaultTranslation, String comment) {
        langFile.addDefault(path, defaultTranslation, comment);
        return langFile.getStringList(path).stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
    }
}
