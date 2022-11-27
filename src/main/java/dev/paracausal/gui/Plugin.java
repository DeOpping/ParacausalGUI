package dev.paracausal.gui;

import dev.paracausal.gui.utils.ItemUtils;
import dev.paracausal.gui.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    @Override
    public void onLoad() {
        this.utils = new Utils(this);
        this.itemUtils = new ItemUtils(this);
    }

    @Override
    public void onEnable() {
        new Menu(this);
    }

    public int serverVersion = Integer.parseInt(getServer().getVersion().split("\\.")[1]);
    public boolean placeholderApi = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    public boolean headDatabase = getServer().getPluginManager().getPlugin("HeadDatabase") != null;
    public boolean advancedHeads = getServer().getPluginManager().getPlugin("AdvancedHeads") != null;

    private Utils utils;
    public Utils getUtils() { return utils; }

    private ItemUtils itemUtils;
    public ItemUtils getItemUtils() { return itemUtils; }

}