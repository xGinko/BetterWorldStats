package me.xginko.betterworldstats.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.utils.KyoriUtil;
import me.xginko.betterworldstats.utils.PAPIUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageCache {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final ConfigFile langFile;
    private final String no_permission_serialized;
    private final List<String> world_stats_message_serialized;

    public LanguageCache(String lang) throws Exception {
        BetterWorldStats plugin = BetterWorldStats.getInstance();
        File langYML = new File(plugin.getDataFolder() + File.separator + "lang", lang + ".yml");
        // Check if the lang folder has already been created
        File parent = langYML.getParentFile();
        if (!parent.exists() && !parent.mkdir())
            BetterWorldStats.getLog().error("Unable to create lang directory.");
        // Check if the file already exists and save the one from the plugin's resources folder if it does not
        if (!langYML.exists())
            plugin.saveResource("lang" + File.separator + lang + ".yml", false);
        // Finally, load the lang file with configmaster
        this.langFile = ConfigFile.loadConfig(langYML);

        this.langFile.addComment(
                "Command Placeholders:" +
                "\n %size%        | %spoofsize%     | %players%      | %years%      | %months%       | %days%" +
                "\n %age_in_days% | %age_in_months% | %age_in_years% | %file_count% | %folder_count% | %chunk_file_count%"
        );

        this.world_stats_message_serialized = getListTranslation("stats-message",
                "<dark_aqua>-----------------------------------------------------",
                " <gray>The server has spawned <gold>%players% player(s)<gray> at least once",
                " <gray>The map is <gold>%years% year(s)<gray>, <gold>%months% month(s)<gray> and <gold>%days% day(s)<gray> old",
                " <gray>The world is a total of <gold>%size% GB",
                "<dark_aqua>-----------------------------------------------------"
        );

        this.no_permission_serialized = getTranslation("no-permission",
                "<red>You don't have permission to use this command.");

        try {
            this.langFile.save();
        } catch (Exception e) {
            BetterWorldStats.getLog().error("Failed to save language file: " + langYML.getName(), e);
        }
    }

    public Component noPermissionMsg() {
        return miniMessage.deserialize(PAPIUtil.tryPopulate(no_permission_serialized));
    }

    public List<Component> worldStatsMsg(
            String years, String months, String days,
            String players, String fileSize, String spoofSize,
            String ageAsDays, String ageAsMonths, String ageAsYears,
            String fileCount, String folderCount, String chunkFileCount
    ) {
        return world_stats_message_serialized.stream()
                .map(line -> miniMessage.deserialize(PAPIUtil.tryPopulate(line
                        .replace("%years%", years)
                        .replace("%months%", months)
                        .replace("%days%", days)
                        .replace("%players%", players)
                        .replace("%size%", fileSize)
                        .replace("%spoofsize%", spoofSize)
                        .replace("%age_in_days%", ageAsDays)
                        .replace("%age_in_months%", ageAsMonths)
                        .replace("%age_in_years%", ageAsYears)
                        .replace("%file_count%", fileCount)
                        .replace("%folder_count%", folderCount)
                        .replace("%chunk_file_count%", chunkFileCount)
                )))
                .collect(Collectors.toList());
    }

    private @NotNull String getTranslation(@NotNull String path, @NotNull String defaultTranslation) {
        this.langFile.addDefault(path, defaultTranslation);
        return KyoriUtil.altColorCodesToMiniMessageTags('&', this.langFile.getString(path, defaultTranslation));
    }

    private @NotNull List<String> getListTranslation(@NotNull String path, @NotNull String... defaultTranslation) {
        this.langFile.addDefault(path, Arrays.asList(defaultTranslation));
        return this.langFile.getStringList(path).stream()
                .map(line -> KyoriUtil.altColorCodesToMiniMessageTags('&', line))
                .collect(Collectors.toList());
    }
}
