package dev.paracausal.gui.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.paracausal.gui.ParacausalGUI.*;

public class Utils {


    public static ArrayList<String> toList(FileConfiguration config, String path) {
        Object o = config.get(path);
        ArrayList<String> list = new ArrayList<>();

        if (o instanceof String) list.add(o.toString());
        else list = new ArrayList<>(config.getStringList(path));

        return list;
    }


    public static ArrayList<String> getKeys(FileConfiguration config, String path) {
        return new ArrayList<>(config.getConfigurationSection(path).getKeys(false));
    }


    public static FileConfiguration getMenuConfig(String menu) {
        FileConfiguration cfg = null;
        File cfgFile = new File(getMenuFolderPath(), menu + ".yml");
        if (!cfgFile.exists()) return null;

        try { cfg = YamlConfiguration.loadConfiguration(cfgFile); }
        catch (IllegalArgumentException e) { e.printStackTrace(); }

        return cfg;
    }


    private static Object checkMiniMessage(String input, Player player) {
        boolean mini = false;
        if (input.startsWith("<mini> ")) {
            input = input.replaceFirst("<mini> ", "");
            mini = true;
        }

        if (mini) return miniMessage(input, player);
        else return input;
    }

    private static Component miniMessage(String input, Player player) {
        if (placeholderApi && player != null) input = PlaceholderAPI.setPlaceholders(player, input);
        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize(input);
    }

    public static String color(String input, Player player) {
        if (placeholderApi && player != null) input = PlaceholderAPI.setPlaceholders(player, input);

        if (serverVersion >= 16) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(input);

            while (matcher.find()) {
                String hex = input.substring(matcher.start(), matcher.end());
                String replace = hex.replace('#', 'x');

                char[] chars = replace.toCharArray();
                StringBuilder stringBuilder = new StringBuilder();

                for (char c : chars) stringBuilder.append("&").append(c);

                input = input.replace(hex, stringBuilder.toString());
                matcher = pattern.matcher(input);
            }
        }

        return ChatColor.translateAlternateColorCodes('&', input);
    }

    private static void send(Player player, String input) {
        Object message = checkMiniMessage(input, player);
        if (message instanceof Component) {
            Audience audience = adventure.player(player);
            audience.sendMessage((Component) message);
        }

        else player.sendMessage(color(input, player));
    }

    public static void sendMessage(Player player, String input) {
        if (input == null || input.length() == 0) return;
        send(player, input);
    }


    public static boolean containsTag(String input, String tag) {
        return input.contains("{{" + tag + ":");
    }

    public static String getTag(String input, String tag) {
        return StringUtils.substringBetween(input, "{{" + tag + ":", "}}");
    }

    public static String removeTag(String input, String tag) {
        String replace = "{{" + tag + ":" + getTag(input, tag) + "}}";

        String result = input.replace(replace, "");

        if (result.startsWith(" ")) result = result.replaceFirst(" ", "");
        if (result.endsWith(" ")) result = StringUtils.removeEnd(result, " ");

        return result;
    }

}
