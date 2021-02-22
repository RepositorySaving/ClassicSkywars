package marcos.devsoftware.skywars.game.state;

import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.event.JoinGameEvent;
import marcos.devsoftware.skywars.event.LeaveGameEvent;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.player.GamePlayer;
import marcos.devsoftware.skywars.utility.WorldUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class EndState extends GameState {

    private boolean draw = false;

    @Override
    public void onEnable(SkywarsPlugin skywarsPlugin) {
        super.onEnable(skywarsPlugin);

        getGame().getActivePlayers().forEach(GamePlayer::activeSpectatorSettings);

        if (isDraw()) {
            getGame().getController().sendMessage("&6O tempo de jogo acabou, ninguém ganhou!");
        } else {
            Player winner = getGame().getActivePlayers().get(0).getPlayer();
            Map<UUID, Integer> top = getGame().getController().getKillController().getTop();

            UUID uuid = (UUID) top.keySet().toArray()[0];
            int kill = top.get(uuid);

            if (Bukkit.getPlayer(uuid) == null) {
                getGame().getController().sendMessage(String.format("&f1º: &a%s (%d kills)\n", Bukkit.getPlayer(uuid).getName(), kill));
            } else {
                getGame().getController().sendMessage(String.format("&f1º: &a%s (%d kills)\n", Bukkit.getOfflinePlayer(uuid).getName(), kill));
            }

            getGame().getController().sendTitle("&a" + winner.getName(), "&fvenceu a partida", 20, 40, 20);
            getGame().getController().sendMessage("&6" + winner.getName() + " &avenceu a partida!");
        }

        Bukkit.getScheduler().runTaskLater(skywarsPlugin, () -> gameReload(skywarsPlugin), 100);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    private void onJoinGame(JoinGameEvent event) {
        Player player = event.getPlayer();
        Game game = event.getGame();

        if (!player.getWorld().equals(getGame().getWorldManager().getWorld())) return;

        if (!game.getController().isPlayer(player)) {
            game.getController().removePlayer(player, false);
            return;
        }
    }

    @EventHandler
    private void onLeaveGame(LeaveGameEvent event) {
        if (!getGame().getController().inMatch(event.getPlayer())) return;

        event.getGame().getController().removePlayer(event.getPlayer(), true);
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (!getGame().getController().inMatch((Player) event.getEntity())) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.getEntity().teleport(getGame().getLobbyPoint());
        }

        event.setCancelled(true);
    }

    @EventHandler
    private void onFood(FoodLevelChangeEvent event) {
        if (!getGame().getController().inMatch((Player) event.getEntity())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!getGame().getController().inMatch(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        if (!getGame().getController().inMatch(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        if (!getGame().getController().inMatch(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {
        if (!getGame().getController().inMatch(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onPickupItem(PlayerPickupItemEvent event) {
        if (!getGame().getController().inMatch(event.getPlayer())) return;

        event.setCancelled(true);
    }

    private void gameReload(SkywarsPlugin skywarsPlugin) {
        onDisable();

        for (GamePlayer gamePlayer : getGame().getAllPlayers()) {
            getGame().getController().removePlayer(gamePlayer.getPlayer(), true);

            gamePlayer.getPlayer().teleport(new Location(Bukkit.getWorld("world"), -104.0, 40.0, 1249.0));
        }

        WorldUtility.deleteWorld(getGame().getWorldManager().getWorld());

        Game game = new Game(getGame().getGameFile(), skywarsPlugin);
        skywarsPlugin.getGameManager().getGames().removeIf(thisGame -> thisGame.getWorldManager().getWorld().equals(getGame().getWorldManager().getWorld()));
        skywarsPlugin.getGameManager().getGames().add(game);
    }
}