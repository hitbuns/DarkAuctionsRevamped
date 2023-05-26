package com.ConquestTechMC.Events;

import com.ConquestTechMC.Auctions.AuctionsHandler;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AuctionEndEvent extends Event {

    public static HandlerList handlerList = new HandlerList();
    AuctionsHandler auctionsHandler;

    public AuctionEndEvent(AuctionsHandler auctionsHandler) {
        this.auctionsHandler = auctionsHandler;
    }


    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
