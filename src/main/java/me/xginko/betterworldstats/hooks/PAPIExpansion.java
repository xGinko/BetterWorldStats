package me.xginko.betterworldstats.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xginko.betterworldstats.BetterWorldStats;
import me.xginko.betterworldstats.Statistics;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PAPIExpansion extends PlaceholderExpansion implements BWSHook {

    private final @NotNull Statistics statistics;

    PAPIExpansion() {
        this.statistics = BetterWorldStats.statistics();
    }

    @Override
    public String pluginName() {
        return "PlaceholderAPI";
    }

    @Override
    public boolean canHook() {
        return BetterWorldStats.getInstance().getServer().getPluginManager().isPluginEnabled(pluginName());
    }

    @Override
    public void hook() {
        register();
    }

    @Override
    public void unHook() {
        unregister();
    }

    @Override
    public @NotNull String getAuthor() {
        return BetterWorldStats.getInstance().getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "worldstats";
    }

    @Override
    public @NotNull String getVersion() {
        return BetterWorldStats.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        switch (identifier) {
            case "size":
                return statistics.worldStats.getSize();
            case "spoofsize":
                return statistics.worldStats.getSpoofedSize();
            case "file_count":
                return statistics.worldStats.getFileCount();
            case "folder_count":
                return statistics.worldStats.getFolderCount();
            case "chunk_count":
                return statistics.worldStats.getChunkCount();
            case "entity_count":
                return statistics.worldStats.getEntityCount();
            case "playerStats":
                return statistics.playerStats.getUniqueJoins();
            case "days":
                return statistics.birthCalendar.getDaysPart().toString();
            case "months":
                return statistics.birthCalendar.getMonthsPart().toString();
            case "years":
                return statistics.birthCalendar.getYearsPart().toString();
            case "age_in_days":
                return statistics.birthCalendar.asDays().toString();
            case "age_in_months":
                return statistics.birthCalendar.asMonths().toString();
            case "age_in_years":
                return statistics.birthCalendar.asYears().toString();
            default:
                return null;
        }
    }

    private static @NotNull String tryParse(@Nullable CommandSender sender, @NotNull String input) {
        try {
            if (sender instanceof Player) {
                return PlaceholderAPI.setPlaceholders((Player) sender, input);
            } else {
                return PlaceholderAPI.setPlaceholders(null, input);
            }
        } catch (Throwable t) {
            return input;
        }
    }

    public static @NotNull TagResolver papiTagResolver(@Nullable CommandSender sender) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> Tag.selfClosingInserting(
                LegacyComponentSerializer.legacySection().deserialize(
                        tryParse(sender, '%' + argumentQueue.popOr("papi tag requires an argument").value() + '%')
                )
        ));
    }
}