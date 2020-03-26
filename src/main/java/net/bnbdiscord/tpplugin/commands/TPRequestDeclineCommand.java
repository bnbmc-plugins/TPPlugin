package net.bnbdiscord.tpplugin.commands;

import net.bnbdiscord.tpplugin.TPPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class TPRequestDeclineCommand implements CommandExecutor {
    TPPlugin plugin;

    public TPRequestDeclineCommand(TPPlugin p) {
        plugin = p;
    }
    
    String[] errors = {
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
            Player player = (Player) commandSender;
            if (plugin.TeleportRequests.containsKey(player.getUniqueId().toString())) {
                ArrayList<String> reqs = plugin.TeleportRequests.get(player.getUniqueId().toString());
                if (reqs.size() > 1) {
                    if (args.length == 0) {

                        player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "PROBLEM! " + ChatColor.YELLOW + "You have multiple teleport requests:");
                        ArrayList<String> reqNames = new ArrayList<>();
                        for(String r : reqs){
                            reqNames.add(Objects.requireNonNull(plugin.getServer().getPlayer(UUID.fromString(r))).getName());
                        }
                        player.sendMessage(ChatColor.YELLOW + String.join(", ", reqNames));
                        player.sendMessage(ChatColor.YELLOW + "Therefore, you must specify someone to decline.");
                        return true;

                    } else if (args.length > 1) return false;
                    else {
                        Player teleportingPlayer = plugin.getServer().getPlayer(args[0]);

                        if (teleportingPlayer == null) {
                            player.sendMessage(errors[new Random().nextInt(errors.length)] + "That player has not sent you a teleport request!");
                            return true;
                        }

                        if(!reqs.contains(teleportingPlayer.getUniqueId().toString())){
                            player.sendMessage(ChatColor.RED + "Player has not requested a teleport.");
                            return true;
                        }

                        plugin.removeRequest(player.getUniqueId().toString(), teleportingPlayer.getUniqueId().toString());
                        teleportingPlayer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "DECLINED! " + ChatColor.RED + "Your teleport request to " + player.getName() + " has been declined :(");
                        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "DECLINED! " + ChatColor.GREEN + "Successfully declined " + teleportingPlayer.getName + "'s teleport request!");
                        return true;
                    }

                } else {
                    String req = reqs.get(0);
                    if(args.length != 0){
                        player.sendMessage(errors[new Random().nextInt(errors.length)] + "You don't have multiple teleport requests! Enter the command without any arguments.");
                        return true;
                    }
                    Player teleportingPlayer = plugin.getServer().getPlayer(UUID.fromString(req));
                    if(teleportingPlayer == null){
                        player.sendMessage(errors[new Random().nextInt(errors.length)] + "That player isn't online anymore!");
                        return true;
                    }
                    plugin.removeRequest(player.getUniqueId().toString(), teleportingPlayer.getUniqueId().toString());
                    teleportingPlayer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "DECLINED! " + ChatColor.RED + "Your teleport request to " + player.getName() + " has been declined :(");
                    player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "DECLINED! " + ChatColor.GREEN + "Successfully declined " + teleportingPlayer.getName + "'s teleport request!");
                    return true;

                }
            } else {

                player.sendMessage(errors[new Random().nextInt(errors.length)] + "You don't have any pending teleport requests!");
                return true;

            }
        }
        return false;
    }
}
