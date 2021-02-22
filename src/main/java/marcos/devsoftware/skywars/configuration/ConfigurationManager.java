package marcos.devsoftware.skywars.configuration;

import lombok.Getter;
import marcos.devsoftware.skywars.game.GameManager;

import java.io.File;

@Getter
public class ConfigurationManager {

    private final File cagesFolder;
    private final File gamesFolder;
    private final ConfigurationFile chestsFile;
    private final ConfigurationFile configurationFile;
    private final ConfigurationFile kitsFile;
    private final ConfigurationFile messagesFile;
    private final ConfigurationFile scoreboardFile;

    public ConfigurationManager(GameManager gameManager) {
        this.cagesFolder = new File(gameManager.getSkywarsPlugin().getDataFolder(), "cages");
        if (!cagesFolder.exists()) {
            cagesFolder.mkdirs();
        }

        this.gamesFolder = new File(gameManager.getSkywarsPlugin().getDataFolder(), "games");
        if (!gamesFolder.exists()) {
            gamesFolder.mkdirs();
        }

        this.kitsFile = new ConfigurationFile("kits", gameManager.getSkywarsPlugin().getDataFolder());
        this.configurationFile = new ConfigurationFile("configuration", gameManager.getSkywarsPlugin().getDataFolder());
        this.chestsFile = new ConfigurationFile("chests", gameManager.getSkywarsPlugin().getDataFolder());
        this.messagesFile = new ConfigurationFile("messages", gameManager.getSkywarsPlugin().getDataFolder());
        this.scoreboardFile = new ConfigurationFile("scoreboard", gameManager.getSkywarsPlugin().getDataFolder());
    }
}