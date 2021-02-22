package marcos.devsoftware.skywars.game.state;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.event.JoinGameEvent;
import marcos.devsoftware.skywars.event.LeaveGameEvent;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.game.state.task.ActiveTask;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ActiveState extends GameState {

    private ActiveTask task;

    @Override
    public void onEnable(SkywarsPlugin skywarsPlugin) {
        super.onEnable(skywarsPlugin);

        this.task = new ActiveTask(getGame());
        this.task.runTaskTimer(skywarsPlugin, 0, 20);

        getGame().getController().sendMessage("&aBoa sorte a todos os jogadores!");
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
    }

    @EventHandler
    private void onLeaveGame(LeaveGameEvent event) {
        if (!getGame().getController().inMatch(event.getPlayer())) return;

        event.getGame().getController().removePlayer(event.getPlayer(), true);

        if (getGame().getPlayers().size() == 1) {
            getGame().getController().setState(new EndState());
        }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (!getGame().getController().inMatch((Player) event.getEntity())) return;
        Player player = ((Player) event.getEntity());

        if (getGame().getController().isPlayer(player)) {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                getGame().getController().getKillController().killPlayer(player);

                event.setCancelled(true);
            }
        } else {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.getEntity().teleport(getGame().getLobbyPoint());
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!getGame().getController().inMatch((Player) event.getEntity())) return;
        Player player = (Player) event.getEntity();

        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            if (!getGame().getController().inMatch(damager)) return;

            if (getGame().getController().isSpectator(damager)) {
                event.setCancelled(true);
                return;
            }

            getGame().getController().getKillController().getPlayerKillCacheMap().put(player.getUniqueId(), damager.getUniqueId());
        } else {
            if (!(event.getDamager() instanceof Projectile)) return;
            Projectile projectile = (Projectile) event.getDamager();

            Player damager = (Player) projectile.getShooter();
            if (damager.equals(player)) return;

            getGame().getController().getKillController().getPlayerKillCacheMap().put(player.getUniqueId(), damager.getUniqueId());

            if (projectile.getName().equalsIgnoreCase("arrow")) {
                damager.playSound(damager.getLocation(), Sound.LEVEL_UP, 2.0F, 2.0F);
            }
        }

        if (event.getDamage() >= player.getHealth()) {
            getGame().getController().getKillController().killPlayer(player);

            if (getGame().getPlayers().size() == 1) {
                getGame().getController().setState(new EndState());
            }
        }
    }

    @EventHandler
    private void onFood(FoodLevelChangeEvent event) {
        if (!getGame().getController().inMatch((Player) event.getEntity())) return;

        if (getGame().getController().isSpectator((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!getGame().getController().inMatch(event.getPlayer())) return;

        if (getGame().getController().isSpectator(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {
        if (!getGame().getController().inMatch(event.getPlayer())) return;

        if (getGame().getController().isSpectator(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPickupItem(PlayerPickupItemEvent event) {
        if (!getGame().getController().inMatch(event.getPlayer())) return;

        if (getGame().getController().isSpectator(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}