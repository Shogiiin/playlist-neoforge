package com.fudgeu.playlist.enums;

public class Conditions {
    public static final String CREATIVE = "creative";
    public static final String UNDERWATER = "underwater";
    public static final String END_DRAGON = "end_dragon";
    public static final String CREDITS = "credits";
    public static final String MENU = "menu";

    public static boolean isConditionStrict(String condition) {
        switch (condition) {
            case "creative":
                return false;
            case "underwater":
            case "end_dragon":
            case "credits":
            case "menu":
                return true;
            default:
                return false;
        }
    }
}
