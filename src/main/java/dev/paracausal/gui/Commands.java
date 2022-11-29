package dev.paracausal.gui;

import dev.paracausal.gui.actions.Action;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static dev.paracausal.gui.Menu.*;
import static dev.paracausal.gui.actions.Actions.actions;
import static dev.paracausal.gui.utils.Utils.*;

public class Commands {

    private static ArrayList<String> allCommands(FileConfiguration config, String path) {
        return toList(config, path + ".commands");
    }


    private static List<String> activeCommands(FileConfiguration config, String path, ClickType clickType) {
        ArrayList<String> filtered = new ArrayList<>();

        allCommands(config, path).forEach(string -> {
            if (containsTag(string, "click")) {
                ClickType click;

                try { click = ClickType.valueOf(getTag(string, "click")); }
                catch (IllegalArgumentException ignored) { if (clickType == ClickType.LEFT) filtered.add(string); return; }

                if (clickType == click) filtered.add(removeTag(string, "click"));
            }

            else if (clickType == ClickType.LEFT) filtered.add(string);
        });

        return filtered;
    }


    public static void execute(FileConfiguration config, String path, ClickType clickType, Player player) {
        activeCommands(config, path, clickType).forEach(command -> {
            UUID uuid = player.getUniqueId();

            boolean containsBroadcastTag = containsTag(command, "broadcast");
            boolean containsBcTag = containsTag(command, "bc");
            if (containsBroadcastTag || containsBcTag) {
                String msg;
                if (containsBroadcastTag) {
                    msg = getTag(command, "broadcast");
                    command = removeTag(command, "broadcast");
                }

                else {
                    msg = getTag(command, "bc");
                    command = removeTag(command, "bc");
                }

                Bukkit.getOnlinePlayers().forEach(p -> sendMessage(p, msg));
            }

            boolean containsMessageTag = containsTag(command, "message");
            boolean containsMsgTag = containsTag(command, "msg");
            if (containsMessageTag || containsMsgTag) {
                String msg;
                if (containsMessageTag) {
                    msg = getTag(command, "message");
                    command = removeTag(command, "message");
                }

                else {
                    msg = getTag(command, "msg");
                    command = removeTag(command, "msg");
                }

                sendMessage(player, msg);
            }



            String lower = command.toLowerCase();



            //           Custom Actions           //
            if (!actions.isEmpty()) {
                boolean actionRan = false;

                for (Map.Entry<String, Action> entry : actions.entrySet()) {
                    String actionKey = entry.getKey();
                    Action action = entry.getValue();

                    if (lower.startsWith("[" + actionKey + "]")) {
                        action.run();
                        action.run(command);
                        action.run(player);
                        action.run(command, player);
                        actionRan = true;
                    }
                }

                if (actionRan) return;
            }


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

            if (lower.startsWith("[menu]")) {
                String menu = StringUtils.substringAfter(command, "]");
                if (menu.startsWith(" ")) menu = menu.replaceFirst(" ", "");
                if (menu.endsWith(".yml")) menu = menu.replace(".yml", "");

                switchingMenuList.add(uuid);
                openInventory(getMenuConfig(menu), menu, player);
                switchingMenuList.remove(uuid);
                return;
            }



            if (command.length() < 1) return;
            if (command.replaceAll(" ", "").length() < 1) return;


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
