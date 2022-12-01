package dev.paracausal.gui;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public class ParacausalGUI {

    private static JavaPlugin plugin;
    private static String menuFolderPath;
    private static String bedrockPrefix;

    /**
     * Initialize the plugin!
     * @param plugin JavaPlugin
     */
    public static void setPlugin(JavaPlugin plugin) {
        ParacausalGUI.plugin = plugin;
        adventure = BukkitAudiences.create(plugin);
        new Menu();
        serverVersion = Integer.parseInt(plugin.getServer().getVersion().split("\\.")[1]);
        placeholderApi = plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
        headDatabase = plugin.getServer().getPluginManager().getPlugin("HeadDatabase") != null;
    }

    /**
     * Get the plugin instance!
     * @return JavaPlugin
     */
    public static JavaPlugin getPlugin() {
        return plugin;
    }


    /**
     * Set the location where your menus are stored!<br>
     * Example of what to set the path to: getDataFolder() + File.separator + "menus"<br>
     * This example would get menus from "plugins/(plugin)/menus/(menu.yml)"
     * @param path String
     */
    public static void setMenuFolderPath(String path) {
        menuFolderPath = path;
    }


    /**
     * Get the menu folder path that was set!
     * @return String
     */
    public static String getMenuFolderPath() {
        return menuFolderPath;
    }


    /**
     * Set the prefix for your Bedrock players!
     * @param prefix Bedrock username prefix
     */
    public static void setBedrockPrefix(String prefix) {
        bedrockPrefix = prefix;
    }

    /**
     * Get the bedrock player prefix you set!
     * @return String
     */
    public static String getBedrockPrefix() {
        return bedrockPrefix;
    }


    public static int serverVersion = 8;
    public static boolean placeholderApi = false;
    public static boolean headDatabase = false;

    public static BukkitAudiences adventure = null;

}