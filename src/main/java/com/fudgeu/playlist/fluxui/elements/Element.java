package com.fudgeu.playlist.fluxui.elements;

import com.fudgeu.playlist.fluxui.style.InheritedStyles;
import com.fudgeu.playlist.fluxui.style.Style;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;

public interface Element {
    void draw(GuiGraphics var1, int var2, int var3, InheritedStyles var4);

    int getWidth();

    int getHeight();

    void setParent(Element var1);

    Optional<Element> getParent();

    Style getStyle();

    ArrayList<Element> getElementsAt(int var1, int var2);

    Optional<String> getId();
}
