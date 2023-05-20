package com.ConquestTechMC;

import com.ConquestTechMC.config.SettingsConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @EventHandler
    public void onEnable() {

        new SettingsConfig(this);

        registerCommands();
        registerListeners();


    }

    void registerCommands() {

    }

    void registerListeners() {


    }

}