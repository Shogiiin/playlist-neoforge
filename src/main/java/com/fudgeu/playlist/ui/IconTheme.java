package com.fudgeu.playlist.ui;

public enum IconTheme {
    LIGHT("light", "icon_theme.light"),
    ENHANCED("enhanced", "icon_theme.enhanced"),
    COLORFUL("colorful", "icon_theme.colorful"),
    COLORFUL_CONSISTENT("colorful_consistent", "icon_theme.colorful_consistent");

    public final String commonName;
    public final String translationKey;

    private IconTheme(String commonName, String translationKey) {
        this.commonName = commonName;
        this.translationKey = translationKey;
    }
}
