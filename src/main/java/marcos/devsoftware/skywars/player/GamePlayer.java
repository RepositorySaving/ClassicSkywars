package marcos.devsoftware.skywars.player;

import lombok.Getter;
import marcos.devsoftware.skywars.utility.ItemUtility;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
public class GamePlayer {

    private final Player player;

    public GamePlayer(Player player) {
        this.player = player;
    }

    public void activePlayerSettings() {
        activeGeneralSettings();

        ItemStack kitItem = ItemUtility.createItem(Material.CHEST, "&bEscolher Kit", null);
        ItemStack settingsItem = ItemUtility.createItem(Material.DIAMOND, "&6Opções", null);
        ItemStack leaveItem = ItemUtility.createItem(Material.REDSTONE_TORCH_ON, "&cDigite /sair para voltar ao início", null);

        player.getInventory().addItem(kitItem, settingsItem, leaveItem);
    }

    public void activePlayingSettings() {
        activeGeneralSettings();
    }

    public void activeSpectatorSettings() {
        activeGeneralSettings();

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void activeQuitSettings() {
        activeGeneralSettings();
    }

    public void activeGeneralSettings() {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setAllowFlight(false);
        player.setFlying(false);

        if (player.getGameMode() != GameMode.SURVIVAL) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        if (!player.getActivePotionEffects().isEmpty()) {
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }
    }
}