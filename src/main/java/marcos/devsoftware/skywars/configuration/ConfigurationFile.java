package marcos.devsoftware.skywars.configuration;

import com.google.common.base.Charsets;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

@Getter
public class ConfigurationFile {

    private final File file;
    private final YamlConfiguration configuration;

    @SneakyThrows
    public ConfigurationFile(String fileNameOriginal, File directory) {
        String fileName = fileNameOriginal.contains(".yml") ? fileNameOriginal : fileNameOriginal.concat(".yml");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        this.file = new File(directory, fileName);
        if (!this.file.exists()) {
            this.file.createNewFile();
        }

        this.configuration = new YamlConfiguration();
        this.configuration.load(new InputStreamReader(new FileInputStream(this.file), Charsets.UTF_8));
    }

    @SneakyThrows
    public void save() {
        this.configuration.save(this.file);
    }

    public String getName() {
        return FilenameUtils.removeExtension(this.file.getName());
    }
}