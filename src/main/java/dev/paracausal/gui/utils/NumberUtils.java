package dev.paracausal.gui.utils;

public class NumberUtils {

    protected static boolean canToInt(String input) {
        try { Integer.parseInt(input); return true; }
        catch (NumberFormatException ignored) { return false; }
    }

    protected static int toInt(String input) {
        if (!canToInt(input)) return 0;
        return Integer.parseInt(input);
    }

}
