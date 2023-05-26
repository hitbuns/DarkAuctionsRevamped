package com.ConquestTechMC.Events;

import com.ConquestTechMC.Auctions.AuctionsHandler;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AuctionStartEvent extends Event implements Cancellable {

    private boolean cancel = false;
    public static HandlerList handlerList = new HandlerList();
    AuctionsHandler auctionsHandler;

    public AuctionsHandler getAuctionsHandler() {
        return auctionsHandler;
    }

    public AuctionStartEvent(AuctionsHandler auctionsHandler) {
        this.auctionsHandler = auctionsHandler;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        cancel = b;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
