package me.xginko.betterworldstats.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public enum TranslatableMapper implements BiConsumer<TranslatableComponent, Consumer<Component>> {
    INSTANCE;

    public static final ComponentFlattener FLATTENER = ComponentFlattener.basic().toBuilder()
            .complexMapper(TranslatableComponent.class, TranslatableMapper.INSTANCE)
            .build();

    @Override
    public void accept(final TranslatableComponent translatableComponent, final Consumer<Component> componentConsumer) {
        for (final Translator source : GlobalTranslator.translator().sources()) {
            if (source instanceof TranslationRegistry && ((TranslationRegistry) source).contains(translatableComponent.key())) {
                componentConsumer.accept(GlobalTranslator.render(translatableComponent, Locale.getDefault()));
                return;
            }
        }

        final @Nullable String fallback = translatableComponent.fallback();
        if (fallback == null) return;

        for (final Translator source : GlobalTranslator.translator().sources()) {
            if (source instanceof TranslationRegistry && ((TranslationRegistry) source).contains(fallback)) {
                componentConsumer.accept(GlobalTranslator.render(Component.translatable(fallback), Locale.getDefault()));
                return;
            }
        }
    }
}