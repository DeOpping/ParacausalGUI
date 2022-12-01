package dev.paracausal.gui;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static dev.paracausal.gui.Menu.*;
import static dev.paracausal.gui.ParacausalGUI.getBedrockPrefix;
import static dev.paracausal.gui.utils.ItemUtils.*;
import static dev.paracausal.gui.utils.Utils.color;
import static dev.paracausal.gui.utils.Utils.getMenuConfig;

public class Creator {

    public static void openInventory(FileConfiguration config, String menu, Player player) {
        if (menu.endsWith(".yml")) menu = menu.replace(".yml", "");

        if (player.getName().startsWith(getBedrockPrefix()))
            config = getMenuConfig(menu + "-bedrock");

        int rows = config.getInt("rows");
        String title = config.getString("title");
        Inventory inv = Bukkit.createInventory(null, rows*9, color(title, player));

        boolean paginate = config.contains("paginated");

        UUID uuid = player.getUniqueId();

        currentMenuMap.put(uuid, menu);
        currentInventoryMap.put(uuid, inv);
        if (paginate) currentPageMap.put(uuid, 1);

        contents(config, inv, player);

        player.openInventory(inv);
    }


    private static void contents(FileConfiguration config, Inventory inventory, Player player) {
        config.getConfigurationSection("contents").getKeys(false).forEach(key ->
                slots(config, "contents." + key).forEach(slot -> {
                    ItemStack item = addNBT(createItem(config, "contents." + key, player), "key", "contents." + key);
                    inventory.setItem(slot, item);
                }));
    }

}