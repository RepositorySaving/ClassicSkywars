package marcos.devsoftware.skywars.game;

import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationFile;
import marcos.devsoftware.skywars.game.state.GameState;
import marcos.devsoftware.skywars.player.GamePlayer;
import marcos.devsoftware.skywars.scoreboard.BoardManager;
import marcos.devsoftware.skywars.utility.MessageUtility;
import marcos.devsoftware.skywars.utility.Utility;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GameController {

    private final Game game;
    private final SkywarsPlugin skywarsPlugin;
    private final BoardManager boardManager;
    private final GameKillController killController;
    private final Map<Player, Location> playerCageMap;
    private long timeUntilStartMatch;
    private long prepareTime;
    private long timeUntilGameEnds;

    public GameController(Game game, SkywarsPlugin skywarsPlugin) {
        this.game = game;
        this.skywarsPlugin = skywarsPlugin;
        this.boardManager = new BoardManager(game, skywarsPlugin);
        this.killController = new GameKillController(game, skywarsPlugin);

        this.timeUntilStartMatch = 90000;
        this.prepareTime = 3000;
        this.timeUntilGameEnds = 900000;

        this.playerCageMap = new HashMap<>();
    }

    public void addPlayer(Player player) {
        GamePlayer gamePlayer = new GamePlayer(player);
        if (skywarsPlugin.getGameManager().inAnyMatch(player)) {
            MessageUtility.sendMessage(player, "&cVocê já está em partida.");
            return;
        }

        boardManager.createBoard(player);
        boardManager.setExactlyTeams(player, true);
        game.getPlayers().add(gamePlayer);
        gamePlayer.activePlayerSettings();

        sendMessage(String.format("&9[SkyWar] &6%s entrou na partida &a%d/%d", player.getName(), game.getPlayers().size(), game.getMaxPlayers()));

        player.teleport(game.getLobbyPoint());
    }

    public void removePlayer(Player player, boolean isQuit) {
        GamePlayer gamePlayer = new GamePlayer(player);
        game.getPlayers().removeIf(playing -> playing.getPlayer() == player);

        if (isQuit) {
            if (!isSpectator(player)) {
                sendMessage(String.format("&9[SkyWar] &6%s &7deixou a partida", player.getName()));
            }

            boardManager.remove(player);
            game.getSpectators().removeIf(playing -> playing.getPlayer() == player);
            gamePlayer.activeQuitSettings();
            return;
        }

        boardManager.createBoard(player);
        boardManager.setExactlyTeams(player, false);

        player.teleport(game.getLobbyPoint());

        game.getSpectators().add(gamePlayer);
        gamePlayer.activeSpectatorSettings();
    }

    public void hidePlayer(Player player, Team team) {
        EntityPlayer craftPlayer = ((CraftPlayer) player).getHandle();

        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, craftPlayer);
        team.getPlayers().forEach(teamPlayer -> {
            if (!player.equals(teamPlayer.getPlayer())) {
                EntityPlayer craftPlayerTeam = ((CraftPlayer) teamPlayer).getHandle();
                craftPlayerTeam.playerConnection.sendPacket(packet);
            }
        });
    }

    public void showPlayer(Player player, Team team) {
        EntityPlayer craftPlayer = ((CraftPlayer) player).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftPlayer);

        team.getPlayers().forEach(teamPlayer -> {
            EntityPlayer craftPlayerTeam = ((CraftPlayer) teamPlayer).getHandle();
            PacketPlayOutPlayerInfo teamPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftPlayerTeam);

            if (!player.equals(teamPlayer.getPlayer())) {
                craftPlayerTeam.playerConnection.sendPacket(packet);
                craftPlayer.playerConnection.sendPacket(teamPacket);
            }
        });
    }

    public void sendTitle(String title, String subTitle, int fadeIn, int displayTime, int fadeOut) {
        game.getAllPlayers().forEach(gamePlayer -> Utility.sendTitle(gamePlayer.getPlayer(), title, subTitle, fadeIn, displayTime, fadeOut));
    }

    public void sendMessage(String message) {
        game.getAllPlayers().forEach(gamePlayer -> MessageUtility.sendMessage(gamePlayer.getPlayer(), message));
    }

    public void assignCagePositions() {
        File cageFolder = skywarsPlugin.getGameManager().getConfigurationManager().getCagesFolder();
        ConfigurationFile cageFile = new ConfigurationFile("default", cageFolder);
        YamlConfiguration configuration = cageFile.getConfiguration();

        for (int i = 0; i < game.getActivePlayers().size(); i++) {
            Location spawnPoint = game.getSpawnPoints().get(i);
            GamePlayer gamePlayer = game.getActivePlayers().get(i);

            for (String point : configuration.getStringList("points")) {
                String[] pointArray = point.split(",");
                int x = Integer.parseInt(pointArray[0]);
                int y = Integer.parseInt(pointArray[1]);
                int z = Integer.parseInt(pointArray[2]);

                Material material = Material.getMaterial(pointArray[3]);
                Location location = spawnPoint.clone();

                location.add(x, y, z).getWorld().getBlockAt(location).setType(material);
            }

            gamePlayer.getPlayer().teleport(spawnPoint);
            playerCageMap.put(gamePlayer.getPlayer(), spawnPoint);
        }
    }

    public boolean inMatch(Player player) {
        return isPlayer(player) || isSpectator(player);
    }

    public boolean isPlayer(Player player) {
        for (GamePlayer gamePlayer : game.getActivePlayers()) {
            if (gamePlayer.getPlayer() == player) return true;
        }

        return false;
    }

    public boolean isSpectator(Player player) {
        for (GamePlayer gamePlayer : game.getActiveSpectators()) {
            if (gamePlayer.getPlayer() == player) return true;
        }

        return false;
    }

    public void setState(GameState state) {
        if (game.getState() != null) {
            if (isState(state)) return;

            game.getState().onDisable();
        }

        game.setState(state);
        game.getState().setGame(game);
        game.getState().onEnable(skywarsPlugin);
    }

    public boolean isState(GameState state) {
        return game.getState().getClass() == state.getClass();
    }
}