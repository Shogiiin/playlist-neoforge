package com.fudgeu.playlist.fluxui.style.color;

import java.util.function.Supplier;

public class SolidColor implements ColorStyle {
    public static final SolidColor TRANSPARENT = create(Color.create(0.0F, 0.0F, 0.0F, 0.0F));
    public static final SolidColor WHITE = create(Color.create(1.0F));
    private Supplier<Color> color;

    private SolidColor(Supplier<Color> color) {
        this.color = color;
    }

    public static SolidColor create(Supplier<Color> color) {
        return new SolidColor(color);
    }

    public static SolidColor create(Color color) {
        return new SolidColor(() -> color);
    }

    @Override
    public Color getColor() {
        return this.color.get();
    }
}
