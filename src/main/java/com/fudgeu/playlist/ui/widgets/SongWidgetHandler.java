package com.fudgeu.playlist.ui.widgets;

import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.FluxWidget;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.transitions.QuickTransition;
import com.fudgeu.playlist.fluxui.transitions.TransitionHelper;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

public class SongWidgetHandler {
    public static SongWidgetHandler INSTANCE;
    private final Minecraft minecraftClient;
    private final ResourceLocation volumeWidgetLocation;
    private int lastSongWidget;
    private final BoolProp displayingVolume = new BoolProp(false);
    private UUID volumeWidgetTimerId;

    private SongWidgetHandler() {
        this.minecraftClient = Minecraft.getInstance();
        this.volumeWidgetLocation = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/song_widget/volume_widget/layout.xml");
    }

    public static SongWidgetHandler init() {
        return INSTANCE = new SongWidgetHandler();
    }

    public void displayVolumeWidget() {
        FluxInstance fluxInstance = FluxInstance.getInstance();
        TransitionHelper transitionHelper = fluxInstance.getTransitionHelper();
        FluxWidget lastWidget = null;
        Element lastWidgetElement = null;
        Optional<FluxWidget> lastWidgetOpt = WidgetHandler.INSTANCE.getWidget(this.lastSongWidget);
        if (lastWidgetOpt.isPresent()) {
            lastWidget = lastWidgetOpt.get();
            lastWidgetElement = lastWidget.getWidget();
        }

        if (lastWidgetElement != null && !this.displayingVolume.get()) {
            QuickTransition.slideOutHorizontal(lastWidgetElement, transitionHelper, 0, 8);
            int lastSongSaved = this.lastSongWidget;
            transitionHelper.setTimeout(() -> WidgetHandler.INSTANCE.deleteWidget(lastSongSaved), 8);
        }

        PropProvider props = new PropProvider();
        float currentVolume = this.minecraftClient.options.getSoundSourceVolume(SoundSource.MUSIC);
        props.setString("volumeLevel", this.floatToPercent(currentVolume));
        Optional<Element> volumeWidgetOpt = fluxInstance.loadElementFromXML(this.volumeWidgetLocation, props);
        if (!volumeWidgetOpt.isEmpty()) {
            Element volumeWidget = volumeWidgetOpt.get();
            if (lastWidgetElement != null && this.displayingVolume.get()) {
                lastWidget.setWidget(volumeWidget);
                transitionHelper.resetTimeout(this.volumeWidgetTimerId);
            } else {
                FluxWidget widget = new FluxWidget(volumeWidget, FluxWidget.AnchorPoint.BOTTOM_RIGHT_CORNER);
                int id = WidgetHandler.INSTANCE.addWidget(widget);
                this.lastSongWidget = id;
                QuickTransition.slideInHorizontal(volumeWidget, transitionHelper, 0, 8);
                this.displayingVolume.set(true);
                this.volumeWidgetTimerId = transitionHelper.setTimeout(() -> {
                    QuickTransition.fadeOut(widget.getWidget(), transitionHelper, 0.0F, 8);
                    transitionHelper.setTimeout(() -> WidgetHandler.INSTANCE.deleteWidget(id), 8);
                    this.displayingVolume.set(false);
                }, 175);
            }
        }
    }

    public void displayWidget(ResourceLocation songId, SongWidgetHandler.SongWidgetDisplayMode displayMode) {
        FluxInstance fluxInstance = FluxInstance.getInstance();
        StateManager stateManager = PlaylistClient.instance.stateManager;
        TransitionHelper transitionHelper = fluxInstance.getTransitionHelper();
        if (stateManager.getStateBool("showQuickPlayer")) {
            int oldWidgetOffset = 0;
            int newWidgetOffset = 0;
            switch (displayMode) {
                case SKIP:
                    oldWidgetOffset = -20;
                    newWidgetOffset = 20;
                    break;
                case REWIND:
                    oldWidgetOffset = 20;
                    newWidgetOffset = -20;
            }

            int finalOldWidgetOffset = oldWidgetOffset;
            WidgetHandler.INSTANCE.getWidget(this.lastSongWidget).ifPresent(widgetx -> {
                QuickTransition.slideOutHorizontal(widgetx.getWidget(), transitionHelper, finalOldWidgetOffset, 8);
                int lastSongSaved = this.lastSongWidget;
                transitionHelper.setTimeout(() -> WidgetHandler.INSTANCE.deleteWidget(lastSongSaved), 8);
            });
            Element widgetElement;
            if (songId != null) {
                PropProvider props = this.generateSongWidgetProps(songId, displayMode);
                Optional<Element> widgetOpt = fluxInstance.buildComponent("SongWidget", null, props);
                if (widgetOpt.isEmpty()) {
                    return;
                }

                widgetElement = widgetOpt.get();
            } else {
                Optional<Element> widgetOpt = fluxInstance.buildComponent("InterludeWidget", null, null);
                if (widgetOpt.isEmpty()) {
                    return;
                }

                widgetElement = widgetOpt.get();
            }

            FluxWidget widget = new FluxWidget(widgetElement, FluxWidget.AnchorPoint.BOTTOM_RIGHT_CORNER);
            int id = WidgetHandler.INSTANCE.addWidget(widget);
            this.lastSongWidget = id;
            QuickTransition.slideInHorizontal(widgetElement, transitionHelper, newWidgetOffset, 8);
            this.displayingVolume.set(false);
            transitionHelper.setTimeout(() -> {
                QuickTransition.fadeOut(widgetElement, transitionHelper, 0.0F, 8);
                transitionHelper.setTimeout(() -> WidgetHandler.INSTANCE.deleteWidget(id), 8);
            }, 175);
        }
    }

    private PropProvider generateSongWidgetProps(ResourceLocation songId, SongWidgetHandler.SongWidgetDisplayMode displayMode) {
        PropProvider props = new PropProvider();
        props.setString("songId", songId.toString());
        switch (displayMode) {
            case SKIP:
                props.setString("action", "skip");
                break;
            case REWIND:
                props.setString("action", "rewind");
                break;
            case PLAY:
                props.setString("action", "play");
        }

        return props;
    }

    private String floatToPercent(float volume) {
        volume = Math.round(volume * 100.0F);
        return (int)volume + "%";
    }

    public static enum SongWidgetDisplayMode {
        SKIP,
        REWIND,
        PLAY;
    }
}
