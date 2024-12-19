package de.acGames.chunkClaim.listeners;

import de.acGames.chunkClaim.ChunkClaim;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.*;

public class Protect  implements Listener {

    private boolean isInForeignChunk(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        World.Environment environment = player.getWorld().getEnvironment();
        int x = chunk.getX();
        int z = chunk.getZ();
        List<Integer> chunkKey = Arrays.asList(x, z);

        Map<List<Integer>, Set<String>> playerChunks = ChunkClaim.plugin.dimensionChunks.get(environment);
        if (playerChunks != null) {
            Set<String> owners = playerChunks.get(chunkKey);
            return owners != null && !owners.contains(player.getName());
        }
        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = player.getLocation().getChunk();
        World.Environment environment = player.getWorld().getEnvironment();

        int x = chunk.getX();
        int z = chunk.getZ();
        List<Integer> chunkKey = Arrays.asList(x, z);

        Map<List<Integer>, Set<String>> playerChunks = ChunkClaim.plugin.dimensionChunks.get(environment);
        if (playerChunks == null) {
            resetToSurvivalMode(player);
            return;
        }

        Set<String> owners = playerChunks.get(chunkKey);

        if (owners != null && !owners.isEmpty()) {
            boolean isOwner = owners.contains(player.getName());
            ChatColor color = isOwner ? ChatColor.GREEN : ChatColor.RED;
            String ownerList = String.join(" ", owners);

            player.sendActionBar(ChatColor.GRAY + "ChunkClaim " + color + ownerList);

            if (!isOwner && player.getGameMode() == GameMode.SURVIVAL) {
                if (!ChunkClaim.plugin.playerLastGameMode.containsKey(player.getUniqueId())) {
                    ChunkClaim.plugin.playerLastGameMode.put(player.getUniqueId(), GameMode.SURVIVAL);
                }
                player.setGameMode(GameMode.ADVENTURE);
            } else if (isOwner) {
                resetToSurvivalMode(player);
            }
        } else {
            player.sendActionBar(ChatColor.GRAY + "");
            resetToSurvivalMode(player);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.ADVENTURE && isInForeignChunk(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if (player.getGameMode() == GameMode.ADVENTURE && isInForeignChunk(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.ADVENTURE && isInForeignChunk(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.ADVENTURE && isInForeignChunk(player)) {
            event.setCancelled(true);
        }
    }

    private void resetToSurvivalMode(Player player) {
        if (ChunkClaim.plugin.playerLastGameMode.containsKey(player.getUniqueId())) {
            if (player.getGameMode() == GameMode.ADVENTURE) {
                player.setGameMode(ChunkClaim.plugin.playerLastGameMode.get(player.getUniqueId()));
            }
            ChunkClaim.plugin.playerLastGameMode.remove(player.getUniqueId());
        }
    }
}
