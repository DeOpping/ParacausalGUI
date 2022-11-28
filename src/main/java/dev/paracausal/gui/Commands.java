package dev.paracausal.gui;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

import static dev.paracausal.gui.utils.Utils.*;

public class Commands {

    private static FileConfiguration config;
    private static String path;
    private static ClickType clickType;

    protected static void commandsConfig(FileConfiguration config) {
        Commands.config = config;
    }

    protected static void commandsPath(String path) {
        Commands.path = path + ".commands";
    }

    protected static void commandsClickType(ClickType clickType) {
        Commands.clickType = clickType;
    }

    private static ArrayList<String> allCommands() {
        return toList(config, path);
    }

    private static List<String> activeCommands() {
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

    public static void execute(Player player) {
        activeCommands().forEach(command -> {
            String lower = command.toLowerCase();

            if (lower.startsWith("[broadcast]")) {
                String msg = StringUtils.substringAfter(command, "]");
                if (msg.startsWith(" ")) msg = msg.replaceFirst(" ", "");

                String message = msg;
                Bukkit.getOnlinePlayers().forEach(p -> sendMessage(p, message));
                return;
            }

            if (lower.startsWith("[message]")) {
                String msg = StringUtils.substringAfter(command, "]");
                if (msg.startsWith(" ")) msg = msg.replaceFirst(" ", "");
                sendMessage(player, msg);
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
