package marcos.devsoftware.skywars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AlwaysActiveListener implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

    @EventHandler
    private void onChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (player.getWorld() == online.getWorld()) {
                player.showPlayer(online);
                online.showPlayer(player);
                continue;
            }
            
            player.hidePlayer(online);
            online.hidePlayer(player);
        }
    }
}
