package me.xGinko.BetterWorldStats.config;

import me.xGinko.BetterWorldStats.BetterWorldStats;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LanguageCache {
    private final FileConfiguration fileConfiguration;
    boolean addedMissing = false;
    public String noPermissions;
    public List<String> worldStatsMessage;

    public LanguageCache(String lang) {
        BetterWorldStats plugin = BetterWorldStats.getInstance();
        File langFile = new File(plugin.getDataFolder() + File.separator + "lang", lang + ".yml");
        fileConfiguration = new YamlConfiguration();

        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            plugin.saveResource("lang" + File.separator + lang + ".yml", false);
        }
        try {
            fileConfiguration.load(langFile);

            this.worldStatsMessage = getListTranslation("stats-message", Arrays.asList(
                    "&6-----------------------------------------------------",
                    "&3The server has spawned &a%players%&3 player(s) at least once",
                    "&3The map is &a%years%&3 years, &a%months%&3 months and &a%days%&3 days old",
                    "&3The world (with compression) is a total of &a%spoof%&3 GB",
                    "&6-----------------------------------------------------"
            ));
            this.noPermissions = getStringTranslation("no-permission", "You don't have permission to use this command.");

            if (addedMissing) fileConfiguration.save(langFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            BetterWorldStats.getInstance().getLogger().warning("Translation file " + langFile + " is not formatted properly. Skipping it.");
        }
    }

    public List<String> getListTranslation(String path, List<String> defaultTranslation) {
        List<String> translation = fileConfiguration.getStringList(path);
        if (translation.isEmpty()) {
            fileConfiguration.set(path, defaultTranslation);
            addedMissing = true;
            return defaultTranslation;
        }
        return translation;
    }

    public String getStringTranslation(String path, String defaultTranslation) {
        String translation = fileConfiguration.getString(path);
        if (translation == null) {
            fileConfiguration.set(path, defaultTranslation);
            addedMissing = true;
            return defaultTranslation;
        }
        return translation;
    }
}
