package com.ConquestTechMC.gui;

import com.ConquestTechMC.Auctions.AuctionsHandler;
import com.ConquestTechMC.Auctions.OfferEntry;
import com.ConquestTechMC.Econ;
import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.FormattedNumber;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utilities.impl.HeadUtils;
import com.MenuAPI.Utils;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DarkAuctionsMenu extends AbstractClickableGUI implements Runnable {

    iPage backPage;

    public DarkAuctionsMenu(Player player, iPage page) {
        super(player, "&7Dark Auctions", 6, false);

        this.backPage = page;

        init();

    }

    void init() {
        registerDefaultButtons();

        registerPriority(new Button(3,guiClickEvent -> nextPage(),false));
        registerPriority(new Button(5,guiClickEvent -> backPage(),false));

        setupInventory();
    }

    @Override
    public int getGUIId() {
        return 9002;
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default",new Button(-1,guiClickEvent -> {

            ItemStack itemStack = guiClickEvent.getCurrentItem();

            if (Utils.isNullorAir(itemStack)) return;

            NBTItem nbtItem = new NBTItem(itemStack);
            if (nbtItem.hasKey("clickValue") && nbtItem.getType("clickValue") == NBTType.NBTTagDouble) {

                double clickValue = nbtItem.getDouble("clickValue");

                Economy economy = Econ.getInstance().getEconomy();

                if (economy.getBalance(getPlayer()) >= clickValue) {
                    AuctionsHandler auctionsHandler = AuctionsHandler.getInstance();
                    OfferEntry offerEntry = auctionsHandler.getAuction().getorAdd(getPlayer());
                    offerEntry.setAmount(clickValue);
                    auctionsHandler.getAuction().highestBid = offerEntry;
                    auctionsHandler.auctionUpdateSendUpdateToGUI();

                    auctionsHandler.getAuction().setCountDown(
                            Math.max(auctionsHandler.getAuction().getCountDown(),
                                    auctionsHandler.getSettingsConfig()
                                            .getAuctionCooldown())
                    );

                }


            }

        },false));
        setDefaultAction("default");
    }

    @Override
    public void setupInventory() {

        getInventory().setItem(3,new ItemBuilder(HeadUtils.getItemHead("http://textures.minecraft.net/texture/98b5e9d5afac183f1f570c1b6ef5156c121c1efbd85527d8d79d0adeeb672485"))
                .setDisplayName("&cBack Page").build(false));
        getInventory().setItem(5,new ItemBuilder(HeadUtils.getItemHead("http://textures.minecraft.net/texture/f716ca39511a96720c3379e771963befe224b60ecced9e693495975eda81de72"))
                .setDisplayName("&aNext Page").build(false));


        openPage((int) Math.floor((AuctionsHandler.getInstance().getAuction().highestBid.getAmount()+100)/1800));


    }

    void updateItemHead() {
        AuctionsHandler auctionsHandler = AuctionsHandler.getInstance();
        getInventory().setItem(4,new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("&aHighest Bid - "+(auctionsHandler.getAuction().highestBid != null ?
                        "&6"+auctionsHandler.getAuction().highestBid.getPlayer() +": $"+FormattedNumber
                                .getInstance().getCommaFormattedNumber(auctionsHandler.getAuction().highestBid
                                        .getAmount(),2) : "&cN/A"))
                .setLore(DescriptionBuilder.init()
                        .addLore("&e6Your Bid: &a$"+FormattedNumber
                                .getInstance().getCommaFormattedNumber(auctionsHandler
                                        .getAuction().getorAdd(getPlayer())
                                        .getAmount(),2),"",
                                "&8&oTime Left: &7&o"+auctionsHandler
                                        .getAuction().getAmount()+"s").build())
                .skullMeta(auctionsHandler
                        .getAuction().highestBid.getPlayer()).build(false));
    }

    public void update(boolean clear) {
        if (clear) clearPage();

        bidPattern = page % 2 == 0 ? BidPattern.NORMAL : BidPattern.REVERSE;

        AuctionsHandler auctionsHandler = AuctionsHandler.getInstance();

        updateItemHead();

        int highest = auctionsHandler.getAuction().getPlayerOfferEntryMap()
                .values().stream().filter(offerEntry -> offerEntry != null && offerEntry
                        .getAmount() != 0 && offerEntry
                        .getAmount() >= 1800*page && offerEntry.getAmount() <= 1800*(page+1))
                .peek(offerEntry -> getInventory()
                        .setItem(bidPattern.slots[(int) Math.floor(offerEntry
                                .getAmount()/100)],new ItemBuilder(Material.PLAYER_HEAD)
                                .setDisplayName("&6"+offerEntry.getPlayer().getName()+" - $"+
                                        FormattedNumber.getInstance()
                                                .getCommaFormattedNumber(offerEntry
                                                        .getAmount(),2))
                                .skullMeta(offerEntry.getPlayer())
                                .build(false))).map(offerEntry -> (int) Math.floor(offerEntry
                        .getAmount()/100)).max((o1, o2) -> Integer.compare(o2,o1))
                .orElse(-1);

        for (int i = 0; i < bidPattern.slots.length; i++) {
            try {

                int slot = bidPattern.slots[i];
                ItemStack itemStack = getInventory().getItem(slot);

                if (highest >= i &&
                        itemStack != null && itemStack.getType() == Material.PLAYER_HEAD) continue;

                if (highest >= i) getInventory().setItem(slot,
                        new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)
                                .setDisplayName("&aOffered - $"+
                                        FormattedNumber.getInstance()
                                                .getCommaFormattedNumber(page*1800+
                                                        i*100,2)).build(false));
                else {

                    double value = page*1800+i*100;
                    ItemStack itemStack1 = new ItemBuilder(HeadUtils
                            .getItemHead("http://textures.minecraft.net/texture/396ce13ff6155fdf3235d8d22174c5de4bf5512f1adeda1afa3fc28180f3f7"))
                            .setDisplayName("&eClick to Offer: &a$"+FormattedNumber
                                    .getInstance().getCommaFormattedNumber(value,2))
                            .build(false);

                    NBTItem nbtItem = new NBTItem(itemStack1);
                    nbtItem.setDouble("clickValue",value);

                    getInventory().setItem(slot,nbtItem.getItem());


                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }
    }


    BidPattern bidPattern;

    void clearPage() {
        ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < 6 *9; i++) {
            if (i == 3 || i == 5) continue;
            getInventory().setItem(i,itemStack.clone());
        }


    }

    int page = 0;

    void openPage(int page) {
        this.page = Math.max(0,page);
        update(true);
    }

    void nextPage() {
        openPage(this.page+1);
    }

    void backPage() {
        openPage(this.page-1);
    }



    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {

    }

    @Override
    public void run() {
        updateItemHead();
    }

    public enum BidPattern {

        NORMAL(18,19,28,37,46,47,48,49,40,31,22,23,24,33,42,51,52,53),
        REVERSE(45,46,37,28,19,20,21,22,31,40,49,50,51,42,33,24,25,26)
        ;

        BidPattern(int... slots) {
            this.slots = slots;
        }

        final int[] slots;

        public int[] getSlots() {
            return slots;
        }
    }

}
