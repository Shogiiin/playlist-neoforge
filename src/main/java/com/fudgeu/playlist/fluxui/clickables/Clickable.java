package com.fudgeu.playlist.fluxui.clickables;

import com.fudgeu.playlist.fluxui.elements.Element;
import java.util.function.Supplier;

public interface Clickable {
    boolean interactable();

    void onClick(MouseButton var1, float var2, float var3);

    void onRelease(MouseButton var1, float var2, float var3);

    void onHover(Supplier<Float> var1, Supplier<Float> var2);

    void onUnHover();

    public interface ClickHandler {
        void handle(Element var1, MouseButton var2, float var3, float var4);
    }

    public interface HoverHandler {
        void handle(Element var1, Supplier<Float> var2, Supplier<Float> var3);
    }

    public interface UnHoverHandler {
        void handle(Element var1);
    }
}
