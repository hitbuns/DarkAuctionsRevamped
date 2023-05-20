package com.ConquestTechMC.config;

import com.MenuAPI.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class SettingsConfig extends Config {

    private static SettingsConfig Instance;

    public SettingsConfig(JavaPlugin javaPlugin) {
        super(javaPlugin, javaPlugin.getDataFolder(), "settings.yml",
                "default-settings.yml");

        Instance = this;
    }

    public static SettingsConfig getInstance() {
        return Instance;
    }

    public TimeUnit getCooldownTimeUnit() {
        return contains("settings.time.cooldown.timeUnit") ?
                TimeUnit.valueOf(getString("settings.time.cooldown.timeUnit")) :
                TimeUnit.SECONDS;
    }

    public TimeUnit getEventTimeUnit() {
        return contains("settings.time.eventSpan.timeUnit") ?
                TimeUnit.valueOf(getString("settings.time.eventSpan.timeUnit")) :
                TimeUnit.SECONDS;
    }

    public long getCooldownTimeSpan() {
        return getLong("settings.time.cooldown.timeSpan",0L);
    }

    public long getEventTimeSpan() {
        return getLong("settings.time.eventSpan.timeSpan",0L);
    }

}
