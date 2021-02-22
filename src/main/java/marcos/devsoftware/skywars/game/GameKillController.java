package marcos.devsoftware.skywars.game;

import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Stream;

@Getter
@Setter
public class GameKillController {

    private final Game game;
    private final SkywarsPlugin skywarsPlugin;

    private final Map<UUID, Integer> playerKillMap;
    private final Map<UUID, UUID> playerKillCacheMap;
    private final Map<UUID, Integer> playerKillStreakMap;

    private Player firstKiller;

    public GameKillController(Game game, SkywarsPlugin skywarsPlugin) {
        this.game = game;
        this.skywarsPlugin = skywarsPlugin;

        this.playerKillMap = new HashMap<>();
        this.playerKillCacheMap = new HashMap<>();
        this.playerKillStreakMap = new HashMap<>();

        this.firstKiller = null;
    }

    public void killPlayer(Player player) {
        Player killer = Bukkit.getPlayer(playerKillCacheMap.get(player.getUniqueId()));
        if (killer == null) {
            game.getController().sendMessage("&6" + player.getName() + " &7morreu");
        } else {
            addKillCache(player, killer);

            game.getController().sendMessage("&6" + player.getName() + " &7foi morto por &c" + killer.getName());
        }

        game.getController().removePlayer(player, false);
    }

    public void addKillCache(Player player, Player killer) {
        playerKillMap.put(killer.getUniqueId(), playerKillMap.getOrDefault(killer.getUniqueId(), 0) + 1);
        playerKillCacheMap.put(player.getUniqueId(), killer.getUniqueId());
        playerKillStreakMap.put(killer.getUniqueId(), playerKillStreakMap.getOrDefault(killer.getUniqueId(), 0) + 1);

        int killStreak = playerKillStreakMap.get(killer.getUniqueId());
        if (killStreak >= 2) {
            String killStreakMessage = getKillType(killStreak);
            game.getController().sendMessage(String.format(killStreakMessage, killer.getName()));
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                playerKillCacheMap.remove(player.getUniqueId());
                playerKillStreakMap.put(killer.getUniqueId(), 0);
            }
        }.runTaskLater(skywarsPlugin, 20 * 10);
    }

    public String getKillType(int amount) {
        if (amount > 5) {
            return "&e%s &lestá enfurecido!";
        }

        Optional<GameKillType> killTypeString = Arrays.stream(GameKillType.values()).filter(killType -> killType.getAmount() == amount).findAny();
        return killTypeString.map(GameKillType::getName).orElse(null);
    }

    public Map<UUID, Integer> getTop() {
        Stream<Map.Entry<UUID, Integer>> sorted = playerKillMap.entrySet().stream().sorted(Map.Entry.comparingByValue());

        Map<UUID, Integer> result = new LinkedHashMap<>();
        sorted.forEach(entry -> result.put(entry.getKey(), entry.getValue()));

        return result;
    }

    @Getter
    public enum GameKillType {
        DOUBLE_KILL("&e%s fez um &6&lDOUBLE KILL", 2),
        TRIPLE_KILL("&e%s fez um &b&lTRIPLE KILL", 3),
        QUADRA_KILL("&e%s fez um &d&lQUADRA KILL", 4),
        MOONSTER_KILL("&e%s fez um &c&lMOONSTER KILL", 5);

        private final int amount;
        private final String name;

        GameKillType(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }
    }
}