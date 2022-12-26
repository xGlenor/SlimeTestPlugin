package pl.gduraj.slimetestplugin;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.tp.Teleportations;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SlimeListener implements Listener {

    private SlimeTestPlugin plugin;

    public SlimeListener() {
        plugin = SlimeTestPlugin.getInstance();
    }

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event){
        Player player = Bukkit.getPlayer(event.getUniqueId());
        if(player == null) return;
        System.out.println("ASYNC PLAYER JOIN ==========");
        System.out.println(player.getWorld().getName());
        System.out.println(player.getLocation().toString());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if(!event.getPlayer().hasPlayedBefore()) return;
        CMIUser user = CMI.getInstance().getPlayerManager().getUser(event.getPlayer());
        if(user != null){
            Location location = user.getLogOutLocation();

            if(location.getWorld() != null)
                user.teleportAsync(location, Teleportations.TeleportType.Unknown);
        }

        Player player = event.getPlayer();


        System.out.println("PLAYER JOIN ==========");
        System.out.println(player.getWorld().getName());
        System.out.println(player.getLocation().toString());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        System.out.println("PLAYER QUIT ==========");
        System.out.println(player.getWorld().getName());
        System.out.println(player.getLocation().toString());

    }

}
