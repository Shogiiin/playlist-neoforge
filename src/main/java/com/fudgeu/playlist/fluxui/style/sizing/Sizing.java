package com.fudgeu.playlist.fluxui.style.sizing;

import com.fudgeu.playlist.fluxui.elements.Element;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Sizing {
    public static final Sizing AUTO = new Sizing(Sizing.SizingType.AUTO, null, null);
    private final Sizing.SizingType sizingType;
    private final Supplier<Float> dependentSize;
    private final Supplier<Integer> fixedSize;
    private final Logger LOGGER = LogManager.getLogger();

    public Sizing(Sizing.SizingType sizingType, Supplier<Float> dependentSize, Supplier<Integer> fixedSize) {
        this.sizingType = sizingType;
        this.dependentSize = dependentSize;
        this.fixedSize = fixedSize;
    }

    public static Sizing px(int size) {
        return new Sizing(Sizing.SizingType.FIXED, null, () -> size);
    }

    public static Sizing pxSupplier(Supplier<Integer> size) {
        return new Sizing(Sizing.SizingType.FIXED, null, size);
    }

    public static Sizing percent(float percent) {
        return new Sizing(Sizing.SizingType.DEPENDENT, () -> percent, null);
    }

    public static Sizing percentSupplier(Supplier<Float> percent) {
        return new Sizing(Sizing.SizingType.DEPENDENT, percent, null);
    }

    public int calculate(Sizing.Dimension dimension, Element parent, Supplier<Integer> autoSize) {
        switch (this.sizingType) {
            case FIXED:
                return this.fixedSize.get();
            case DEPENDENT:
                if ((dimension != Sizing.Dimension.WIDTH || parent.getStyle().getWidth().sizingType != Sizing.SizingType.AUTO)
                    && (dimension != Sizing.Dimension.HEIGHT || parent.getStyle().getHeight().sizingType != Sizing.SizingType.AUTO)) {
                    int paddingV = parent.getStyle().getPaddingTop() + parent.getStyle().getPaddingBottom();
                    int paddingH = parent.getStyle().getPaddingLeft() + parent.getStyle().getPaddingRight();
                    int parentSize = dimension == Sizing.Dimension.WIDTH ? parent.getWidth() - paddingH : parent.getHeight() - paddingV;
                    return (int)(parentSize * this.dependentSize.get());
                } else {
                    return autoSize.get();
                }
            case AUTO:
                return autoSize.get();
            default:
                return 0;
        }
    }

    public Sizing.SizingType getSizingType() {
        return this.sizingType;
    }

    public static enum Dimension {
        WIDTH,
        HEIGHT;
    }

    public static enum SizingType {
        FIXED,
        DEPENDENT,
        AUTO;
    }
}
