package dev.paracausal.gui;

import dev.paracausal.gui.utils.ConfigUtils;
import dev.paracausal.gui.utils.ItemUtils;
import dev.paracausal.gui.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static dev.paracausal.gui.Menu.*;

public class Creator {

    private final Utils utils;
    private final ItemUtils itemUtils;


    private ConfigUtils config;
    private String menu;
    private Inventory inventory;
    private Player player;


    public Creator(Plugin plugin) {
        this.utils = plugin.getUtils();
        this.itemUtils = plugin.getItemUtils();
    }


    public void openInventory(ConfigUtils config, String menu, Player player) {
        this.config = config;
        this.menu = menu;
        this.player = player;

        int rows = config.getConfig().getInt("rows");
        String title = config.getConfig().getString("title");
        Inventory inv = Bukkit.createInventory(null, rows*9, utils.color(title, player));

        boolean paginate = config.getConfig().contains("paginated");

        UUID uuid = player.getUniqueId();
        currentMenuMap.put(uuid, menu);
        currentInventoryMap.put(uuid, inv);
        if (paginate) currentPageMap.put(uuid, 1);

        this.inventory = inv;
        this.contents();

        player.openInventory(inv);
    }

    private void contents() {
        config.getConfig().getConfigurationSection("contents").getKeys(false).forEach(key ->
                itemUtils.slots(config, "contents." + key).forEach(slot -> {
                    ItemStack item = itemUtils.addNBT(itemUtils.createItem(config, "contents." + key), "key", "contents." + key);
                    inventory.setItem(slot, item);
                }));
    }

}
