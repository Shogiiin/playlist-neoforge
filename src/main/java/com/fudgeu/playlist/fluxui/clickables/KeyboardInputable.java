package com.fudgeu.playlist.fluxui.clickables;

import com.fudgeu.playlist.fluxui.elements.Element;

public interface KeyboardInputable {
    void onCharPressed(char var1);

    void onKeyPressed(int var1);

    public interface CharacterHandler {
        void handle(Element var1, char var2);
    }

    public interface KeyboardHandler {
        void handle(Element var1, int var2);
    }
}
