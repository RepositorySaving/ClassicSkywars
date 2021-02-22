package marcos.devsoftware.skywars.event;

import lombok.Getter;
import marcos.devsoftware.skywars.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class LeaveGameEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Game game;
    private final Player player;

    public LeaveGameEvent(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}