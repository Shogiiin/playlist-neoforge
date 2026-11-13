package com.fudgeu.playlist.fluxui.style;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GlobalStylesheet {
    private final HashMap<String, Style> globalStylesheet = new HashMap<>();

    public void addStyle(String styleName, Style style) {
        this.globalStylesheet.putIfAbsent(styleName, style);
    }

    public void addStyles(Map<String, Style> styles) {
        this.globalStylesheet.putAll(styles);
    }

    public Optional<Style> getStyle(String styleName) {
        return Optional.ofNullable(this.globalStylesheet.get(styleName));
    }
}
