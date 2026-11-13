package com.fudgeu.playlist.ui.components.icon_theme_menu;

import com.fudgeu.playlist.client.ConfigManager;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import com.fudgeu.playlist.ui.IconTheme;
import com.fudgeu.playlist.ui.TextureManager;
import java.util.HashMap;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IconThemeMenu implements ComponentBuilder {
    private final ResourceLocation componentLocation = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/icon_theme_menu/layout.xml");
    private final PropTemplate propTemplate = new PropTemplate();

    public IconThemeMenu() {
        this.propTemplate.requireRunnable("goBack");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        PropProvider childProps = new PropProvider();
        Runnable goBack = propProvider.getRunnable("goBack");
        childProps.setRunnable("exitScreen", goBack);
        return childProps;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
        FluxInstance fluxInstance = FluxInstance.getInstance();
        String selectedTheme = ConfigManager.INSTANCE.iconTheme.get();
        Optional<Element> iconThemeContentOpt = self.findChildById("IconThemeContent");
        if (!iconThemeContentOpt.isEmpty()) {
            if (iconThemeContentOpt.get() instanceof Container iconThemeContent) {
                HashMap var14 = new HashMap();

                for (IconTheme theme : IconTheme.values()) {
                    BoolProp isSelected = new BoolProp(theme.commonName.equals(selectedTheme));
                    var14.put(theme, isSelected);
                    PropProvider props = new PropProvider();
                    props.setString("label", Component.translatable(theme.translationKey).getString());
                    props.setRunnable("onSelect", () -> this.onThemeSelect(theme, var14));
                    props.setBoolProp("selected", isSelected);
                    fluxInstance.buildComponent("IconThemeEntry", null, props).ifPresent(iconThemeContent::addChild);
                }
            }
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

    private void onThemeSelect(IconTheme selected, HashMap<IconTheme, BoolProp> selectionMap) {
        for (IconTheme theme : selectionMap.keySet()) {
            if (theme == selected) {
                selectionMap.get(theme).set(true);
                ConfigManager.INSTANCE.iconTheme.set(theme.commonName);
                TextureManager.INSTANCE.requestUpdate();
            } else {
                selectionMap.get(theme).set(false);
            }
        }
    }
}
