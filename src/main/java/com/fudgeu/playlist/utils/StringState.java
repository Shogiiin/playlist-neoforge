package com.fudgeu.playlist.utils;

import java.util.HashMap;
import java.util.UUID;

public class StringState {
    private final HashMap<UUID, StringState.StringListener> listeners = new HashMap<>();
    private String currentValue;

    public StringState(String initialValue) {
        this.currentValue = initialValue;
    }

    public String get() {
        return this.currentValue;
    }

    public void set(String newValue) {
        if (!newValue.equals(this.currentValue)) {
            String oldValue = this.currentValue;
            this.currentValue = newValue;

            for (StringState.StringListener listener : this.listeners.values()) {
                listener.fireUpdate(oldValue, this.currentValue);
            }
        }
    }

    public UUID addUpdateListener(StringState.StringListener listener) {
        UUID newId = UUID.randomUUID();
        this.listeners.put(newId, listener);
        return newId;
    }

    public void removeListener(UUID id) {
        this.listeners.remove(id);
    }

    public interface StringListener {
        void fireUpdate(String var1, String var2);
    }
}
