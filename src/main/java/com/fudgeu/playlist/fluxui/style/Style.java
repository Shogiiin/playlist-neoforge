package com.fudgeu.playlist.fluxui.style;

import com.fudgeu.playlist.fluxui.style.color.ColorStyle;
import com.fudgeu.playlist.fluxui.style.color.SolidColor;
import com.fudgeu.playlist.fluxui.style.container.Alignment;
import com.fudgeu.playlist.fluxui.style.container.ContentSpacing;
import com.fudgeu.playlist.fluxui.style.container.LayoutDirection;
import com.fudgeu.playlist.fluxui.style.sizing.Sizing;
import java.util.Optional;
import java.util.function.Supplier;

public class Style {
    private String id = "";
    private LayoutDirection layoutDirection = LayoutDirection.VERTICAL;
    private ContentSpacing contentSpacing = ContentSpacing.ADJACENT;
    private Supplier<Integer> gap = () -> 0;
    private Alignment contentAlignment = Alignment.START;
    private Alignment selfAlignment;
    private Supplier<Integer> borderRadius = () -> 0;
    private Sizing width = Sizing.AUTO;
    private Sizing height = Sizing.AUTO;
    private Sizing maxWidth = Sizing.AUTO;
    private Sizing maxHeight = Sizing.AUTO;
    private ColorStyle backgroundColor = SolidColor.TRANSPARENT;
    private SolidColor foregroundColor = null;
    private Supplier<Float> opacity = () -> 1.0F;
    private Supplier<Integer> paddingTop = () -> 0;
    private Supplier<Integer> paddingBottom = () -> 0;
    private Supplier<Integer> paddingLeft = () -> 0;
    private Supplier<Integer> paddingRight = () -> 0;
    private Supplier<Float> scaleX = () -> 1.0F;
    private Supplier<Float> scaleY = () -> 1.0F;
    private Supplier<Integer> translationX = () -> 0;
    private Supplier<Integer> translationY = () -> 0;

    private Style() {
    }

    public static Style create() {
        return new Style();
    }

    public Style setId(String id) {
        this.id = id;
        return this;
    }

    public Style setLayoutDirection(LayoutDirection layoutDirection) {
        this.layoutDirection = layoutDirection;
        return this;
    }

    public Style setContentSpacing(ContentSpacing contentSpacing) {
        this.contentSpacing = contentSpacing;
        return this;
    }

    public Style setGap(Supplier<Integer> gap) {
        this.gap = gap;
        return this;
    }

    public Style setGap(int gap) {
        return this.setGap(() -> gap);
    }

    public Style setContentAlignment(Alignment contentAlignment) {
        this.contentAlignment = contentAlignment;
        return this;
    }

    public Style setSelfAlignment(Alignment selfAlignment) {
        this.selfAlignment = selfAlignment;
        return this;
    }

    public Optional<Alignment> getSelfAlignment() {
        return Optional.ofNullable(this.selfAlignment);
    }

    public int getGap() {
        return this.gap.get();
    }

    public Style setPadding(Supplier<Integer> paddingSupplier) {
        this.paddingTop = paddingSupplier;
        this.paddingBottom = paddingSupplier;
        this.paddingLeft = paddingSupplier;
        this.paddingRight = paddingSupplier;
        return this;
    }

    public Style setPadding(int padding) {
        return this.setPadding(() -> padding);
    }

    public Style setPaddingTop(Supplier<Integer> padding) {
        this.paddingTop = padding;
        return this;
    }

    public Style setPaddingBottom(Supplier<Integer> padding) {
        this.paddingBottom = padding;
        return this;
    }

    public Style setPaddingLeft(Supplier<Integer> padding) {
        this.paddingLeft = padding;
        return this;
    }

    public Style setPaddingRight(Supplier<Integer> padding) {
        this.paddingRight = padding;
        return this;
    }

    public Style setPaddingTop(int padding) {
        return this.setPaddingTop(() -> padding);
    }

    public Style setPaddingBottom(int padding) {
        return this.setPaddingBottom(() -> padding);
    }

    public Style setPaddingLeft(int padding) {
        return this.setPaddingLeft(() -> padding);
    }

    public Style setPaddingRight(int padding) {
        return this.setPaddingRight(() -> padding);
    }

    public int getPaddingTop() {
        return this.paddingTop.get();
    }

    public int getPaddingBottom() {
        return this.paddingBottom.get();
    }

    public int getPaddingLeft() {
        return this.paddingLeft.get();
    }

    public int getPaddingRight() {
        return this.paddingRight.get();
    }

    public float getScaleX() {
        return this.scaleX.get();
    }

    public float getScaleY() {
        return this.scaleY.get();
    }

    public int getTranslationX() {
        return this.translationX.get();
    }

    public int getTranslationY() {
        return this.translationY.get();
    }

    public Optional<SolidColor> getForegroundColor() {
        return Optional.ofNullable(this.foregroundColor);
    }

    public String getId() {
        return this.id;
    }

    public LayoutDirection getLayoutDirection() {
        return this.layoutDirection;
    }

    public ContentSpacing getContentSpacing() {
        return this.contentSpacing;
    }

    public Alignment getContentAlignment() {
        return this.contentAlignment;
    }

    public Supplier<Integer> getBorderRadius() {
        return this.borderRadius;
    }

    public Style setBorderRadius(Supplier<Integer> borderRadius) {
        this.borderRadius = borderRadius;
        return this;
    }

    public Sizing getWidth() {
        return this.width;
    }

    public Style setWidth(Sizing width) {
        this.width = width;
        return this;
    }

    public Sizing getHeight() {
        return this.height;
    }

    public Style setHeight(Sizing height) {
        this.height = height;
        return this;
    }

    public Sizing getMaxWidth() {
        return this.maxWidth;
    }

    public Style setMaxWidth(Sizing maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public Sizing getMaxHeight() {
        return this.maxHeight;
    }

    public Style setMaxHeight(Sizing maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public ColorStyle getBackgroundColor() {
        return this.backgroundColor;
    }

    public Style setBackgroundColor(ColorStyle backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Style setForegroundColor(SolidColor foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    public Supplier<Float> getOpacity() {
        return this.opacity;
    }

    public Style setOpacity(Supplier<Float> opacity) {
        this.opacity = opacity;
        return this;
    }

    public Style setScaleX(Supplier<Float> scaleX) {
        this.scaleX = scaleX;
        return this;
    }

    public Style setScaleY(Supplier<Float> scaleY) {
        this.scaleY = scaleY;
        return this;
    }

    public Style setTranslationX(Supplier<Integer> translationX) {
        this.translationX = translationX;
        return this;
    }

    public Style setTranslationY(Supplier<Integer> translationY) {
        this.translationY = translationY;
        return this;
    }
}
