package de.acGames.chunkClaim.commands;

import de.acGames.chunkClaim.ChunkClaim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Claim implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) { commandSender.sendMessage(ChatColor.RED + "Executor has to be player!"); return false;};
        if (args.length < 2) { player.sendMessage(ChatColor.RED + "Usage: /chunkclaim <claim|remove> <playername>"); return false; }

        String subCommand = args[0].toLowerCase();
        String targetPlayerName = args[1];


        Chunk chunk = player.getLocation().getChunk();
        List<Integer> chunkKey = Arrays.asList(chunk.getX(), chunk.getZ());
        String dimension = getDimension(player);

        if (dimension == null) { player.sendMessage(ChatColor.RED + "This dimension is not supported."); return true; };

        switch (subCommand) {
            case "claim" -> {
                claimChunk(player, targetPlayerName, dimension, chunkKey);
            }
            case "remove" -> {
                removeChunkClaim(player, targetPlayerName, dimension, chunkKey);
            }
            default -> {
                player.sendMessage(ChatColor.RED + "Unknown subcommand. Use 'claim' or 'remove'.");
            }
        }

        return false;
    }

    private String getDimension(Player player) {
        World.Environment environment = player.getWorld().getEnvironment();
        return switch (environment) {
            case NORMAL -> "overworld";
            case NETHER -> "nether";
            case THE_END -> "end";
            default -> null;
        };
    }

    private void claimChunk(Player player, String targetPlayerName, String dimension, List<Integer> chunkKey) {
        // Update config
        List<List<Integer>> chunkList = ChunkClaim.plugin.configClass.getObject(dimension + "." + targetPlayerName);
        if (!chunkList.contains(chunkKey)) {
            chunkList.add(chunkKey);
        }
        ChunkClaim.plugin.configClass.setMethod(dimension + "." + targetPlayerName, chunkList);

        player.sendMessage(ChatColor.GREEN + "Chunk claimed for " + targetPlayerName + ".");
    }

    private void removeChunkClaim(Player player, String targetPlayerName, String dimension, List<Integer> chunkKey) {
        // Update config
        List<List<Integer>> chunkList = ChunkClaim.plugin.configClass.getObject(dimension + "." + targetPlayerName);
        if (chunkList.contains(chunkKey)) {
            chunkList.remove(chunkKey);
            if (chunkList.isEmpty()) {
                ChunkClaim.plugin.configClass.setMethod(dimension + "." + targetPlayerName, null);
            } else {
                ChunkClaim.plugin.configClass.setMethod(dimension + "." + targetPlayerName, chunkList);
            }

            player.sendMessage(ChatColor.YELLOW + "Chunk claim removed for " + targetPlayerName + ".");
        } else {
            player.sendMessage(ChatColor.RED + "The specified player does not own this chunk.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (args.length == 1 ) { list.add("claim"); list.add("remove"); };
        if (args.length == 2 ) {
            for ( Player player:Bukkit.getOnlinePlayers()) {
                list.add(player.getName());
            };
        };

        return list;
    }
}
