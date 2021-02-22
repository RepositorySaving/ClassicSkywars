package marcos.devsoftware.skywars.utility;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@UtilityClass
public class Utility {

    public Location deserializeLocation(World world, String locationString) {
        String[] locationArray = locationString.split(",");

        double x = Double.parseDouble(locationArray[0].split(":")[1]);
        double y = Double.parseDouble(locationArray[1].split(":")[1]);
        double z = Double.parseDouble(locationArray[2].split(":")[1]);
        float yaw = Float.parseFloat(locationArray[3].split(":")[1]);
        float pitch = Float.parseFloat(locationArray[4].split(":")[1]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public String serializeLocation(Location location) {
        return "X:" + location.getX() + ", " + "Y:" + location.getY() + ", " + "Z:" + location.getZ() + ", " + "Yaw:" + location.getYaw() + ", " + "Pitch:" + location.getPitch();
    }

    public static String convertTime(long milliseconds, boolean isSimple) {
        long minutes = milliseconds / 1000 / 60;
        long seconds = milliseconds / 1000 % 60;

        if (isSimple) {
            if (minutes == 1 && seconds == 0) {
                return "60";
            } else if (minutes == 0) {
                return String.valueOf(seconds);
            }
        }

        return (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    public void sendTitle(Player player, String title, String subTitle, int fadeIn, int displayTime, int fadeOut) {
        title = MessageUtility.format(title);

        EntityPlayer craftPlayer = ((CraftPlayer) player).getHandle();
        PlayerConnection playerConnection = craftPlayer.playerConnection;
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}"), fadeIn, displayTime, fadeOut);
        playerConnection.sendPacket(titlePacket);

        if (subTitle != null) {
            subTitle = MessageUtility.format(subTitle);

            PacketPlayOutTitle subTitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subTitle + "\"}"), fadeIn, displayTime, fadeOut);
            playerConnection.sendPacket(subTitlePacket);
        }
    }
}