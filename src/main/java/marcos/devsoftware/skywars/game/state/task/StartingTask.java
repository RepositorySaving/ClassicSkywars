package marcos.devsoftware.skywars.game.state.task;

import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.game.state.ActiveState;
import marcos.devsoftware.skywars.utility.Utility;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class StartingTask extends BukkitRunnable {

    private final Game game;

    public StartingTask(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        game.getController().getBoardManager().getBoards().forEach((player, board) -> game.getController().getBoardManager().updateScore(player));

        long time = game.getController().getTimeUntilStartMatch();
        if (time == 1000) {
            game.getController().sendMessage("&9[SkyWar] &6Iniciando a partida...");
            game.getController().setState(new ActiveState());
            return;
        }

        int[] timerArray = new int[] { 60000, 30000, 15000, 10000, 5000, 4000, 3000, 2000 };
        boolean isTime = Arrays.stream(timerArray).anyMatch(x -> x == time);
        if (isTime) {
            game.getController().sendMessage(String.format("&9[SkyWar] &6Iniciando em &a%s &6segundos.", Utility.convertTime(time, true)));
        }

        game.getController().setTimeUntilStartMatch(time - 1000);
    }
}