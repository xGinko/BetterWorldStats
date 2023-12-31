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
        BetterWorldStats plugin = BetterWorldStats.getInstance();
        File langYML = new File(plugin.getDataFolder() + File.separator + "lang", lang + ".yml");
        // Check if the lang folder has already been created
        File parent = langYML.getParentFile();
        if (!parent.exists() && !parent.mkdir())
            BetterWorldStats.getLog().severe("Unable to create lang directory.");
        // Check if the file already exists and save the one from the plugins resources folder if it does not
        if (!langYML.exists())
            plugin.saveResource("lang" + File.separator + lang + ".yml", false);
        // Finally load the lang file with configmaster
        this.langFile = ConfigFile.loadConfig(langYML);

        langFile.addComment("Command Placeholders:" +
                "\n" + "%size% | %spoofsize% | %players% | %years% | %months% | %days%");

        this.world_stats_message = getStringListTranslation("stats-message", Arrays.asList(
                "&3-----------------------------------------------------",
                " &7The server has spawned &6%players% player(s)&7 at least once",
                " &7The map is &6%years% year(s)&7, &6%months% month(s)&7 and &6%days% day(s)&7 old",
                " &7The world (with compression) is a total of &6%size% GB",
                "&3-----------------------------------------------------"
        ));

        this.no_permission = getStringTranslation("no-permission", "You don't have permission to use this command.");

        try {
            langFile.save();
        } catch (Exception e) {
            BetterWorldStats.getLog().severe("Failed to save language file: "+ langYML.getName() +" - " + e.getLocalizedMessage());
        }
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
