package com.ConquestTechMC.gui;

import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DarkAuctionsAdminMenu extends AbstractClickableGUI {

    iPage backPage;

    public DarkAuctionsAdminMenu(Player player,iPage backPage) {
        super(player, "Dark Auctions Admin Menu", 3, false);

        this.backPage = backPage;

        init();
    }

    void init() {

        registerDefaultButtons();



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



    }

    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {

    }
}
