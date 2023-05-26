package com.ConquestTechMC.gui;

import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utils;
import org.bukkit.entity.Player;

public class ItemSelectorPrompt extends AbstractClickableGUI {

    iPage back;

    public ItemSelectorPrompt(Player player, iPage page) {
        super(player, "Choose an item for the force start of dark auction", 3, false);

        this.back = page;

        init();
    }

    void init() {
        registerDefaultButtons();

        setupInventory();
    }

    @Override
    public int getGUIId() {
        return 9003;
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default",new Button(-1, guiClickEvent -> guiClickEvent
                .getPlayer().sendMessage(Utils.color("&cYou cannot click here!")),false));
        setDefaultAction("default");
    }

    @Override
    public void setupInventory() {

    }

    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {

    }
}
