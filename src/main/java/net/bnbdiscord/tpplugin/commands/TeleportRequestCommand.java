package net.bnbdiscord.tpplugin.commands;

import net.bnbdiscord.tpplugin.TPPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Random;

public class TeleportRequestCommand implements CommandExecutor {
    private TPPlugin plugin;

    public TeleportRequestCommand(TPPlugin p) {
        plugin = p;
    }

    String[] plugin.ERRORS = {
            ChatColor.RED + "" + ChatColor.BOLD + "AW SHUCKS! " + ChatColor.RED,
            ChatColor.RED + "" + ChatColor.BOLD + "OOPS! " + ChatColor.RED,
            ChatColor.RED + "" + ChatColor.BOLD + "ERROR! " + ChatColor.RED,
            ChatColor.RED + "" + ChatColor.BOLD + "FAIL! " + ChatColor.RED,
            ChatColor.RED + "" + ChatColor.BOLD + "UH OH! " + ChatColor.RED,
            ChatColor.RED + "" + ChatColor.BOLD + "WHOOPS! " + ChatColor.RED,
            ChatColor.RED + "" + ChatColor.BOLD + "NOPE! " + ChatColor.RED
    };
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player playerSender = (Player) commandSender;
            if (args.length != 1) return false;
            Player playerReceiver = plugin.getServer().getPlayer(args[0]);
            if (playerReceiver == null) {
                playerSender.sendMessage(plugin.ERRORS[new Random().nextInt(plugin.ERRORS.length)] + "Can't find a user by that name!");
            } else {
                String receiverID = playerReceiver.getUniqueId().toString();
                String senderID = playerSender.getUniqueId().toString();
                if(receiverID.equals(senderID)){
                    playerSender.sendMessage(plugin.ERRORS[new Random().nextInt(plugin.ERRORS.length)] + "You can't send yourself a teleport request, silly!");
                    return true;
                }
                if (plugin.TeleportRequests.containsKey(receiverID)) {
                    if (plugin.TeleportRequests.get(receiverID).contains(senderID)) {
                        playerSender.sendMessage(plugin.ERRORS[new Random().nextInt(plugin.ERRORS.length)] + "You have already sent that player a teleport request!");
                        return true;
                    }
                } else {
                    plugin.TeleportRequests.put(receiverID, new ArrayList<>());
                }
                ArrayList<String> reqs = plugin.TeleportRequests.get(receiverID);
                reqs.add(senderID);
                plugin.TeleportRequests.replace(receiverID, reqs);

                playerSender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "SENT!" + ChatColor.GREEN + " You have sent a teleport request to " + playerReceiver.getName() + ". They have " +
                        plugin.getConfig().getInt("tpplugin-request-expiration") / 20 + " seconds to accept it.");
                playerReceiver.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "TPA RECIEVED!" + ChatColor.GREEN + " You have received a teleport request from " + playerSender.getName());
                playerReceiver.sendMessage(ChatColor.GREEN + "To accept this, type /tpaccept, otherwise, type /tpdeny. You have " +
                        plugin.getConfig().getInt("tpplugin-request-expiration") / 20 + " seconds to accept it.");
                BukkitScheduler scheduler = plugin.getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(plugin, () -> {
                    if (plugin.TeleportRequests.containsKey(receiverID)) {
                        ArrayList<String> req2s = plugin.TeleportRequests.get(receiverID);
                        if (req2s.contains(senderID)) {
                            playerReceiver.sendMessage(plugin.ERRORS[new Random().nextInt(plugin.ERRORS.length)] + "The teleport request from " + playerSender.getName() + " has expired!");
                            playerSender.sendMessage(plugin.ERRORS[new Random().nextInt(plugin.ERRORS.length)] + "The teleport request to " + playerReceiver.getName() + " has expired!");
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
