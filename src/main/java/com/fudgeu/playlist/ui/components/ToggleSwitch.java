package com.fudgeu.playlist.ui.components;

import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import com.fudgeu.playlist.fluxui.transitions.TransitionHelper;
import net.minecraft.resources.ResourceLocation;

public class ToggleSwitch implements ComponentBuilder {
    private static final ResourceLocation xmlFile = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/toggle_switch/layout.xml");
    private final PropTemplate requiredProps = new PropTemplate();

    public ToggleSwitch() {
        this.requiredProps.requireRunnable("onToggle");
        this.requiredProps.requireBoolProp("toggleValue");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        TransitionHelper transitioner = FluxInstance.getInstance().getTransitionHelper();
        BoolProp toggleValue = propProvider.getBoolProp("toggleValue");
        toggleValue.addUpdateListener((oldValue, newValue) -> self.findChildById("ToggleSwitch").ifPresent(e -> {
            if (newValue) {
                e.getStyle().setPaddingLeft(transitioner.bezier(0, 8, 3));
            } else {
                e.getStyle().setPaddingLeft(transitioner.bezier(8, 0, 3));
            }
        }));
        Runnable onToggle = propProvider.getRunnable("onToggle");
        PropProvider childProps = new PropProvider();
        childProps.setClickHandler("onToggleClick", (e, m, x, y) -> onToggle.run());
        return childProps;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
        boolean toggleValue = propProvider.getBoolProp("toggleValue").get();
        self.findChildById("ToggleSwitch").ifPresent(e -> {
            if (toggleValue) {
                e.getStyle().setPaddingLeft(8);
            }
        });
    }

    @Override
    public void refresh() {
    }

    @Override
    public PropTemplate getPropTemplate() {
        return this.requiredProps;
    }

    @Override
    public ResourceLocation getComponentLocation() {
        return xmlFile;
    }
}
