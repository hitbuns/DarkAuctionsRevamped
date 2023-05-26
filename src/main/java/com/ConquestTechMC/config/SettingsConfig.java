package com.ConquestTechMC.config;

import com.MenuAPI.Config;
import com.MenuAPI.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    public Location getEventLocation() {
        try {
            return new Location(Bukkit.getWorld(getString("settings.auction-location.world", "world")),
                    getDouble("settings.auction-location.x", 0),
                    getDouble("settings.auction-location.y", 0),
                    getDouble("settings.auction-location.z", 0));
        } catch (Exception e) {
            return new Location(Bukkit.getWorld("world"),0,0,0);
        }
    }

    public Location getEventSpawnLocation() {
        try {
            return new Location(Bukkit.getWorld(getString("settings.spawn-location.world", "world")),
                    getDouble("settings.spawn-location.x", 0),
                    getDouble("settings.spawn-location.y", 0),
                    getDouble("settings.spawn-location.z", 0));
        } catch (Exception e) {
            return new Location(Bukkit.getWorld("world"),0,0,0);
        }
    }

    public int getRandomAuctionItemAmount() {
        return Utils.RNG_INT(getMinAuctionItemAmount(),getMaxAuctionItemAmount());
    }

    public int getAuctionCooldown() {
        return getInt("auction-countdown-seconds",10);
    }

    public int getAuctionStartCooldown() {
        return getInt("auction-countdown-start-seconds",30);
    }

    public int getMinAuctionItemAmount() {
        return Math.min(Math.max(1,getInt("settings.auction-min",1)),getMaxAuctionItemAmount());
    }

    public int getMaxAuctionItemAmount() {
        return Math.max(1,getInt("settings.auction-max"));
    }

}
