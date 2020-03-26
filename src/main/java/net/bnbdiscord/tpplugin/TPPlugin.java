package net.bnbdiscord.tpplugin;

import net.bnbdiscord.tpplugin.commands.TPRequestAcceptCommand;
import net.bnbdiscord.tpplugin.commands.TPRequestDeclineCommand;
import net.bnbdiscord.tpplugin.commands.TeleportRequestCommand;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class TPPlugin extends JavaPlugin {
    public HashMap<String, ArrayList<String>> TeleportRequests = new HashMap<>(); //these string
    public final String[] ERRORS = {
            ChatColor.RED + "" + ChatColor.BOLD + "AW SHUCKS! " + ChatColor.RED,
            ChatColor.RED + "" + ChatColor.BOLD + "OOPS! " + ChatColor.RED,
            ChatColor.RED + "" + ChatColor.BOLD + "ERROR! " + ChatColor.RED,
            ChatColor.RED + "" + ChatColor.BOLD + "FAIL! " + ChatColor.RED,
            ChatColor.RED + "" + ChatColor.BOLD + "UH OH! " + ChatColor.RED,
            ChatColor.RED + "" + ChatColor.BOLD + "WHOOPS! " + ChatColor.RED,
            ChatColor.RED + "" + ChatColor.BOLD + "NOPE! " + ChatColor.RED
    };
    @Override
    public void onEnable() {
        getConfig().addDefault("tpplugin-request-expiration", 1200);
        getLogger().info("We're all endermen, and it's raining! (TPPlugin initialize)");
        Objects.requireNonNull(getCommand("tprequest")).setExecutor(new TeleportRequestCommand(this));
        Objects.requireNonNull(getCommand("tpaccept")).setExecutor(new TPRequestAcceptCommand(this));
        Objects.requireNonNull(getCommand("tpdecline")).setExecutor(new TPRequestDeclineCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("/toggledownfall (TPPlugin disable)");
    }


    public void removeRequest(String personid, String reqid){
        ArrayList<String> reqs = TeleportRequests.get(personid);
        reqs.remove(reqid);
        if(reqs.isEmpty()) TeleportRequests.remove(personid);
    }
}
