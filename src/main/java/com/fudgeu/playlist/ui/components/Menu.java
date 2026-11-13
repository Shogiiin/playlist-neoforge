package com.fudgeu.playlist.ui.components;

import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import net.minecraft.resources.ResourceLocation;

public class Menu implements ComponentBuilder {
    private final ResourceLocation componentLocation = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/menu/layout.xml");
    private final PropTemplate propTemplate = new PropTemplate();

    public Menu() {
        this.propTemplate.requireString("title", "Untitled Menu");
        this.propTemplate.requireString("primaryButtonLabel", "Primary Button");
        this.propTemplate.requireRunnable("primaryButtonAction", () -> {});
        this.propTemplate.requireBoolProp("primaryButtonEnabled", new BoolProp(true));
        this.propTemplate.requireRunnable("backAction", () -> {});
        this.propTemplate.requireString("footerText", "");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        return propProvider;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
    }

    @Override
    public void refresh() {
    }

    @Override
    public PropTemplate getPropTemplate() {
        return this.propTemplate;
    }

    @Override
    public ResourceLocation getComponentLocation() {
        return this.componentLocation;
    }
}
