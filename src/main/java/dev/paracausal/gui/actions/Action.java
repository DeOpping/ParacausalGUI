package dev.paracausal.gui.actions;


import org.bukkit.entity.Player;

public interface Action {
    void run();
    void run(String command);
    void run(Player player);
    void run(String command, Player player);

}