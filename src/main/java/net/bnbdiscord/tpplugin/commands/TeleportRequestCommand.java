package net.bnbdiscord.tpplugin.commands;

import net.bnbdiscord.tpplugin.TPPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public class TeleportRequestCommand implements CommandExecutor {
    private TPPlugin plugin;

    public TeleportRequestCommand(TPPlugin p) {
        plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player playerSender = (Player) commandSender;
            if (args.length != 1) return false;
            Player playerReceiver = plugin.getServer().getPlayer(args[0]);
            if (playerReceiver == null) {
                playerSender.sendMessage(ChatColor.RED + "No users match your query.");
            } else {
                String receiverID = playerReceiver.getUniqueId().toString();
                String senderID = playerSender.getUniqueId().toString();
                if(receiverID.equals(senderID)){
                    playerSender.sendMessage(ChatColor.RED + "You can't send yourself a teleport request!");
                    return true;
                }
                if (plugin.TeleportRequests.containsKey(receiverID)) {
                    if (plugin.TeleportRequests.get(receiverID).contains(senderID)) {
                        playerSender.sendMessage(ChatColor.RED + "You have already sent that user a teleport request!");
                        return true;
                    }
                } else {
                    plugin.TeleportRequests.put(receiverID, new ArrayList<>());
                }
                ArrayList<String> reqs = plugin.TeleportRequests.get(receiverID);
                reqs.add(senderID);
                plugin.TeleportRequests.replace(receiverID, reqs);
                playerSender.sendMessage(ChatColor.AQUA + "You have sent a teleport request to " + playerSender.getName() + ". They have " +
                        plugin.getConfig().getInt("tpplugin-request-expiration")/20 + "seconds to accept it.");
                playerReceiver.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "You have recieved a teleport request from " + playerSender.getName());
                playerReceiver.sendMessage(ChatColor.AQUA + "To accept this, run </tpaccept>, otherwise, run </tpdecline>. You have " +
                        plugin.getConfig().getInt("tpplugin-request-expiration")/20 + "seconds to accept it.");
                BukkitScheduler scheduler = plugin.getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(plugin, () -> {
                    if (plugin.TeleportRequests.containsKey(receiverID)) {
                        ArrayList<String> req2s = plugin.TeleportRequests.get(receiverID);
                        if (req2s.contains(senderID)) {
                            playerReceiver.sendMessage(ChatColor.RED + "The teleport request for " + playerSender.getName() + " has expired");
                            playerReceiver.sendMessage(ChatColor.RED + "The teleport request for " + playerSender.getName() + " has expired");
                            plugin.removeRequest(receiverID, senderID);
                        }
                    }
                }, plugin.getConfig().getInt("tpplugin-request-expiration"));
            }
            return true;
        }
        return false;
    }
}
