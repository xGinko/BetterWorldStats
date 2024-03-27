package me.xginko.betterworldstats.hooks;

import java.util.HashSet;
import java.util.Set;

public interface BWSHook {

    String pluginName();
    boolean canHook();
    void hook();
    void unHook();

    Set<BWSHook> HOOKS = new HashSet<>();

    static void reloadHooks() {
        HOOKS.forEach(BWSHook::unHook);
        HOOKS.clear();

        try {
            HOOKS.add(new PAPIExpansion());
        } catch (Throwable ignored) {
        }

        for (BWSHook hook : HOOKS) {
            if (hook.canHook()) {
                hook.hook();
            } else {
                HOOKS.remove(hook);
            }
        }
    }
}
