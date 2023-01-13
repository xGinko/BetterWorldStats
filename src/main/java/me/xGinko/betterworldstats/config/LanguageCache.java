package me.xGinko.betterworldstats.config;

import me.xGinko.betterworldstats.BetterWorldStats;
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
    public String no_permissions;
    public List<String> world_stats_message;

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

            this.world_stats_message = getListTranslation("stats-message", Arrays.asList(
                    "&3-----------------------------------------------------",
                    " &7The server has spawned &6%players% player(s)&7 at least once",
                    " &7The map is &6%years% year(s)&7, &6%month% month(s)&7 and &6%days% day(s)&7 old",
                    " &7The world (with compression) is a total of &6%size% GB",
                    "&3-----------------------------------------------------"
            ));
            this.no_permissions = getStringTranslation("no-permission", "You don't have permission to use this command.");

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
