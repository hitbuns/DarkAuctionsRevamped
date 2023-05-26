package com.ConquestTechMC.Auctions;

import org.bukkit.entity.Player;

public class OfferEntry {

    private double amount;
    private final Player offlinePlayer;

    public OfferEntry(Player offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = Math.max(0,amount);
    }

    public Player getPlayer() {
        return offlinePlayer;
    }
}
