package com.fudgeu.playlist.fluxui.backgrounddrawer;

import com.fudgeu.playlist.fluxui.style.InheritedStyles;
import com.fudgeu.playlist.fluxui.style.color.ColorStyle;
import net.minecraft.client.gui.GuiGraphics;

public interface BackgroundDrawer {
    void draw(GuiGraphics var1, int var2, int var3, int var4, int var5, InheritedStyles var6, ColorStyle var7);
}
