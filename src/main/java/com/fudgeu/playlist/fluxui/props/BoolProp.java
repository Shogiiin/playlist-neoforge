package com.fudgeu.playlist.fluxui.props;

import java.util.ArrayList;

public class BoolProp {
    private final ArrayList<BoolProp.BoolListener> listeners = new ArrayList<>();
    private boolean currentValue;

    public BoolProp(boolean initialValue) {
        this.currentValue = initialValue;
    }

    public boolean get() {
        return this.currentValue;
    }

    public void invert() {
        this.set(!this.currentValue);
    }

    public void set(boolean newValue) {
        if (newValue != this.currentValue) {
            this.currentValue = newValue;

            for (BoolProp.BoolListener listener : this.listeners) {
                listener.fireUpdate(!this.currentValue, this.currentValue);
            }
        }
    }

    public void addUpdateListener(BoolProp.BoolListener listener) {
        this.listeners.add(listener);
    }

    public interface BoolListener {
        void fireUpdate(boolean var1, boolean var2);
    }
}
