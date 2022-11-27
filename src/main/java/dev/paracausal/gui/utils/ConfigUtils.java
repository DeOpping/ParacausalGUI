package dev.paracausal.gui.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigUtils {

    private FileConfiguration dataConfig;


    /**
     * Set the location of the file!
     * @param parent the parent file, expected to be the plugin's dataFolder()
     * @param location file path and extension
     */
    public void setFile(File parent, String location) {
        dataConfig = YamlConfiguration.loadConfiguration(new File(parent, location));
    }


    /**
     * Get the FileConfiguration!
     * @return FileConfiguration
     */
    public FileConfiguration getConfig() {
        return dataConfig;
    }

}
