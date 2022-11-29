package dev.paracausal.gui;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

import static dev.paracausal.gui.Commands.*;
import static dev.paracausal.gui.ParacausalGUI.getPlugin;
import static dev.paracausal.gui.utils.Utils.getMenuConfig;

public class Menu implements Listener {

    public static HashMap<UUID, String> currentMenuMap = new HashMap<>();
    public static HashMap<UUID, Inventory> currentInventoryMap = new HashMap<>();
    public static HashMap<UUID, Integer> currentPageMap = new HashMap<>();



    public Menu() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }


    public static void openInventory(FileConfiguration config, String menu, Player player) {
        Creator.openInventory(config, menu, player);
    }



    @EventHandler
    private void onClose(InventoryCloseEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        currentMenuMap.remove(uuid);
        currentInventoryMap.remove(uuid);
        currentPageMap.remove(uuid);
    }


    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        currentMenuMap.remove(uuid);
        currentInventoryMap.remove(uuid);
        currentPageMap.remove(uuid);
    }


    @EventHandler
    private void onInventoryDrag(InventoryDragEvent e) {
        UUID uuid = e.getWhoClicked().getUniqueId();
        if (!currentInventoryMap.containsKey(uuid)) return;
        e.setCancelled(true);
    }


    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();

        if (!currentInventoryMap.containsKey(uuid)) return;
        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().equals(Material.AIR)) return;

        ClickType clickType = e.getClick();

        NBTItem nbt = new NBTItem(item);
        String path = nbt.getString("key");

        execute(getMenuConfig(currentMenuMap.get(uuid)), path, clickType, player);
    }

}
