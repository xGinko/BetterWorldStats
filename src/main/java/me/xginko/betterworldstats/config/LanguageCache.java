package me.xginko.betterworldstats.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageCache {

    private final @NotNull ConfigFile langFile;
    private final @NotNull String no_permission_serialized;
    private final @NotNull List<String> world_stats_message_serialized;

    public LanguageCache(String language) throws Exception {
        BetterWorldStats plugin = BetterWorldStats.getInstance();
        File langYML = new File(plugin.getDataFolder() + "/lang", language + ".yml");
        // Check if the file already exists and save the one from the plugin's resources folder if it does not
        if (!langYML.exists()) {
            plugin.saveResource("lang/" + language + ".yml", false);
        }
        // Finally, load the lang file with configmaster
        this.langFile = ConfigFile.loadConfig(langYML);

        this.langFile.addComment(
                "Command Placeholders:" +
                "\n %size%        | %spoofsize%     | %players%      | %years%      | %months%       | %days%" +
                "\n %age_in_days% | %age_in_months% | %age_in_years% | %file_count% | %folder_count% | %chunk_count%" +
                "\n %entity_count% "
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
            BetterWorldStats.logger().error("Failed to save language file: {}", langYML.getName(), e);
        }
    }

    public @NotNull Component noPermissionMsg(CommandSender sender) {
        return MiniMessage.miniMessage().deserialize(no_permission_serialized, Util.papiTagResolver(sender));
    }

    public @NotNull List<Component> worldStatsMsg(
            CommandSender sender,
            String years, String months, String days,
            String players, String fileSize, String spoofSize,
            String ageAsDays, String ageAsMonths, String ageAsYears,
            String fileCount, String folderCount, String chunkCount,
            String entityCount
    ) {
        return world_stats_message_serialized.stream()
                .map(line -> MiniMessage.miniMessage().deserialize(line
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
                                .replace("%chunk_count%", chunkCount)
                                .replace("%entity_count%", entityCount),
                        Util.papiTagResolver(sender)
                ))
                .collect(Collectors.toList());
    }

    private @NotNull String getTranslation(@NotNull String path, @NotNull String defaultTranslation) {
        this.langFile.addDefault(path, defaultTranslation);
        return Util.replaceAmpersand(this.langFile.getString(path, defaultTranslation));
    }

    private @NotNull List<String> getListTranslation(@NotNull String path, @NotNull String... defaultTranslation) {
        this.langFile.addDefault(path, Arrays.asList(defaultTranslation));
        return this.langFile.getStringList(path).stream().map(Util::replaceAmpersand).collect(Collectors.toList());
    }
}
