package com.fudgeu.playlist.ui.widgets;

import com.fudgeu.playlist.fluxui.FluxWidget;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Random;

public class WidgetHandler {
    public static WidgetHandler INSTANCE;
    private final LinkedHashMap<Integer, FluxWidget> widgets = new LinkedHashMap<>();
    private final Random random = new Random();

    private WidgetHandler() {
    }

    public static WidgetHandler init() {
        return INSTANCE = new WidgetHandler();
    }

    public int addWidget(FluxWidget widget) {
        int newId = this.random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.widgets.put(newId, widget);
        return newId;
    }

    public Optional<FluxWidget> getWidget(int id) {
        return Optional.ofNullable(this.widgets.get(id));
    }

    public void deleteWidget(int id) {
        this.widgets.remove(id);
    }

    public LinkedHashMap<Integer, FluxWidget> getWidgets() {
        return this.widgets;
    }
}
