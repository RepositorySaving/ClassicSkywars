package marcos.devsoftware.skywars.utility;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class ItemUtility {

    public ItemStack createItem(Material material, String displayName, String[] loreArray) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(MessageUtility.format(displayName));

        if (loreArray != null) {
            List<String> lore = MessageUtility.formattedList(Arrays.asList(loreArray));
            itemMeta.setLore(lore);
        }

        item.setItemMeta(itemMeta);
        return item;
    }
}