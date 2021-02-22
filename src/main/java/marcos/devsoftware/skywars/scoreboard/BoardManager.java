package marcos.devsoftware.skywars.scoreboard;

import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.markboard.object.MPlayerBoard;
import marcos.devsoftware.markboard.object.MarkBoard;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BoardManager {

    private final Game game;
    private final SkywarsPlugin skywarsPlugin;

    private final Map<Player, MPlayerBoard> boards;

    public BoardManager(Game game, SkywarsPlugin skywarsPlugin) {
        this.game = game;
        this.skywarsPlugin = skywarsPlugin;

        this.boards = new HashMap<>();
    }

    public MPlayerBoard createBoard(Player player) {
        if (boards.containsKey(player)) return boards.get(player);
        MPlayerBoard board = MarkBoard.instance().createBoard(player, "&3&lSky&6&lCraft");

        PlayerTeam aliveTeam = new PlayerTeam(board.getScoreboard(), "alive");
        aliveTeam.setProperties(true, true, "&7");

        PlayerTeam deadTeam = new PlayerTeam(board.getScoreboard(), "dead");
        deadTeam.setProperties(true, false, "&8");

        boards.put(player, board);
        return board;
    }

    public void setExactlyTeams(Player player, boolean isPlayer) {
        boards.forEach((target, targetBoard) -> targetBoard.getScoreboard().getTeams().forEach(team -> team.removePlayer(player)));

        MPlayerBoard board = createBoard(player);
        Scoreboard scoreboard = board.getScoreboard();

        if (isPlayer) {
            boards.forEach((target, targetBoard) -> {
                scoreboard.getTeam("alive").addPlayer(target);
                targetBoard.getScoreboard().getTeam("alive").addPlayer(player);

                Team aliveTeam = targetBoard.getScoreboard().getTeam("alive");
                Team deadTeam = targetBoard.getScoreboard().getTeam("dead");

                game.getController().showPlayer(player, deadTeam);
                game.getController().showPlayer(player, aliveTeam);
            });
        } else {
            boards.forEach((target, targetBoard) -> {
                scoreboard.getTeam("dead").addPlayer(target);
                targetBoard.getScoreboard().getTeam("dead").addPlayer(player);

                Team aliveTeam = targetBoard.getScoreboard().getTeam("alive");
                Team deadTeam = targetBoard.getScoreboard().getTeam("dead");

                game.getController().showPlayer(player, deadTeam);
                game.getController().hidePlayer(player, aliveTeam);
            });
        }
    }

    public void remove(Player player) {
        if (boards.containsKey(player)) {
            boards.get(player).delete();
            boards.remove(player);
        }
    }

    public void updateScore(Player player) {
        MPlayerBoard board = boards.get(player);
        String gameState = game.getState().getName();

        if (gameState.equalsIgnoreCase("WaitingState")) {
            board.setAll(
                    "&a",
                    "&bMapa&7:",
                    "&f" + game.getMapName(),
                    "&b",
                    "&bJogadores&7:",
                    "&f" + game.getPlayers().size() + "/" + game.getMaxPlayers(),
                    "&c",
                    "&bIniciando em&7:",
                    "&f--:--",
                    "&d"
            );
        } else if (gameState.equalsIgnoreCase("StartingState")) {
            board.setAll(
                    "&a",
                    "&bMapa&7:",
                    "&f" + game.getMapName(),
                    "&b",
                    "&bJogadores&7:",
                    "&f" + game.getPlayers().size() + "/" + game.getMaxPlayers(),
                    "&c",
                    "&bIniciando em&7:",
                    "&f" + Utility.convertTime(game.getController().getTimeUntilStartMatch(), false),
                    "&d"
            );

        } else if (gameState.equalsIgnoreCase("ActiveState")) {
            String type;
            String actualTime;
            if (game.getController().getPrepareTime() > 0) {
                type = "&bPreparando...";
                actualTime = Utility.convertTime(game.getController().getPrepareTime(), false);
            } else {
                type = "&bTempo restante&7:";
                actualTime = Utility.convertTime(game.getController().getTimeUntilGameEnds(), false);
            }

            board.setAll(
                    "&a",
                    "&bMapa&7:",
                    "&f" + game.getMapName(),
                    "&b",
                    "&bJogadores&7:",
                    "&f" + game.getPlayers().size() + "/" + game.getMaxPlayers(),
                    "&c",
                    type,
                    "&f" + actualTime,
                    "&d"
            );
        }
    }
}