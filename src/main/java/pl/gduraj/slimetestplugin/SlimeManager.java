package pl.gduraj.slimetestplugin;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Slime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SlimeManager {

    private SlimeTestPlugin plugin;
    private Map<String, SlimeWorld> worlds;
    private SlimePlugin slime;
    private SlimeLoader sloader;

    public SlimeManager() {
        this.plugin = SlimeTestPlugin.getInstance();
        this.slime = plugin.getSlimePlugin();
        this.sloader = plugin.getSqlLoader();
        this.worlds = new HashMap<>();
    }

    public void unloadAllWorlds() {
        synchronized (worlds){
            for(String s : worlds.keySet()){
                unloadWorld(s);
            }
        }
    }

    public void loadWorlds(){
        try {
            sloader.listWorlds().forEach(s -> {
                loadAndGetWorld(s);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SlimeWorld loadAndGetWorld(String worldName) {
        SlimeWorld world = worlds.get(worldName);
        System.out.println("test1");
        if (world == null) {
            System.out.println("test2");
            try {
                if(sloader.worldExists(worldName)){
                    if(sloader.isWorldLocked(worldName))
                        sloader.unlockWorld(worldName);

                    try {
                        world = slime.loadWorld(sloader, worldName, false, slimePropertyMap());
                    } catch (CorruptedWorldException | NewerFormatException | WorldInUseException |
                             UnknownWorldException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    try {
                        world = slime.createEmptyWorld(sloader, worldName, false, slimePropertyMap());
                    } catch (WorldAlreadyExistsException e) {
                        throw new RuntimeException(e);
                    }
                }

                worlds.put(worldName, world);
            } catch (IOException | UnknownWorldException e) {
                throw new RuntimeException(e);
            }
        }
        if (Bukkit.getWorld(worldName) == null) {
            generateWorld(world);
        }

        return world;
    }

    public Location teleportToWorld(String worldName) {
        SlimeWorld slimeWorld = loadAndGetWorld(worldName);
        World world = Bukkit.getWorld(slimeWorld.getName());

        return world.getSpawnLocation();
    }

    public void generateWorld(SlimeWorld slimeWorld){
        slime.generateWorld(slimeWorld);

        World world = Bukkit.getWorld(slimeWorld.getName());
        if(world != null){
            world.getWorldBorder().setCenter(0,0);
            world.getWorldBorder().setSize(1000);

        }

    }

    public void unloadWorld(String worldName) {
        SlimeWorld world = worlds.get(worldName);
        if (world != null) {
            try {
                //Bukkit.getWorld(worldName).save();
                if(Bukkit.unloadWorld(worldName, true))
                    System.out.println("Świat " + worldName + " zostal odładowany");
                else
                    System.out.println("ERROR");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    public void cloneWorld(String worldName){
        SlimeWorld slimeWorld = slime.getWorld("glenor");
        if(slimeWorld != null){
            try {
                SlimeWorld newWorld = slimeWorld.clone(worldName, sloader);
                System.out.println(newWorld);

                loadAndGetWorld(worldName);

            } catch (IOException | WorldAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteWorld(String worldName) {
        SlimeWorld world = worlds.get(worldName);
        try {
            unloadWorld(worldName);
            sloader.deleteWorld(worldName);
            worlds.remove(worldName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private SlimeWorld asyncLoadWorld(String worldName) {
        final SlimeWorld[] slimeWorlds = new SlimeWorld[1];
        slime.asyncLoadWorld(sloader, worldName, false, slimePropertyMap()).thenAccept(world -> {
            if (world.isEmpty()) {
                // Do something else
                throw new IllegalStateException("Failed to load world (" + worldName + ") on environment normal");
            }
            slimeWorlds[0] = world.get();
        });
        return slimeWorlds[0];
    }

    private SlimeWorld asyncCreateEmptyWorld(String worldName) {
        final SlimeWorld[] slimeWorlds = new SlimeWorld[1];
        slime.asyncCreateEmptyWorld(sloader, worldName, false, slimePropertyMap()).thenAcceptAsync(world -> {
            if (world.isEmpty()) {
                // Do something else
                throw new IllegalStateException("Failed to create an empty world (" + worldName + ") on environment normal");
            }
            slimeWorlds[0] = world.get();
        });
        return slimeWorlds[0];
    }

    public SlimePropertyMap slimePropertyMap() {
        SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
        slimePropertyMap.setValue(SlimeProperties.DIFFICULTY, "normal");
        slimePropertyMap.setValue(SlimeProperties.SPAWN_X, 0);
        slimePropertyMap.setValue(SlimeProperties.SPAWN_Y, 100);
        slimePropertyMap.setValue(SlimeProperties.SPAWN_Z, 0);
        return slimePropertyMap;
    }

    public Map<String, SlimeWorld> getWorlds() {
        return worlds;
    }
}
