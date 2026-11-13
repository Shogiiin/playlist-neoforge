package com.fudgeu.playlist.fluxui.transitions;

import com.fudgeu.playlist.fluxui.elements.Element;
import java.util.function.Supplier;

public class QuickTransition {
    public static void scaleIn(Element element, TransitionHelper transitioner, float scaleStart, int duration) {
        Supplier<Float> scaleSupplier1 = transitioner.bezier(scaleStart, 1.0F, duration);
        Supplier<Float> scaleSupplier2 = transitioner.bezier(0.0F, 1.0F, duration);
        element.getStyle().setScaleX(scaleSupplier1);
        element.getStyle().setScaleY(scaleSupplier1);
        element.getStyle().setOpacity(scaleSupplier2);
    }

    public static void scaleOut(Element element, TransitionHelper transitioner, float scaleEnd, int duration) {
        Supplier<Float> scaleSupplier1 = transitioner.bezier(1.0F, scaleEnd, duration);
        Supplier<Float> scaleSupplier2 = transitioner.bezier(1.0F, 0.0F, duration);
        element.getStyle().setScaleX(scaleSupplier1);
        element.getStyle().setScaleY(scaleSupplier1);
        element.getStyle().setOpacity(scaleSupplier2);
    }

    public static void slideInHorizontal(Element element, TransitionHelper transitioner, int offset, int duration) {
        Supplier<Float> opacitySupplier = transitioner.bezier(0.0F, 1.0F, duration);
        Supplier<Integer> translateSupplier = transitioner.bezier(offset, 0, duration);
        element.getStyle().setOpacity(opacitySupplier);
        element.getStyle().setTranslationX(translateSupplier);
    }

    public static void slideOutHorizontal(Element element, TransitionHelper transitioner, int offset, int duration) {
        Supplier<Float> opacitySupplier = transitioner.bezier(1.0F, 0.0F, duration);
        Supplier<Integer> translateSupplier = transitioner.bezier(0, offset, duration);
        element.getStyle().setOpacity(opacitySupplier);
        element.getStyle().setTranslationX(translateSupplier);
    }

    public static void fadeOut(Element element, TransitionHelper transitioner, float to, int duration) {
        Supplier<Float> opacitySupplier = transitioner.bezier(1.0F, to, duration);
        element.getStyle().setOpacity(opacitySupplier);
    }
}
