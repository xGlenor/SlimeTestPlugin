package pl.gduraj.slimetestplugin;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public final class SlimeTestPlugin extends JavaPlugin {

    private static SlimeTestPlugin instance;
    private SlimePlugin slimePlugin;
    private SlimeManager slimeManager;
    private SlimeLoader sqlLoader;

    private SlimeCommand slimeCommand;

    @Override
    public void onEnable() {
        instance = this;
        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        sqlLoader = slimePlugin.getLoader("mysql");

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        this.slimeManager = new SlimeManager();
        slimeManager.loadWorlds();
        this.slimeCommand = new SlimeCommand();
        getServer().getPluginManager().registerEvents(new SlimeListener(), this);

    }

//    public void loadWorlds(){
//        List<String> worlds = getConfig().getStringList("worlds");
//        if(!worlds.isEmpty()){
//            for(String worldName : worlds){
//                getSlimeManager().loadAndGetWorld(worldName);
//            }
//        }
//    }

    @Override
    public void onDisable() {
        slimeManager.unloadAllWorlds();
    }

    public static SlimeTestPlugin getInstance() {
        return instance;
    }

    public SlimePlugin getSlimePlugin() {
        return slimePlugin;
    }

    public SlimeLoader getSqlLoader() {
        return sqlLoader;
    }

    public SlimeManager getSlimeManager() {
        return slimeManager;
    }

    /*    public void loadWorlds(){
        try {
            SlimeWorld world = slimePlugin.loadWorld(getSqlLoader(), worldName, false, slimePropertyMap);

            if(world != null)
                plugin.getSlimeWorldHashMap().put(worldName, world);

        } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException |
                 WorldInUseException exception){
            exception.printStackTrace();
        }
    }*/

}
