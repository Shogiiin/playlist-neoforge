package com.fudgeu.playlist.fluxui.clickables;

public interface Focusable {
    boolean isFocusable();

    boolean isFocused();

    void setFocused(boolean var1);

    void onFocus();

    void onUnFocus();
}
