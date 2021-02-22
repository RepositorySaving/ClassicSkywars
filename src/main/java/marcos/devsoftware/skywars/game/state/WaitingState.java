package marcos.devsoftware.skywars.game.state;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.event.JoinGameEvent;
import marcos.devsoftware.skywars.event.LeaveGameEvent;
import marcos.devsoftware.skywars.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitTask;

public class WaitingState extends GameState {

    private BukkitTask task;

    @Override
    public void onEnable(SkywarsPlugin skywarsPlugin) {
        super.onEnable(skywarsPlugin);

        this.task = Bukkit.getScheduler().runTaskTimer(skywarsPlugin, () -> getGame().getController().getBoardManager().getBoards().forEach((player, board) -> getGame().getController().getBoardManager().updateScore(player)), 0, 0);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.task != null) {
            this.task.cancel();
        }
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

        int requiredPlayers = game.getMinPlayers() - game.getPlayers().size();
        if (requiredPlayers > 0) {
            game.getController().sendMessage(String.format("&6%s &c%d &6%s para iniciar a partida", (requiredPlayers == 1 ? "Falta" : "Faltam"), requiredPlayers, (requiredPlayers == 1 ? "jogador" : "jogadores")));
        }

        if (game.getPlayers().size() == game.getMinPlayers()) {
            game.getController().setState(new StartingState());
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
        //todo fazer seleção de kits depois

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
}