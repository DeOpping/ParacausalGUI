package dev.paracausal.gui;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static dev.paracausal.gui.Menu.*;
import static dev.paracausal.gui.utils.ItemUtils.*;
import static dev.paracausal.gui.utils.Utils.color;

public class Creator {

    private static FileConfiguration config;
    private static String menu;
    private static Inventory inventory;
    private static Player player;


    public static void openInventory(FileConfiguration config, String menu, Player player) {
        Creator.config = config;
        Creator.menu = menu;
        Creator.player = player;

        int rows = config.getInt("rows");
        String title = config.getString("title");
        Inventory inv = Bukkit.createInventory(null, rows*9, color(title, player));

        boolean paginate = config.contains("paginated");

        UUID uuid = player.getUniqueId();
        currentMenuMap.put(uuid, menu);
        currentInventoryMap.put(uuid, inv);
        if (paginate) currentPageMap.put(uuid, 1);

        inventory = inv;
        contents();

        player.openInventory(inv);
    }

    private static void contents() {
        config.getConfigurationSection("contents").getKeys(false).forEach(key ->
                slots(config, "contents." + key).forEach(slot -> {
                    ItemStack item = addNBT(createItem("contents." + key), "key", "contents." + key);
                    inventory.setItem(slot, item);
                }));
    }

}
