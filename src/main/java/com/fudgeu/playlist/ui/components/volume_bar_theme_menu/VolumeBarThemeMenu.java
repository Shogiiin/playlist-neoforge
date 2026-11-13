package com.fudgeu.playlist.ui.components.volume_bar_theme_menu;

import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.VolumeBarThemeManager;
import com.fudgeu.playlist.client.ConfigManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import java.util.HashMap;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class VolumeBarThemeMenu implements ComponentBuilder {
    private final PlaylistClient playlistClient;
    private final FluxInstance fluxInstance;
    private final StateManager stateManager;
    private final ResourceLocation xmlFile = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/volume_bar_theme_menu/layout.xml");
    private final PropTemplate propTemplate;

    public VolumeBarThemeMenu() {
        this.playlistClient = PlaylistClient.instance;
        this.fluxInstance = FluxInstance.getInstance();
        this.stateManager = this.playlistClient.stateManager;
        this.propTemplate = new PropTemplate();
        this.propTemplate.requireRunnable("goBack");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        Runnable goBack = propProvider.getRunnable("goBack");
        PropProvider childProps = new PropProvider();
        childProps.setRunnable("exitScreen", goBack);
        return childProps;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
        VolumeBarThemeManager themeManager = this.playlistClient.volumeBarThemeManager;
        HashMap<String, BoolProp> selectedProps = new HashMap<>();
        List<Element> elements = themeManager.themes.keySet().stream().map(theme -> {
            PropProvider themeProps = new PropProvider();
            String themeId = theme.toString();
            selectedProps.put(themeId, new BoolProp(false));
            themeProps.setString("theme", themeId);
            themeProps.setRunnable("onSelect", () -> {
                ConfigManager.INSTANCE.volumeBarTheme.set(themeId);
                this.updateSelectedProps(selectedProps, themeManager);
            });
            themeProps.setBoolProp("selected", selectedProps.get(themeId));
            return this.fluxInstance.buildComponent("VolumeBarTheme", "VBTEntry", themeProps).get();
        }).toList();
        this.updateSelectedProps(selectedProps, themeManager);
        self.findChildById("VBTContent").ifPresent(e -> {
            if (e instanceof Container c) {
                elements.forEach(c::addChild);
            }
        });
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

    private void updateSelectedProps(HashMap<String, BoolProp> selectedProps, VolumeBarThemeManager themeManager) {
        for (String id : selectedProps.keySet()) {
            selectedProps.get(id).set(id.equals(themeManager.getCurrentThemeID().toString()));
        }
    }
}
