package marcos.devsoftware.skywars.game.state;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.event.JoinGameEvent;
import marcos.devsoftware.skywars.event.LeaveGameEvent;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.game.state.task.StartingTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class StartingState extends GameState {

    private StartingTask task;

    @Override
    public void onEnable(SkywarsPlugin skywarsPlugin) {
        super.onEnable(skywarsPlugin);

        this.task = new StartingTask(getGame());
        this.task.runTaskTimer(skywarsPlugin, 0, 20);
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

        if (game.getPlayers().size() == game.getMaxPlayers() && game.getController().getTimeUntilStartMatch() > 10000) {
            game.getController().setTimeUntilStartMatch(10000);
        }
    }

    @EventHandler
    private void onLeaveGame(LeaveGameEvent event) {
        if (!getGame().getController().inMatch(event.getPlayer())) return;

        event.getGame().getController().removePlayer(event.getPlayer(), true);

        if (getGame().getPlayers().size() < getGame().getMinPlayers()) {
            getGame().getController().setState(new WaitingState());
            return;
        }
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