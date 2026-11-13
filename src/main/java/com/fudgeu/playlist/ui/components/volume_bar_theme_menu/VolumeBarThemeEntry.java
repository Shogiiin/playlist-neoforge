package com.fudgeu.playlist.ui.components.volume_bar_theme_menu;

import com.fudgeu.playlist.VolumeBarThemeManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import com.fudgeu.playlist.fluxui.style.color.Color;
import com.fudgeu.playlist.fluxui.style.color.SolidColor;
import com.fudgeu.playlist.fluxui.style.sizing.Sizing;
import com.fudgeu.playlist.utils.VolumeBarTheme;
import com.fudgeu.playlist.utils.Wave;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class VolumeBarThemeEntry implements ComponentBuilder {
    private final FluxInstance fluxInstance;
    private final ResourceLocation xmlFile = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/volume_bar_theme_menu/entry/layout.xml");
    private final PropTemplate propTemplate;
    private final SolidColor SELECTED_COLOR = SolidColor.create(Color.fromHex(855309));
    private final SolidColor UNSELECTED_COLOR = SolidColor.TRANSPARENT;

    public VolumeBarThemeEntry() {
        this.fluxInstance = FluxInstance.getInstance();
        this.propTemplate = new PropTemplate();
        this.propTemplate.requireString("theme");
        this.propTemplate.requireRunnable("onSelect");
        this.propTemplate.requireBoolProp("selected");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        String themeLabel = propProvider.getString("theme");
        Runnable onSelect = propProvider.getRunnable("onSelect");
        BoolProp selected = propProvider.getBoolProp("selected");
        selected.addUpdateListener(
            (oldValue, newValue) -> self.findChildById("VolumeBarThemeEntry")
                .ifPresent(e -> e.getStyle().setBackgroundColor(newValue ? this.SELECTED_COLOR : this.UNSELECTED_COLOR))
        );
        PropProvider childProps = new PropProvider();
        childProps.setString("themeLabel", Component.translatable(ResourceLocation.parse(themeLabel).getPath()).getString());
        childProps.setClickHandler("onClick", (e, m, x, y) -> onSelect.run());
        self.getStyle().setWidth(Sizing.percent(1.0F));
        return childProps;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
        PlaylistClient playlistClient = PlaylistClient.instance;
        VolumeBarThemeManager themeManager = playlistClient.volumeBarThemeManager;
        String themeId = propProvider.getString("theme");
        Optional<VolumeBarTheme> themeOpt = themeManager.getTheme(ResourceLocation.parse(themeId));
        if (!themeOpt.isEmpty()) {
            VolumeBarTheme theme = themeOpt.get();
            Optional<Element> elementOpt = self.findChildById("ThemePreview");
            if (!elementOpt.isEmpty() && elementOpt.get() instanceof Container c) {
                float var18 = 1.0F;
                float multiplier = (theme.waves.size() - 1.0F) / theme.waves.size();

                for (int i = theme.waves.size() - 1; i >= 0; i--) {
                    Wave wave = theme.waves.get(i);
                    float waveSize;
                    if (i != 0) {
                        float nextWavePos = theme.waves.get(i - 1).position;
                        waveSize = (wave.position - nextWavePos) * multiplier;
                        var18 -= waveSize;
                    } else {
                        waveSize = var18;
                    }

                    Optional<Element> waveContainerOpt = this.fluxInstance.buildElement("Container", "Wave" + i, new PropProvider());
                    if (waveContainerOpt.isEmpty() || !(waveContainerOpt.get() instanceof Container waveContainer)) {
                        return;
                    }

                    Color var20 = Color.create(wave.color.r, wave.color.g, wave.color.b);
                    waveContainer.getStyle().setBackgroundColor(SolidColor.create(var20));
                    waveContainer.getStyle().setWidth(Sizing.percent(waveSize));
                    waveContainer.getStyle().setHeight(Sizing.px(2));
                    c.addChild(waveContainer);
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
        return this.xmlFile;
    }
}
