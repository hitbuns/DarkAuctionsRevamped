package com.ConquestTechMC.Auctions;

import com.ConquestTechMC.Events.AuctionEndEvent;
import com.ConquestTechMC.Events.AuctionStartEvent;
import com.ConquestTechMC.Main;
import com.ConquestTechMC.config.RewardsConfig;
import com.ConquestTechMC.config.SettingsConfig;
import com.ConquestTechMC.gui.DarkAuctionsMenu;
import com.MenuAPI.BukkitEventCaller;
import com.MenuAPI.MenuAPI;
import com.MenuAPI.Utils;
import joptsimple.internal.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AuctionsHandler implements Runnable, Listener {

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public SettingsConfig getSettingsConfig() {
        return settingsConfig;
    }

    public Auction getAuction() {
        return auction;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    ScheduledExecutorService scheduledExecutorService= Executors.newScheduledThreadPool(2);;
    SettingsConfig settingsConfig;
    boolean eventActive = false;
    ScheduledFuture<?> scheduledFuture;
    Auction auction;
    RewardsConfig rewardsConfig;
    long timeStamp = System.currentTimeMillis();

    public static AuctionsHandler getInstance() {
        return Instance;
    }

    static AuctionsHandler Instance;

    public AuctionsHandler(JavaPlugin javaPlugin) {

        Instance = this;

        settingsConfig = new SettingsConfig(javaPlugin);
        rewardsConfig = new RewardsConfig(javaPlugin);

        javaPlugin.getServer().getPluginManager().registerEvents(this,javaPlugin);

        auction = new Auction("#1",settingsConfig.getString("settings.auction-name",
                "&cGrand Exchange"),settingsConfig.getEventLocation(),settingsConfig
                .getEventSpawnLocation());

        startCooldownCycle(false);
    }

    void cancelCurrent() {
        if (scheduledFuture != null && !(scheduledFuture.isCancelled() ||
                scheduledFuture.isDone()))
            scheduledFuture.cancel(true);
    }

    public void startActiveEvent(ItemStack itemStack) {
        cancelCurrent();

        if (BukkitEventCaller.callEvent(new AuctionStartEvent(this)) && !Utils.isNullorAir(itemStack)) onAuctionStart(itemStack.clone());
        else {
            startCooldownCycle(false);
        }
    }

    public void startCooldownCycle(boolean auctionEnd) {
        cancelCurrent();
        scheduledFuture = scheduledExecutorService.schedule(this,settingsConfig
                .getCooldownTimeSpan(),settingsConfig.getCooldownTimeUnit());
        timeStamp = System.currentTimeMillis();

        if (auctionEnd) {
            BukkitEventCaller.callEvent(new AuctionEndEvent(this));
            onAuctionEnd();
        }
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void auctionUpdateSendUpdateToGUI() {
        MenuAPI.getInstance().getGuiListener().getHandlerList().values().stream()
                .map(abstractGUI -> {
                    try {
                        return (DarkAuctionsMenu) abstractGUI;
                    } catch (Exception ignored) {
                        return null;
                    }
                }).filter(Objects::nonNull).forEach(darkAuctionsMenu -> darkAuctionsMenu.update(false));
    }

    void onAuctionStart(ItemStack itemStack) {
        eventActive = true;
        auction.setItemStack(itemStack);
        auction.setAmount(settingsConfig.getRandomAuctionItemAmount()-1);
        auction.countDown = settingsConfig.getAuctionStartCooldown();
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(()-> {

            auction.run();
            if (auction.countDown < 0) {

                onAuctionContinueCheck();

            }

                },1,1,
                TimeUnit.SECONDS);
    }

    void onAuctionContinueCheck() {

        auction.setAmount(auction.amount-1);
        auction.getPlayerOfferEntryMap().clear();
        auction.highestBid = null;
        if (auction.amount > 0) {
            auction.setItemStack(rewardsConfig.getRandomItem());
            auction.setCountDown(settingsConfig.getAuctionStartCooldown());
        } else startCooldownCycle(true);
    }

    void onAuctionEnd() {
        eventActive = false;
        auction.setItemStack(null);
        auction.setAmount(0);
        auction.setCountDown(0);
        auction.getPlayerOfferEntryMap().clear();
    }

    public boolean isEventActive() {
        return eventActive;
    }

    @Override
    public void run() {
        startActiveEvent(rewardsConfig.getRandomItem());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent entityDamageEvent) {
        Entity entity = entityDamageEvent.getEntity();
        if (entity.equals(auction.item) || entity.equals(auction.armorStand)) entityDamageEvent.setCancelled(true);
    }

    @EventHandler
    public void onVelocity(EntityPickupItemEvent entityPickupItemEvent) {
        if (entityPickupItemEvent.getItem().equals(auction.item))
            entityPickupItemEvent.setCancelled(true);
    }

    public static class Auction implements Runnable {

        private Location location,spawnLocation;
        private final String id;
        private String displayName;
        ItemStack itemStack;
        ArmorStand armorStand;
        int amount = 0;
        private final Map<Player,OfferEntry> playerOfferEntryMap = new HashMap<>();
        private int countDown;

        public Map<Player, OfferEntry> getPlayerOfferEntryMap() {
            return playerOfferEntryMap;
        }

        public OfferEntry getorAdd(Player player) {
            if (player == null) return null;
            OfferEntry offerEntry = getPlayerOfferEntryMap().get(player);
            if (offerEntry == null) getPlayerOfferEntryMap().put(player,
                    offerEntry = new OfferEntry(player));
            return offerEntry;
        }

        public OfferEntry highestBid;

        public void updateHighestBid() {
            highestBid = getPlayerOfferEntryMap().values().stream()
                    .filter(offerEntry -> offerEntry.getPlayer() != null && offerEntry.getPlayer()
                            .isOnline()).max((o1, o2) -> Double.compare(o2.getAmount(),
                            o1.getAmount())).orElse(null);
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }


        public int getCountDown() {
            return countDown;
        }

        public void setCountDown(int countDown) {
            this.countDown = countDown;
        }

        public ArmorStand getArmorStand() {
            return armorStand;
        }

        public Item getItem() {
            return item;
        }

        public ItemStack getItemStack(boolean clone) {
            return Utils.isNullorAir(itemStack) ? null : itemStack.clone();
        }

        public void setItemStack(ItemStack itemStack) {
            this.itemStack = Utils.isNullorAir(itemStack) ? null : itemStack.clone();

            if (this.itemStack != null) {

                if (item == null) {

                    item = (Item) location.getWorld().spawnEntity(location,EntityType.DROPPED_ITEM,false);
                    updateItem(itemStack);

                } else updateItem(itemStack);

            } else if (item != null) {
                item.remove();
                item = null;
            }

        }

        void updateItem(ItemStack itemStack) {
            if (item != null && !Utils.isNullorAir(itemStack)) {
                item.setItemStack(itemStack);
                ItemMeta itemMeta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : null;
                String s = itemMeta != null && itemMeta.hasDisplayName() && itemMeta.getDisplayName() != null ? itemMeta.getDisplayName() : "&e" +
                        Strings.join(Arrays.stream(itemStack
                                        .getType().name().split("_"))
                                .map(s1 -> s1.toUpperCase().charAt(0) + s1.toLowerCase().substring(1)).toArray(String[]::new), " ");
                item.setCustomNameVisible(true);
                item.setCustomName(Utils.color(s));
                item.setGravity(false);
                item.setPickupDelay(Integer.MAX_VALUE);
                item.setUnlimitedLifetime(true);
                item.setInvulnerable(true);
            }
        }

        Item item;

        public Auction(@NotNull String id,@NotNull Location location,@NotNull Location spawnLocation) {
            this(id,null,location,spawnLocation);
        }

        public Auction(@NotNull String id, String displayName, @NotNull Location location,
                       @NotNull Location spawnLocation) {
            this.location = location;
            this.location.setYaw(0);
            this.location.setPitch(0);

            armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND,false);

            armorStand.setCollidable(false);
            armorStand.setVisible(false);
            armorStand.setArms(false);
            EntityEquipment entityEquipment = armorStand.getEquipment();
            entityEquipment.setHelmet(new ItemStack(Material.GLASS),false);
            armorStand.setCustomNameVisible(false);
            armorStand.setGravity(false);
            armorStand.setInvulnerable(true);
            armorStand.setInvisible(true);

            this.id = id;
            this.displayName = Utils.color(displayName != null ? displayName :
                    "&e"+id);
            this.spawnLocation = spawnLocation;

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

        public Location getSpawnLocation() {
            return spawnLocation;
        }

        @Override
        public void run() {
            countDown--;
            Bukkit.getScheduler().runTask(Main.getInstance(),() -> MenuAPI.getInstance().getGuiListener().getHandlerList().values()
                    .stream().map(abstractGUI -> {
                        try {
                            return (DarkAuctionsMenu) abstractGUI;
                        } catch (Exception ignored) {
                            return null;
                        }
                    }).filter(Objects::nonNull).forEach(DarkAuctionsMenu::updateItemHead));
        }
    }

}
