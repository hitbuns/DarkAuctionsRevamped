package com.ConquestTechMC.config;

import com.MenuAPI.Config;
import org.bukkit.plugin.java.JavaPlugin;

public class RewardsConfig extends Config {

    public RewardsConfig(JavaPlugin javaPlugin) {
        super(javaPlugin, javaPlugin.getDataFolder(),"rewards.yml");

    }

    /*

    dark-auctions:
        %mi-ItemId%: %chance%
        %mi-ItemId2%: %chance%
     */


}
