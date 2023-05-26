package com.ConquestTechMC.EventListeners;

import com.ConquestTechMC.Auctions.AuctionsHandler;
import com.ConquestTechMC.gui.DarkAuctionsMenu;
import com.MenuAPI.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AuctionListener implements Listener {


    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        AuctionsHandler auctionsHandler = AuctionsHandler.getInstance();
        auctionsHandler.getAuction().getPlayerOfferEntryMap().remove(playerQuitEvent.getPlayer());
        if (auctionsHandler.getAuction().highestBid.getPlayer().equals(playerQuitEvent
                .getPlayer())) {
            auctionsHandler.getAuction().updateHighestBid();
        }
        auctionsHandler.auctionUpdateSendUpdateToGUI();
    }

    @EventHandler
    public void onAuctionOpen(PlayerInteractEntityEvent playerInteractEntityEvent) {
        Entity entity = playerInteractEntityEvent.getRightClicked();
        AuctionsHandler.Auction auction = AuctionsHandler.getInstance().getAuction();
        if (entity.equals(auction.getArmorStand()) || entity.equals(auction.getItem())) {
            new DarkAuctionsMenu(playerInteractEntityEvent.getPlayer(),
                    null).open(playerInteractEntityEvent.getPlayer());
        }
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        if (AuctionsHandler.getInstance().isEventActive()) playerJoinEvent
                .getPlayer().sendMessage(Utils.color("&eThere is a current dark auction currently going on! &a/auction join &eto join this dark auction"));
    }

}
