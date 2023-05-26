package com.ConquestTechMC;

import com.ConquestTechMC.Commands.CmdAuction;
import com.ConquestTechMC.EventListeners.AuctionListener;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @EventHandler
    public void onEnable() {


        Instance = this;

        new Econ(this);

        registerCommands();
        registerListeners();


    }

    @Override
    public void onDisable() {
        Item item = CmdAuction.Instance.getAuctionsHandler().getAuction().getItem();
        if (item != null) item.remove();

        ArmorStand armorStand = CmdAuction.Instance.getAuctionsHandler().getAuction().getArmorStand();
        if (armorStand != null) armorStand.remove();
    }

    void registerCommands() {
        new CmdAuction(this);
    }

    void registerListeners() {
        getServer().getPluginManager().registerEvents(new AuctionListener(),this);
    }

    private static Main Instance;

    public static Main getInstance() {
        return Instance;
    }
}