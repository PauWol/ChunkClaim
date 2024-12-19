package de.acGames.chunkClaim;

import de.acGames.chunkClaim.commands.Claim;
import de.acGames.chunkClaim.helpers.Config;
import de.acGames.chunkClaim.listeners.Protect;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class ChunkClaim extends JavaPlugin {

    public static ChunkClaim plugin;
    public Config configClass;
    public final Map<UUID, GameMode> playerLastGameMode = new HashMap<>();
    public Map<World.Environment, Map<List<Integer>, Set<String>>> dimensionChunks = new HashMap<>();

    @Override
    public void onEnable() {

        getCommand("chunkclaim").setExecutor(new Claim());

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new Protect(),this);


        saveDefaultConfig();
        plugin = this;
        configClass = new Config();
        configClass.loadChunksFromConfig();

        getLogger().info("chunkclaim enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("chunkclaim disabled!");
    }
}
