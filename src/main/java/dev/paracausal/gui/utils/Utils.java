package dev.paracausal.gui.utils;

import dev.paracausal.gui.Plugin;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private final Plugin plugin;

    public Utils(Plugin plugin) {
        this.plugin = plugin;
    }


    public static ArrayList<String> toList(ConfigUtils config, String path) {
        Object o = config.getConfig().get(path);
        ArrayList<String> list = new ArrayList<>();

        if (o instanceof String) list.add(o.toString());
        else list = new ArrayList<>(config.getConfig().getStringList(path));

        return list;
    }


    public static ArrayList<String> getKeys(ConfigUtils config, String path) {
        return new ArrayList<>(config.getConfig().getConfigurationSection(path).getKeys(false));
    }



    private Object checkMiniMessage(String input, Player player) {
        boolean mini = false;
        if (input.startsWith("<mini> ")) {
            input = input.replaceFirst("<mini> ", "");
            mini = true;
        }

        if (mini) return miniMessage(input, player);
        else return input;
    }

    private Component miniMessage(String input, Player player) {
        if (plugin.placeholderApi && player != null) input = PlaceholderAPI.setPlaceholders(player, input);
        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize(input);
    }

    public String color(String input, Player player) {
        if (plugin.placeholderApi && player != null) input = PlaceholderAPI.setPlaceholders(player, input);

        if (plugin.serverVersion >= 16) {
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

    private void send(Player player, String input) {
        Object message = this.checkMiniMessage(input, player);
        if (message instanceof Component) {
            Audience audience = (Audience) player;
            audience.sendMessage((Component) message);
        }

        else player.sendMessage(this.color(input, player));
    }

    public void sendMessage(Player player, String input) {
        if (input == null || input.length() == 0) return;
        send(player, input);
    }

}
