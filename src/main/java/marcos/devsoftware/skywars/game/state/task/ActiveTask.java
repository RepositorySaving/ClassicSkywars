package marcos.devsoftware.skywars.game.state.task;

import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.game.state.EndState;
import marcos.devsoftware.skywars.player.GamePlayer;
import marcos.devsoftware.skywars.utility.Utility;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ActiveTask extends BukkitRunnable {

    private final Game game;
    private boolean alreadyPlaying;

    public ActiveTask(Game game) {
        this.game = game;
        this.alreadyPlaying = false;

        game.getController().assignCagePositions();
        game.getActivePlayers().forEach(GamePlayer::activeGeneralSettings);

        //todo sistema de kit aq

        game.getChestPoints().forEach(chestPoint -> {
            Chest chest = (Chest) chestPoint.getWorld().getBlockAt(chestPoint).getState();

            ItemStack sword = new ItemStack(Material.STONE_SWORD, 1);
            ItemStack stone = new ItemStack(Material.STONE, 32);
            ItemStack wood = new ItemStack(Material.WOOD, 32);
            ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE, 1);
            ItemStack iron = new ItemStack(Material.IRON_INGOT, 32);
            ItemStack food = new ItemStack(Material.COOKED_BEEF, 10);

            chest.getInventory().addItem(sword, stone, wood, chestplate, iron, food);
        });
    }

    @Override
    public void run() {
        game.getController().getBoardManager().getBoards().forEach((player, board) -> game.getController().getBoardManager().updateScore(player));

        long actualTime;
        if (alreadyPlaying) {
            actualTime = game.getController().getTimeUntilGameEnds();
            if (actualTime == 0) {
                EndState endState = new EndState();
                endState.setDraw(true);

                game.getController().setState(endState);
                return;
            }

            game.getController().setTimeUntilGameEnds(actualTime - 1000);
        } else {
            actualTime = game.getController().getPrepareTime();
            if (actualTime == 0) {
                alreadyPlaying = true;
                game.getActivePlayers().forEach(gamePlayer -> gamePlayer.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.AIR));
                return;
            }

            game.getActivePlayers().forEach(gamePlayer -> Utility.sendTitle(gamePlayer.getPlayer(), "&c" + Utility.convertTime(actualTime, true), null, 10, 20, 10));
            game.getController().setPrepareTime(actualTime - 1000);
        }
    }
}