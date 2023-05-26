package com.ConquestTechMC;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Econ {


    private Economy econ;
    private Plugin plugin;
    private static Econ Instance;

    public static Econ getInstance() {
        return Instance;
    }

    public Economy getEconomy() {
        return econ;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Econ(Plugin plugin) {
        (Instance = this).plugin = plugin;
        setupEconomy();
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


}