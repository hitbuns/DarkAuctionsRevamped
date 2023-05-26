package com.ConquestTechMC.config;

import com.MenuAPI.Config;
import com.MenuAPI.Configs.SaveItemConfig;
import com.MenuAPI.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class RewardsConfig extends Config {


    private static RewardsConfig Instance;

    public RewardsConfig(JavaPlugin javaPlugin) {
        super(javaPlugin, javaPlugin.getDataFolder(),"rewards.yml","default-rewards.yml");
        Instance = this;
    }

    public static RewardsConfig getInstance() {
        return Instance;
    }

    /*

    dark-auctions:
        %mi-ItemId%: %chance%
        %mi-ItemId2%: %chance%
     */

    public double getTotalChance() {
        ConfigurationSection configurationSection = getorAddConfigurationSection("dark-auctions");
        return configurationSection
                .getKeys(false).stream().filter(s ->
                        !Utils.isNullorAir(SaveItemConfig.getInstance().loadItem(s)))
                .mapToDouble(s -> configurationSection.getDouble(s,0))
                .sum();
    }

    public String getRandomId() {
        double rng = Utils.RNG(0,getTotalChance()),currentThreshHold = 0;
        ConfigurationSection configurationSection = getorAddConfigurationSection("dark-auctions");
        String[] filtered = configurationSection.getKeys(false).stream()
                .filter(s -> !Utils.isNullorAir(SaveItemConfig.getInstance().loadItem(s)))
                .toArray(String[]::new);
        for (String key : filtered) {
            currentThreshHold += configurationSection.getDouble(key,0);
            if (rng <= currentThreshHold) {
                return key;
            }
        }
        return null;
    }

    public ItemStack getRandomItem() {
        ItemStack itemStack = SaveItemConfig.getInstance().loadItem(getRandomId());
        return itemStack != null ? itemStack.clone() : null;
    }

}
