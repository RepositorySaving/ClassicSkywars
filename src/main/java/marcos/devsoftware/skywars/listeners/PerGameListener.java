package marcos.devsoftware.skywars.listeners;

import marcos.devsoftware.skywars.event.JoinGameEvent;
import marcos.devsoftware.skywars.event.LeaveGameEvent;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class PerGameListener implements Listener {

    private final GameManager gameManager;

    public PerGameListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    private void onChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (gameManager.findGameByWorld(player.getWorld()).isPresent()) {
            Optional<Game> gameOptional = gameManager.findGameByWorld(player.getWorld());
            if (!gameOptional.isPresent()) return;

            Game game = gameOptional.get();
            Bukkit.getPluginManager().callEvent(new JoinGameEvent(game, player));
        } else if (gameManager.findGameByWorld(event.getFrom()).isPresent()) {
            Optional<Game> gameOptional = gameManager.findGameByWorld(event.getFrom());
            if (!gameOptional.isPresent()) return;

            Game game = gameOptional.get();
            Bukkit.getPluginManager().callEvent(new LeaveGameEvent(game, player));
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (gameManager.findGameByPlayer(player) == null) return;
        Game game = gameManager.findGameByPlayer(player);

        Bukkit.getPluginManager().callEvent(new LeaveGameEvent(game, player));
        event.setQuitMessage(null);
    }
}