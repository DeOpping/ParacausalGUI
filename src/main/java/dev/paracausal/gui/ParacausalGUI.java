package dev.paracausal.gui;

import org.bukkit.plugin.java.JavaPlugin;

public class ParacausalGUI {

    private static JavaPlugin plugin;

    /**
     * Initialize the plugin!
     * @param plugin JavaPlugin
     */
    public static void setPlugin(JavaPlugin plugin) {
        ParacausalGUI.plugin = plugin;
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


    public static int serverVersion = 8;
    public static boolean placeholderApi = false;
    public static boolean headDatabase = false;
    public static boolean advancedHeads = false;


}