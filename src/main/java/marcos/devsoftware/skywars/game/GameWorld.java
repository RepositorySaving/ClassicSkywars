package marcos.devsoftware.skywars.game;

import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.utility.WorldUtility;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter @Setter
public class GameWorld {

    private World world;

    public GameWorld(Game game) {
        YamlConfiguration configuration = game.getGameFile().getConfiguration();

        String originalWorldName = configuration.getString("worldName");
        String newWorldName = WorldUtility.nameGenerator(game.getMapName());

        this.world = WorldUtility.createWorld(Bukkit.getWorld(originalWorldName), newWorldName);
        configuration.set("temporaryWorldName", this.world.getName());

        game.getGameFile().save();
    }
}