package com.ConquestTechMC.Auctions;

import com.ConquestTechMC.Events.AuctionEndEvent;
import com.ConquestTechMC.Events.AuctionStartEvent;
import com.ConquestTechMC.config.SettingsConfig;
import com.MenuAPI.BukkitEventCaller;
import com.MenuAPI.Utils;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class AuctionsHandler implements Runnable{

    public static ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public static SettingsConfig getSettingsConfig() {
        return settingsConfig;
    }

    static ScheduledExecutorService scheduledExecutorService;
    static SettingsConfig settingsConfig;
    boolean eventActive = false;
    ScheduledFuture<?> scheduledFuture;
    Auction auction;

    public AuctionsHandler(JavaPlugin javaPlugin) {

        if (scheduledExecutorService == null)
            scheduledExecutorService = Executors.newScheduledThreadPool(2);

        if (settingsConfig == null)
            settingsConfig = new SettingsConfig(javaPlugin);

        startCooldownCycle(false);
    }

    void cancelCurrent() {
        if (scheduledFuture != null && !(scheduledFuture.isCancelled() ||
                scheduledFuture.isDone()))
            scheduledFuture.cancel(true);
    }

    void startActiveEvent() {
        cancelCurrent();
        scheduledFuture = scheduledExecutorService.schedule(()->startCooldownCycle(true),
                settingsConfig.getEventTimeSpan(),settingsConfig.getEventTimeUnit());

        if (BukkitEventCaller.callEvent(new AuctionStartEvent())) onAuctionStart();
        else {
            startCooldownCycle(false);
        }
    }

    void startCooldownCycle(boolean auctionEnd) {
        cancelCurrent();
        scheduledFuture = scheduledExecutorService.schedule(this,settingsConfig
                .getCooldownTimeSpan(),settingsConfig.getCooldownTimeUnit());

        if (auctionEnd) {
            BukkitEventCaller.callEvent(new AuctionEndEvent());
            onAuctionEnd();
        }
    }

    void onAuctionStart() {
        eventActive = true;
    }

    void onAuctionEnd() {
        eventActive = false;
    }

    public boolean isEventActive() {
        return eventActive;
    }

    @Override
    public void run() {
        startActiveEvent();
    }

    public static class Auction {

        private Location location;
        private final String id;
        private String displayName;

        public Auction(String id,String displayName,Location location) {
            this.location = location;
            this.id = id;
            this.displayName = Utils.color(displayName != null ? displayName :
                    "&e"+id);
        }

        public String getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Location getLocation() {
            return location;
        }
    }

}
