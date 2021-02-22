package marcos.devsoftware.skywars.game;

import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationFile;
import marcos.devsoftware.skywars.configuration.ConfigurationUtility;
import marcos.devsoftware.skywars.game.state.GameState;
import marcos.devsoftware.skywars.game.state.WaitingState;
import marcos.devsoftware.skywars.player.GamePlayer;
import marcos.devsoftware.skywars.utility.Utility;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class Game {

    private final GameController controller;
    private final GameWorld worldManager;

    private final ConfigurationFile gameFile;
    private final String mapName;
    private final int maxPlayers;
    private final int minPlayers;
    private final Location lobbyPoint;
    private final List<Location> spawnPoints;
    private final List<Location> chestPoints;
    private final List<GamePlayer> players;
    private final List<GamePlayer> spectators;
    private GameState state;

    public Game(ConfigurationFile gameFile, SkywarsPlugin skywarsPlugin) {
        this.controller = new GameController(this, skywarsPlugin);

        this.gameFile = gameFile;
        YamlConfiguration gameConfiguration = gameFile.getConfiguration();

        this.mapName = gameConfiguration.getString("mapName");
        this.maxPlayers = gameConfiguration.getInt("maxPlayers");
        this.minPlayers = gameConfiguration.getInt("minPlayers");
        this.worldManager = new GameWorld(this);
        this.lobbyPoint = ConfigurationUtility.readLocation(worldManager.getWorld(), gameConfiguration.getConfigurationSection("lobbyPoint"));

        this.spawnPoints = new ArrayList<>();
        for (String spawnPoint : gameConfiguration.getStringList("spawnPoints")) {
            spawnPoints.add(Utility.deserializeLocation(worldManager.getWorld(), spawnPoint));
        }

        this.chestPoints = new ArrayList<>();
        for (String chestPoint : gameConfiguration.getStringList("chestPoints")) {
            chestPoints.add(Utility.deserializeLocation(worldManager.getWorld(), chestPoint));
        }

        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();

        controller.setState(new WaitingState());
    }

    public Set<GamePlayer> getAllPlayers() {
        Set<GamePlayer> allPlayers = new HashSet<>();
        allPlayers.addAll(getActivePlayers());
        allPlayers.addAll(getActiveSpectators());

        return allPlayers;
    }

    public List<GamePlayer> getActivePlayers() {
        return players.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<GamePlayer> getActiveSpectators() {
        return spectators.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}