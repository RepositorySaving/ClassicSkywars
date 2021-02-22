package marcos.devsoftware.markboard;

import marcos.devsoftware.markboard.object.MPlayerBoard;
import marcos.devsoftware.markboard.object.MarkBoard;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public final class MarkBoardPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        Map<Player, MPlayerBoard> boards = MarkBoard.instance().getBoards();
        if (boards == null || boards.isEmpty()) return;

        boards.forEach((player, someBoard) -> {
            MPlayerBoard board = MarkBoard.instance().getBoard(player);
            if (player == null || board == null) return;

            board.delete();
        });
    }
}