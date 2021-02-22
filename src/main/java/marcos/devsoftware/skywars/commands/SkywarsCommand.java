package marcos.devsoftware.skywars.commands;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SkywarsCommand implements CommandExecutor {

    private final SkywarsPlugin skywarsPlugin;

    public SkywarsCommand(SkywarsPlugin skywarsPlugin) {
        this.skywarsPlugin = skywarsPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        Optional<Game> gameOptional = skywarsPlugin.getGameManager().findOpenGame();
        if (!gameOptional.isPresent()) return true;

        Game game = gameOptional.get();
        game.getController().addPlayer(player);
        return false;
    }
}
