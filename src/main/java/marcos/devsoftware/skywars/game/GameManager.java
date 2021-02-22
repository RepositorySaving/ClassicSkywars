package marcos.devsoftware.skywars.game;

import lombok.Getter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationFile;
import marcos.devsoftware.skywars.configuration.ConfigurationManager;
import marcos.devsoftware.skywars.game.state.StartingState;
import marcos.devsoftware.skywars.game.state.WaitingState;
import marcos.devsoftware.skywars.listeners.AlwaysActiveListener;
import marcos.devsoftware.skywars.listeners.PerGameListener;
import marcos.devsoftware.skywars.player.GamePlayer;
import marcos.devsoftware.skywars.utility.WorldUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public class GameManager {

    private final SkywarsPlugin skywarsPlugin;
    private final ConfigurationManager configurationManager;

    private final List<Game> games;

    private final AlwaysActiveListener alwaysActiveListener;
    private final PerGameListener perGameListener;

    public GameManager(SkywarsPlugin skywarsPlugin) {
        this.skywarsPlugin = skywarsPlugin;
        this.configurationManager = new ConfigurationManager(this);

        this.games = new ArrayList<>();

        for (File file : Objects.requireNonNull(configurationManager.getGamesFolder().listFiles())) {
            ConfigurationFile gameFile = new ConfigurationFile(file.getName(), configurationManager.getGamesFolder());
            Game game = new Game(gameFile, skywarsPlugin);

            games.add(game);
        }

        this.alwaysActiveListener = new AlwaysActiveListener();
        this.perGameListener = new PerGameListener(this);

        Bukkit.getPluginManager().registerEvents(alwaysActiveListener, skywarsPlugin);
        Bukkit.getPluginManager().registerEvents(perGameListener, skywarsPlugin);
    }

    public void onDisable() {
        for (Game game : games) {
            for (GamePlayer gamePlayer : game.getAllPlayers()) {
                if (gamePlayer.getPlayer() == null) continue;

                game.getController().removePlayer(gamePlayer.getPlayer(), true);
                gamePlayer.getPlayer().teleport(new Location(Bukkit.getWorld("world"), -104.0, 40.0, 1249.0));
            }

            WorldUtility.deleteWorld(game.getWorldManager().getWorld());
        }
    }

    public boolean inAnyMatch(Player player) {
        return skywarsPlugin.getGameManager().findGameByPlayer(player) != null;
    }

    public Optional<Game> findOpenGame() {
        return games.stream().filter(game -> game.getController().isState(new WaitingState()) || game.getController().isState(new StartingState())).findAny();
    }

    public Game findGameByPlayer(Player player) {
        for (Game game : games) {
            if (game == null) continue;

            for (GamePlayer gamePlayer : game.getPlayers()) {
                if (gamePlayer.getPlayer() == player) {
                    return game;
                }
            }
        }

        return null;
    }

    public Optional<Game> findGameByWorld(World world) {
        return games.stream().filter(game -> game.getWorldManager().getWorld().getName().equalsIgnoreCase(world.getName())).findAny();
    }
}