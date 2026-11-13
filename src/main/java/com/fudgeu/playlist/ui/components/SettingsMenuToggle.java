package com.fudgeu.playlist.ui.components;

import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import net.minecraft.resources.ResourceLocation;

public class SettingsMenuToggle implements ComponentBuilder {
    private final PropTemplate propTemplate;
    private final ResourceLocation xmlFile = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/settings_menu/toggle/layout.xml");

    public SettingsMenuToggle() {
        this.propTemplate = new PropTemplate();
        this.propTemplate.requireBoolProp("value");
        this.propTemplate.requireRunnable("onToggle");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        PropProvider childProps = new PropProvider();
        childProps.setBoolProp("settingValue", propProvider.getBoolProp("value"));
        childProps.setRunnable("onSettingToggle", propProvider.getRunnable("onToggle"));
        childProps.setString("settingLabel", propProvider.getChildText());
        return childProps;
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
        return this.xmlFile;
    }
}
