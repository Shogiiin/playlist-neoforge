package com.fudgeu.playlist.fluxui.style.color;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Color {
    private static final Logger LOGGER = LogManager.getLogger();
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    private Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public static Color create(float red, float green, float blue, float alpha) {
        return new Color(red, green, blue, alpha);
    }

    public static Color create(float red, float green, float blue) {
        return new Color(red, green, blue, 1.0F);
    }

    public static Color create(float gray) {
        return new Color(gray, gray, gray, 1.0F);
    }

    public static Color fromHex(int hex) {
        float r = (hex & 0xFF0000) >> 16;
        float g = (hex & 0xFF00) >> 8;
        float b = hex & 0xFF;
        return create(r / 255.0F, g / 255.0F, b / 255.0F);
    }

    public static Color fromHex(String hexString) {
        hexString = hexString.replace("#", "");

        int hex;
        try {
            hex = Integer.parseInt(hexString, 16);
        } catch (Exception var3) {
            LOGGER.error("Failed to interpret hex color '" + hexString + "'. Defaulting to white.");
            return create(1.0F);
        }

        return fromHex(hex);
    }

    public int hex() {
        int a = (int)(this.alpha * 255.0F) << 24;
        int r = (int)(this.red * 255.0F) << 16;
        int g = (int)(this.green * 255.0F) << 8;
        int b = (int)(this.blue * 255.0F);
        return a + r + g + b;
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }

    public float getAlpha() {
        return this.alpha;
    }
}
