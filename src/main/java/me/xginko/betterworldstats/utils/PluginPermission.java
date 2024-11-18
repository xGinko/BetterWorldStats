package me.xginko.betterworldstats.utils;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum PluginPermission {

    RELOAD_CMD(new Permission("betterworldstats.reload", PermissionDefault.OP)),
    VERSION_CMD(new Permission("betterworldstats.version", PermissionDefault.OP)),
    WORLDSTATS_CMD(new Permission("betterworldstats.worldstats", PermissionDefault.TRUE));

    private final Permission permission;

    PluginPermission(Permission permission) {
        this.permission = permission;
    }

    public Permission get() {
        return permission;
    }

    public static void registerAll() {
        for (PluginPermission pluginPermission : PluginPermission.values()) {
            try {
                Bukkit.getPluginManager().addPermission(pluginPermission.get());
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public static void unregisterAll() {
        for (PluginPermission pluginPermission : PluginPermission.values()) {
            try {
                Bukkit.getPluginManager().removePermission(pluginPermission.get());
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
