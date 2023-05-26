package com.ConquestTechMC.Commands;

import com.ConquestTechMC.Auctions.AuctionsHandler;
import com.MenuAPI.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CmdAuction implements CommandExecutor, TabCompleter {

    AuctionsHandler auctionsHandler;
    public static CmdAuction Instance;

    public AuctionsHandler getAuctionsHandler() {
        return auctionsHandler;
    }

    public CmdAuction(JavaPlugin javaPlugin) {
        Instance = this;
        javaPlugin.getCommand("auction").setExecutor(this);
        auctionsHandler = new AuctionsHandler(javaPlugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(Utils.color("&cNo permission."));
            return true;
        }

        if (strings.length < 1) {
            commandSender.sendMessage(Utils.color("&cUsage: /"+command
                    .getName()+" join"));
            return true;
        }

        if (strings[0].equalsIgnoreCase("join")) {
            if (auctionsHandler.isEventActive()) {
                player.teleport(auctionsHandler.getAuction().getSpawnLocation());
            } else player.sendMessage(Utils.color("&cThe dark auction is not available until another "
            +Utils.convertLongToDate(auctionsHandler.getSettingsConfig().getCooldownTimeUnit()
                    .toMillis(auctionsHandler.getSettingsConfig().getCooldownTimeSpan()) -
                    (System.currentTimeMillis()-auctionsHandler.getTimeStamp()))+". Please try again later."));
        } else commandSender.sendMessage(Utils.color("&cUsage: /"+command
                .getName()+" join"));

        return true;
    }


    List<String> list = Collections.singletonList("join");


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return strings.length == 1 ? list.stream().filter(s1 -> s1.toLowerCase()
                .startsWith(strings[0].toLowerCase())).collect(Collectors.toList()) : null;
    }
}
