package me.xginko.betterworldstats.adventure;

import me.xginko.betterworldstats.utils.TranslatableMapper;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.logger.slf4j.ComponentLoggerProvider;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

@SuppressWarnings("UnstableApiUsage")
public final class ComponentLoggerProviderImpl implements ComponentLoggerProvider {

    private static final @NotNull ANSIComponentSerializer SERIALIZER = ANSIComponentSerializer.builder()
            .flattener(TranslatableMapper.FLATTENER)
            .build();

    @Override
    public @NotNull ComponentLogger logger(final @NotNull LoggerHelper helper, final @NotNull String name) {
        return helper.delegating(LoggerFactory.getLogger(name), SERIALIZER::serialize);
    }
}

