package com.fudgeu.playlist.utils;

public class RGBA {
    public float r;
    public float g;
    public float b;
    public float a = 1.0F;

    public RGBA(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public RGBA(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public RGBA(float g) {
        this.r = g;
        this.g = g;
        this.b = g;
    }

    public RGBA(float g, float a) {
        this.r = g;
        this.g = g;
        this.b = g;
        this.a = a;
    }

    public RGBA lighter() {
        float newR = Math.min(this.r + 0.05F, 1.0F);
        float newG = Math.min(this.g + 0.05F, 1.0F);
        float newB = Math.min(this.b + 0.05F, 1.0F);
        return new RGBA(newR, newG, newB);
    }

    public RGBA darker() {
        float newR = Math.max(this.r - 0.05F, 0.0F);
        float newG = Math.max(this.g - 0.05F, 0.0F);
        float newB = Math.max(this.b - 0.05F, 0.0F);
        return new RGBA(newR, newG, newB);
    }

    public static RGBA hex(int hex) {
        float r = (hex & 0xFF0000) >> 16;
        float g = (hex & 0xFF00) >> 8;
        float b = hex & 0xFF;
        return new RGBA(r / 255.0F, g / 255.0F, b / 255.0F);
    }

    public static String hexString(int hex) {
        String hexString = Integer.toHexString(hex);
        if (hexString.length() < 6) {
            StringBuilder fixedHexString = new StringBuilder(hexString);

            for (int i = 0; i < 6 - hexString.length(); i++) {
                fixedHexString.insert(0, "0");
            }

            hexString = fixedHexString.toString();
        }

        return hexString;
    }

    public static Integer getHexFromString(String hexString) {
        hexString = hexString.replace("#", "");
        if (hexString.length() != 6) {
            return null;
        } else {
            int hex = 0;

            try {
                hex = Integer.parseInt(hexString, 16);
            } catch (Exception var3) {
                return null;
            }

            return hex;
        }
    }
}
