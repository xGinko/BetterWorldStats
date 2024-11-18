package me.xginko.betterworldstats.hooks;

import me.xginko.betterworldstats.utils.Disableable;
import me.xginko.betterworldstats.utils.Enableable;

import java.util.HashSet;
import java.util.Set;

public interface BWSHook extends Enableable, Disableable {

    String pluginName();
    boolean shouldEnable();

    Set<BWSHook> HOOKS = new HashSet<>();

    static void reloadHooks() {
        HOOKS.forEach(Disableable::disable);
        HOOKS.clear();

        try {
            HOOKS.add(new PAPIExpansion());
        } catch (Throwable ignored) {
        }

        for (BWSHook hook : HOOKS) {
            if (hook.shouldEnable()) hook.enable();
            else HOOKS.remove(hook);
        }
    }
}
