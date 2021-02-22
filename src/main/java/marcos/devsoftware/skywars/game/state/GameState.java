package marcos.devsoftware.skywars.game.state;

import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@Getter @Setter
public abstract class GameState implements Listener {

    private Game game;

    public void onEnable(SkywarsPlugin skywarsPlugin) {
        Bukkit.getPluginManager().registerEvents(this, skywarsPlugin);
    }

    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}