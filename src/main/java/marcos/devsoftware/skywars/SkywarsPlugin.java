package marcos.devsoftware.skywars;

import lombok.Getter;
import marcos.devsoftware.skywars.commands.SkywarsCommand;
import marcos.devsoftware.skywars.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SkywarsPlugin extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        super.onEnable();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            this.gameManager = new GameManager(this);

            getCommand("skywars").setExecutor(new SkywarsCommand(this));
        }, 100);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.gameManager != null) {
            this.gameManager.onDisable();
        }
    }
}