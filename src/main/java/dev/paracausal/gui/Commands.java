package dev.paracausal.gui;

import dev.paracausal.gui.utils.ConfigUtils;
import dev.paracausal.gui.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

import static dev.paracausal.gui.utils.Utils.getKeys;

public class Commands {

    private final Utils utils;

    private ConfigUtils config;
    private String path;
    private ClickType clickType;

    public Commands(Plugin plugin) {
        this.utils = plugin.getUtils();
    }

    private ArrayList<String> allCommands() {
        return getKeys(config, path + ".commands");
    }

    private List<String> activeCommands() {
        ArrayList<String> filtered = new ArrayList<>();

        allCommands().forEach(string -> {
            if (string.contains(":")) {
                ClickType click = ClickType.LEFT;

                try { click = ClickType.valueOf(StringUtils.substringBefore(string, ":")); }
                catch (IllegalArgumentException ignored) {}

                if (clickType == click) filtered.add(StringUtils.substringAfter(string, ":"));
            }

            else if (clickType == ClickType.LEFT) filtered.add(string);
        });

        return filtered;
    }

    public void execute(ConfigUtils config, String path, ClickType clickType, Player player) {
        this.config = config;
        this.path = path + ".commands";
        this.clickType = clickType;

        activeCommands().forEach(command -> {
            String lower = command.toLowerCase();

            if (lower.startsWith("[broadcast]")) {
                String msg = StringUtils.substringAfter(command, "]");
                if (msg.startsWith(" ")) msg = msg.replaceFirst(" ", "");

                String message = msg;
                Bukkit.getOnlinePlayers().forEach(p -> utils.sendMessage(p, message));
                return;
            }

            if (lower.startsWith("[message]")) {
                String msg = StringUtils.substringAfter(command, "]");
                if (msg.startsWith(" ")) msg = msg.replaceFirst(" ", "");
                utils.sendMessage(player, msg);
                return;
            }

            CommandSender sender = Bukkit.getConsoleSender();
            if (lower.startsWith("[player]")) {
                command = StringUtils.substringAfter(command, "]");
                if (command.startsWith(" ")) command = command.replaceFirst(" ", "");
                sender = player;
            }

            Bukkit.dispatchCommand(sender, command);
        });
    }

}
