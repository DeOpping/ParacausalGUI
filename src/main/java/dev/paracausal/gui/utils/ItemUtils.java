package dev.paracausal.gui.utils;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.nbtapi.NBTItem;
import dev.paracausal.gui.Plugin;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.advancedplugins.heads.api.AdvancedHeadsAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static dev.paracausal.gui.utils.NumberUtils.toInt;


public class ItemUtils {

    private final Plugin plugin;
    private final Utils utils;
    private Player player = null;

    public ItemUtils(Plugin plugin) {
        this.plugin = plugin;
        this.utils = plugin.getUtils();
    }

    public void setPlayer(Player player) { this.player = player; }


    public ItemStack checkMaterial(ConfigUtils config, String path) {
        String material = config.getConfig().getString(path + ".material");
        ItemStack item;

        if (material.startsWith("head-")) {
            item = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial());
            ItemMeta meta = item.getItemMeta();

            String texture = StringUtils.substringAfter(material, "-");
            SkullUtils.applySkin(meta, texture);

            item.setItemMeta(meta);
            return item;
        }

        if (plugin.headDatabase && material.startsWith("hdb-")) {
            String id = StringUtils.substringAfter(material, "-");
            return new HeadDatabaseAPI().getItemHead(id);
        }

        if (plugin.advancedHeads && material.startsWith("ahd-")) {
            String id = StringUtils.substringAfter(material, "-");
            return AdvancedHeadsAPI.getHeadFromId(toInt(id));
        }

        Material mat;
        try { mat = XMaterial.matchXMaterial(material).get().parseMaterial(); }
        catch (IllegalArgumentException | NoSuchElementException exception) { mat = XMaterial.BARRIER.parseMaterial(); }

        item = new ItemStack(mat);

        if (plugin.serverVersion < 13 && config.getConfig().contains(path + ".data")) {
            int data = config.getConfig().getInt(path + ".data");
            item = new ItemStack(mat, 1, (short) 0, (byte) data);
        }

        return item;
    }


    public ItemStack addNBT(ItemStack item, String key, String value) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(key, value);

        item = nbtItem.getItem();
        return item;
    }


    public ItemMeta addLore(ItemMeta meta, ConfigUtils config, String path) {
        List<String> formatted = new ArrayList<>();
        utils.toList(config, path).forEach(string -> formatted.add(utils.color(string, player)));
        meta.setLore(formatted);
        return meta;
    }


    public ItemMeta addEnchants(ItemMeta meta, ConfigUtils config, String path) {
        utils.toList(config, path).forEach(string -> {
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

    public ItemMeta addItemFlags(ItemMeta meta, ConfigUtils config, String path) {
        utils.toList(config, path).forEach(string -> {
            ItemFlag flag;
            try { flag = ItemFlag.valueOf(string); }
            catch (IllegalArgumentException exception) { return; }

            meta.addItemFlags(flag);
        });

        return meta;
    }


    public ItemStack createItem(ConfigUtils config, String path) {
        ItemStack item = checkMaterial(config, path);
        ItemMeta meta = item.getItemMeta();

        if (config.getConfig().contains(path + ".name"))
            meta.setDisplayName(utils.color(config.getConfig().getString(path + ".name"), player));

        if (config.getConfig().contains(path + ".lore"))
            meta = addLore(meta, config, path + ".lore");

        if (config.getConfig().contains(path + ".enchants"))
            meta = addEnchants(meta, config, path + ".enchants");

        if (config.getConfig().contains(path + "item-flags"))
            meta = addItemFlags(meta, config, path + "item-flags");

        if (plugin.serverVersion >= 14 && config.getConfig().contains(path + ".model-data"))
            meta.setCustomModelData(config.getConfig().getInt(path + ".model-data"));

        item.setItemMeta(meta);

        this.player = null;
        return item;
    }


    public ArrayList<Integer> slots(ConfigUtils config, String path) {
        Object o = config.getConfig().get(path + ".slots");

        ArrayList<String> first = new ArrayList<>();
        ArrayList<Integer> second = new ArrayList<>();

        if (o instanceof String) {
            String slots = o.toString();
            first.add(slots);
        }

        else if (o instanceof Integer) second.add((int) o);

        else first = utils.toList(config, path + ".slots");


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
