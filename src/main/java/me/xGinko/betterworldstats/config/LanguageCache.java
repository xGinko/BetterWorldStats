package me.xGinko.betterworldstats.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xGinko.betterworldstats.BetterWorldStats;
import me.xGinko.betterworldstats.utils.KyoriUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageCache {

    private final ConfigFile langFile;
    public final Component no_permission;
    public final List<Component> world_stats_message;

    public LanguageCache(String lang) throws Exception {
        BetterWorldStats plugin = BetterWorldStats.getInstance();
        File langYML = new File(plugin.getDataFolder() + File.separator + "lang", lang + ".yml");
        // Check if the lang folder has already been created
        File parent = langYML.getParentFile();
        if (!parent.exists() && !parent.mkdir())
            BetterWorldStats.getLog().error("Unable to create lang directory.");
        // Check if the file already exists and save the one from the plugins resources folder if it does not
        if (!langYML.exists())
            plugin.saveResource("lang" + File.separator + lang + ".yml", false);
        // Finally, load the lang file with configmaster
        this.langFile = ConfigFile.loadConfig(langYML);

        this.langFile.addComment(
                "Command Placeholders:" +
                "\n %size%        | %spoofsize%     | %players%      | %years%      | %months%       | %days%" +
                "\n %age_in_days% | %age_in_months% | %age_in_years% | %file_count% | %folder_count% | %chunk_file_count%"
        );

        this.world_stats_message = getListTranslation("stats-message",
                "<dark_aqua>-----------------------------------------------------",
                " <gray>The server has spawned <gold>%players% player(s)<gray> at least once",
                " <gray>The map is <gold>%years% year(s)<gray>, <gold>%months% month(s)<gray> and <gold>%days% day(s)<gray> old",
                " <gray>The world is a total of <gold>%size% GB",
                "<dark_aqua>-----------------------------------------------------"
        );

        this.no_permission = getTranslation("no-permission",
                "<red>You don't have permission to use this command.");

        try {
            this.langFile.save();
        } catch (Exception e) {
            BetterWorldStats.getLog().error("Failed to save language file: "+ langYML.getName(), e);
        }
    }

    public @NotNull Component getTranslation(@NotNull String path, @NotNull String defaultTranslation) {
        this.langFile.addDefault(path, defaultTranslation);
        return MiniMessage.miniMessage().deserialize(KyoriUtil.translateChatColor('&', this.langFile.getString(path, defaultTranslation)));
    }

    public @NotNull List<Component> getListTranslation(@NotNull String path, @NotNull String... defaultTranslation) {
        this.langFile.addDefault(path, Arrays.asList(defaultTranslation));
        return this.langFile.getStringList(path).stream()
                .map(line -> MiniMessage.miniMessage().deserialize(KyoriUtil.translateChatColor('&', line)))
                .collect(Collectors.toList());
    }
}
