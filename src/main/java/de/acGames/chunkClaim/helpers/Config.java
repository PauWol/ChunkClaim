package de.acGames.chunkClaim.helpers;

import de.acGames.chunkClaim.ChunkClaim;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class Config {
    public FileConfiguration config = ChunkClaim.plugin.getConfig();

    public List<List<Integer>> getObject(String path) {
        return this.config.getObject(path,List.class, new ArrayList<>());
    }


    public void setMethod(String path,Object object) {
        this.config.set(path,object);
        ChunkClaim.plugin.saveConfig();
        this.config = ChunkClaim.plugin.getConfig();
        loadChunksFromConfig();
    }

    public void loadChunksFromConfig() {
        FileConfiguration config = ChunkClaim.plugin.configClass.config;

        for (String dimension : config.getKeys(false)) {
            World.Environment environment = switch (dimension.toLowerCase()) {
                case "overworld" -> World.Environment.NORMAL;
                case "nether" -> World.Environment.NETHER;
                case "end" -> World.Environment.THE_END;
                default -> {
                    ChunkClaim.plugin.getLogger().warning("Unknown dimension in config: " + dimension);
                    yield null;
                }
            };

            if (environment != null) {
                Map<List<Integer>, Set<String>> chunkOwners = new HashMap<>();

                for (String player : config.getConfigurationSection(dimension).getKeys(false)) {
                    List<List<Integer>> chunkList = (List<List<Integer>>) config.getList(dimension + "." + player);
                    if (chunkList != null) {
                        for (List<Integer> coords : chunkList) {
                            List<Integer> chunkKey = Arrays.asList(coords.get(0), coords.get(1));
                            chunkOwners.computeIfAbsent(chunkKey, k -> new HashSet<>()).add(player);
                        }
                    }
                }
                ChunkClaim.plugin.dimensionChunks.put(environment, chunkOwners);
            }
        }
    }
}
