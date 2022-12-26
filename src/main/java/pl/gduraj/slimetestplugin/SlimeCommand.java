package pl.gduraj.slimetestplugin;

import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.world.SlimeWorld;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.UUID;

public class SlimeCommand implements CommandExecutor {

    private SlimeTestPlugin plugin;

    public SlimeCommand() {
        this.plugin = SlimeTestPlugin.getInstance();
        plugin.getCommand("st").setExecutor(this);
        plugin.getCommand("stt").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("st")){
            if(!(sender instanceof Player)){
                return true;
            }

            Player player = (Player) sender;
            String worldName = player.getName().toLowerCase();

            if(args.length > 0){
                if(args[0].equalsIgnoreCase("cr")){
                    SlimeWorld world = plugin.getSlimeManager().loadAndGetWorld(worldName);
                    return true;
                } else if (args[0].equalsIgnoreCase("tp")) {
                    player.teleport(plugin.getSlimeManager().teleportToWorld(worldName));
                    return true;
                } else if(args[0].equalsIgnoreCase("load")){
                    SlimeWorld world = plugin.getSlimeManager().loadAndGetWorld(worldName);
                    return true;
                } else if(args[0].equalsIgnoreCase("delete")){
                    plugin.getSlimeManager().deleteWorld(worldName);
                    return true;
                }else if(args[0].equalsIgnoreCase("info")){
                    System.out.println(plugin.getSlimeManager().getWorlds());
                    return true;
                } else if (args[0].equalsIgnoreCase("unload")) {
                    plugin.getSlimeManager().unloadWorld(worldName);
                    return true;
                }else if(args[0].equalsIgnoreCase("clone")){
                    if(args.length > 1){
                        String worldNameCustom = getWorldName(args);
                        plugin.getSlimeManager().cloneWorld(worldNameCustom);
                        return true;
                    }
                    return true;
                }
            }
        }else {
            if(args.length > 0){
                if(args[0].equalsIgnoreCase("test")){
                    UUID uuid = UUID.fromString(args[1]);
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

                    System.out.println(player.getPlayer().getLocation());



                }
            }
        }

        return true;
    }

    private String getWorldName(String[] args){
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append("-");
        }
        
        return builder.toString();
    }

}
