package dev.paracausal.gui.utils;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.nbtapi.NBTItem;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.advancedplugins.heads.api.AdvancedHeadsAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static dev.paracausal.gui.ParacausalGUI.*;
import static dev.paracausal.gui.utils.NumberUtils.toInt;
import static dev.paracausal.gui.utils.Utils.color;
import static dev.paracausal.gui.utils.Utils.toList;


public class ItemUtils {

    private static FileConfiguration config = null;
    private static Player player = null;


    public static void itemUtilsConfig(FileConfiguration config) { ItemUtils.config = config; }
    public static void itemUtilsPlayer(Player player) { ItemUtils.player = player; }


    public static ItemStack checkMaterial(String path) {
        String materialPath = path + ".material";
        String material = config.getString(materialPath);
        ItemStack item;

        if (material.startsWith("head-")) {
            item = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial());
            ItemMeta meta = item.getItemMeta();

            String texture = StringUtils.substringAfter(material, "-");
            SkullUtils.applySkin(meta, texture);

            item.setItemMeta(meta);
            return item;
        }

        if (headDatabase && material.startsWith("hdb-")) {
            String id = StringUtils.substringAfter(material, "-");
            return new HeadDatabaseAPI().getItemHead(id);
        }

        if (advancedHeads && material.startsWith("ahd-")) {
            String id = StringUtils.substringAfter(material, "-");
            return AdvancedHeadsAPI.getHeadFromId(toInt(id));
        }

        Material mat;
        try { mat = XMaterial.matchXMaterial(material).get().parseMaterial(); }
        catch (IllegalArgumentException | NoSuchElementException exception) { mat = XMaterial.BARRIER.parseMaterial(); }

        item = new ItemStack(mat);

        if (serverVersion < 13 && config.contains(path + ".data")) {
            int data = config.getInt(path + ".data");
            item = new ItemStack(mat, 1, (short) 0, (byte) data);
        }

        return item;
    }


    public static ItemStack addNBT(ItemStack item, String key, String value) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(key, value);

        item = nbtItem.getItem();
        return item;
    }


    public static ItemMeta addLore(ItemMeta meta, String path) {
        List<String> formatted = new ArrayList<>();
        toList(config, path).forEach(string -> formatted.add(color(string, player)));
        meta.setLore(formatted);
        return meta;
    }


    public static ItemMeta addEnchants(ItemMeta meta, String path) {
        toList(config, path).forEach(string -> {
            String enchant = string;
            int level = 1;
            boolean restrict = false;

            if (string.contains(" ")) {
                String[] split = string.split(" ");
                enchant = split[0];

                level = toInt(split[1]);
                restrict = split.length > 2 && split[2].equalsIgnoreCase("true");
            }

            Enchantment enchantment;
            try { enchantment = XEnchantment.valueOf(enchant).getEnchant(); }
            catch (IllegalArgumentException exception) { return; }

            meta.addEnchant(enchantment, level, restrict);
        });

        return meta;
    }

    public static ItemMeta addItemFlags(ItemMeta meta, String path) {
        toList(config, path).forEach(string -> {
            ItemFlag flag;
            try { flag = ItemFlag.valueOf(string); }
            catch (IllegalArgumentException exception) { return; }

            meta.addItemFlags(flag);
        });

        return meta;
    }


    public static ItemStack createItem(String path) {
        ItemStack item = checkMaterial(path);
        ItemMeta meta = item.getItemMeta();

        if (config.contains(path + ".name"))
            meta.setDisplayName(color(config.getString(path + ".name"), player));

        if (config.contains(path + ".lore"))
            meta = addLore(meta, path + ".lore");

        if (config.contains(path + ".enchants"))
            meta = addEnchants(meta, path + ".enchants");

        if (config.contains(path + "item-flags"))
            meta = addItemFlags(meta, path + "item-flags");

        if (serverVersion >= 14 && config.contains(path + ".model-data"))
            meta.setCustomModelData(config.getInt(path + ".model-data"));

        item.setItemMeta(meta);

        player = null;
        return item;
    }


    public static ArrayList<Integer> slots(FileConfiguration config, String path) {
        Object o = config.get(path + ".slots");

        ArrayList<String> first = new ArrayList<>();
        ArrayList<Integer> second = new ArrayList<>();

        if (o instanceof String) {
            String slots = o.toString();
            first.add(slots);
        }

        else if (o instanceof Integer) second.add((int) o);

        else first = toList(config, path + ".slots");


        first.forEach(input -> {
            if (!input.contains("-")) { second.add(toInt(input)); return; }
            String[] split = input.split("-");
            if (split.length < 1) return;

            for (int i = toInt(split[0]); i <= toInt(split[1]); i++)
                second.add(i);
        });


        return second;
    }

}
