package marcos.devsoftware.skywars.utility;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.*;

import java.io.*;
import java.util.Objects;
import java.util.UUID;

@UtilityClass
public class WorldUtility {

    public World createWorld(World originalWorld, String newWorldName) {
        copyWorld(originalWorld.getWorldFolder(), new File(Bukkit.getWorldContainer(), newWorldName));

        World world = (new WorldCreator(newWorldName)).createWorld();
        world.setTime(0L);
        world.setDifficulty(Difficulty.NORMAL);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setStorm(false);
        world.setAutoSave(false);

        return world;
    }

    @SneakyThrows
    private void copyWorld(File src, File dest) {
        if (src.getName().equalsIgnoreCase("uid.dat")) return;
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            
            String[] files = src.list();
            if (files != null) {
                for (String file : files) {
                    File srcFile = new File(src, file);
                    File destFile = new File(dest, file);
                    copyWorld(srcFile, destFile);
                }
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            
            in.close();
            out.close();
        }
    }

    private boolean worldDelete(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : Objects.requireNonNull(files)) {
                if (file.isDirectory()) {
                    worldDelete(file);
                } else {
                    file.delete();
                }
            }
        }

        return path.delete();
    }

    public void deleteWorld(World world) {
        if (world == null) return;
        
        Bukkit.getServer().unloadWorld(world, false);
        File deleteFolder = world.getWorldFolder();
        
        worldDelete(deleteFolder);
    }

    public String nameGenerator(String worldName) {
        return worldName + "-" + UUID.randomUUID();
    }
}
