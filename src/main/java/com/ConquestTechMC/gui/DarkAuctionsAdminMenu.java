package com.ConquestTechMC.gui;

import com.ConquestTechMC.Auctions.AuctionsHandler;
import com.ConquestTechMC.config.RewardsConfig;
import com.ConquestTechMC.config.SettingsConfig;
import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DarkAuctionsAdminMenu extends AbstractClickableGUI {

    iPage backPage;

    public DarkAuctionsAdminMenu(Player player,iPage backPage) {
        super(player, "Dark Auctions Admin Menu", 3, false);

        this.backPage = backPage;

        init();
    }

    void init() {

        registerDefaultButtons();

        registerPriority(new Button(11,
                guiClickEvent -> {

            if (AuctionsHandler.getInstance().isEventActive()) {
                getPlayer().sendMessage(Utils.color("&cThere is already an active auction"));
                return;
            }

            AuctionsHandler.getInstance().startActiveEvent(RewardsConfig
                    .getInstance().getRandomItem());

                },false));

        setupInventory();
    }

    @Override
    public int getGUIId() {
        return 9001;
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default",new Button(-1,guiClickEvent -> guiClickEvent
                .getPlayer().sendMessage(Utils.color("&cYou cannot click here!")),false));
        setDefaultAction("default");
    }

    @Override
    public void setupInventory() {

        DecorationUtils.border(getInventory(), Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);

        getInventory().setItem(11,new ItemBuilder(Material.GREEN_WALL_BANNER)
                .setDisplayName("&aForce Start")
                .setLore(DescriptionBuilder.init()
                        .addLore("&7&oClick to force start the dark auction").build()).build(false));
        getInventory().setItem(12,
                new ItemBuilder(Material.RED_BANNER)
                        .setDisplayName("&cForce Stop")
                        .setLore(DescriptionBuilder.init()
                                .addLore("&7&oClick to force stop the auction").build())
                        .build(false));

        update();

    }

    void update() {

        AuctionsHandler auctionsHandler = AuctionsHandler.getInstance();
        ItemStack itemStack = auctionsHandler.getAuction().getItemStack(true);


        if (Utils.isNullorAir(itemStack) || !auctionsHandler.isEventActive()) {
            itemStack =  new ItemBuilder(Material.BARRIER)
                    .setDisplayName("&cNo Active Auction")
                    .build(false);
        }

        getInventory().setItem(14,itemStack);

        getInventory().setItem(15,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .setDisplayName("&eAuction Amount: "+(
                                auctionsHandler.isEventActive() ? auctionsHandler
                                .getAuction().getAmount() : "&cN/A"))
                        .setLore(DescriptionBuilder.init()
                                .addLore("&7&oClick to change auction amount").build())
                        .build(false));

    }

    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {

    }
}
