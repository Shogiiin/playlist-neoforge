package com.fudgeu.playlist.ui.components.icon_theme_menu;

import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import com.fudgeu.playlist.fluxui.style.color.Color;
import com.fudgeu.playlist.fluxui.style.color.SolidColor;
import net.minecraft.resources.ResourceLocation;

public class IconThemeEntry implements ComponentBuilder {
    private final ResourceLocation componentLocation = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/icon_theme_menu/entry/layout.xml");
    private final PropTemplate propTemplate;
    private final SolidColor SELECTED_COLOR = SolidColor.create(Color.fromHex(855309));
    private final SolidColor UNSELECTED_COLOR = SolidColor.TRANSPARENT;

    public IconThemeEntry() {
        this.propTemplate = new PropTemplate();
        this.propTemplate.requireString("label");
        this.propTemplate.requireRunnable("onSelect");
        this.propTemplate.requireBoolProp("selected");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        String label = propProvider.getString("label");
        Runnable onSelect = propProvider.getRunnable("onSelect");
        BoolProp selected = propProvider.getBoolProp("selected");
        selected.addUpdateListener(
            (oldVal, newVal) -> self.findChildById("IconThemeEntry")
                .ifPresent(e -> e.getStyle().setBackgroundColor(newVal ? this.SELECTED_COLOR : this.UNSELECTED_COLOR))
        );
        PropProvider childProps = new PropProvider();
        childProps.setString("label", label);
        childProps.setClickHandler("onClick", (e, m, x, y) -> onSelect.run());
        return childProps;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
        BoolProp selected = propProvider.getBoolProp("selected");
        if (selected.get()) {
            self.findChildById("IconThemeEntry").ifPresent(e -> e.getStyle().setBackgroundColor(this.SELECTED_COLOR));
        }
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
