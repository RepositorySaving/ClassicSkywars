package marcos.devsoftware.markboard.object;

import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MarkBoard {

    private static MarkBoard instance;

    private final Map<Player, MPlayerBoard> boards = new HashMap<>();

    public MPlayerBoard createBoard(Player player, String name) {
        deleteBoard(player);

        MPlayerBoard board = new MPlayerBoard(player, MessageUtility.format(name));

        boards.put(player, board);
        return board;
    }

    public void deleteBoard(Player player) {
        if (hasBoard(player)) {
            getBoard(player).delete();
        }
    }

    public void removeBoard(Player player) {
        boards.remove(player);
    }

    public boolean hasBoard(Player player) {
        return boards.containsKey(player);
    }

    public MPlayerBoard getBoard(Player player) {
        return boards.get(player);
    }

    public Map<Player, MPlayerBoard> getBoards() {
        return boards;
    }

    public static MarkBoard instance() {
        if (instance == null) {
            instance = new MarkBoard();
        }

        return instance;
    }
}
