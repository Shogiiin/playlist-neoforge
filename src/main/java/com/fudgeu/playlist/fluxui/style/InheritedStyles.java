package com.fudgeu.playlist.fluxui.style;

import com.fudgeu.playlist.fluxui.style.color.SolidColor;

public class InheritedStyles {
    private SolidColor foregroundColor;
    private float opacity;
    private int borderRadius;
    private float scaleX;
    private float scaleY;

    public InheritedStyles(SolidColor foregroundColor, float opacity, int borderRadius, float scaleX, float scaleY) {
        this.foregroundColor = foregroundColor;
        this.opacity = opacity;
        this.borderRadius = borderRadius;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public static InheritedStyles from(Style parentStyle) {
        return new InheritedStyles(
            parentStyle.getForegroundColor().orElse(SolidColor.WHITE),
            parentStyle.getOpacity().get(),
            parentStyle.getBorderRadius().get(),
            parentStyle.getScaleX(),
            parentStyle.getScaleY()
        );
    }

    public InheritedStyles passDown(Style parentStyle) {
        return new InheritedStyles(
            parentStyle.getForegroundColor().orElse(SolidColor.WHITE),
            this.opacity * parentStyle.getOpacity().get(),
            parentStyle.getBorderRadius().get(),
            this.scaleX * parentStyle.getScaleX(),
            this.scaleY * parentStyle.getScaleY()
        );
    }

    public SolidColor getForegroundColor() {
        return this.foregroundColor;
    }

    public InheritedStyles setForegroundColor(SolidColor foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    public float getOpacity() {
        return this.opacity;
    }

    public InheritedStyles setOpacity(float opacity) {
        this.opacity = opacity;
        return this;
    }

    public int getBorderRadius() {
        return this.borderRadius;
    }

    public InheritedStyles setBorderRadius(int borderRadius) {
        this.borderRadius = borderRadius;
        return this;
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public InheritedStyles setScaleX(float scaleX) {
        this.scaleX = scaleX;
        return this;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public InheritedStyles setScaleY(float scaleY) {
        this.scaleY = scaleY;
        return this;
    }
}
