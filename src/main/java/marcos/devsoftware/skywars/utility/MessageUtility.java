package marcos.devsoftware.skywars.utility;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MessageUtility {

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(format(message));
    }

    public List<String> formattedList(List<String> list) {
        List<String> listFormatted = new ArrayList<>();
        list.forEach(line -> listFormatted.add(format(line)));

        return listFormatted;
    }

    public String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}