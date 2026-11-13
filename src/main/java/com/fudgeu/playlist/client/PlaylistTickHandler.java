package com.fudgeu.playlist.client;

import com.fudgeu.playlist.MusicManager;
import com.fudgeu.playlist.PlaylistManager;
import com.fudgeu.playlist.ToastManager;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.FluxScreen;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.ui.widgets.WidgetHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlaylistTickHandler {
    public static PlaylistTickHandler INSTANCE;
    public static final Logger LOGGER = LogManager.getLogger();
    private final PlaylistClient playlistClient;
    private final Minecraft minecraftClient;
    private MusicManager musicManager;
    private final PlaylistManager playlistManager;
    private ToastManager toastManager;
    private final KeyBindHandler keyBindHandler;
    private boolean isSaved = false;
    private boolean didPlayerExistLastTick = false;
    private boolean wasReadyLastTick = false;

    private PlaylistTickHandler() {
        this.playlistClient = PlaylistClient.instance;
        this.minecraftClient = Minecraft.getInstance();
        this.musicManager = this.playlistClient.musicManager;
        this.playlistManager = this.playlistClient.playlistManager;
        this.toastManager = this.playlistClient.toastManager;
        this.keyBindHandler = KeyBindHandler.INSTANCE;
    }

    public static PlaylistTickHandler init() {
        return INSTANCE = new PlaylistTickHandler();
    }

    public void handleGameTick(BoolProp isReady) {
        if (isReady.get() && !this.wasReadyLastTick) {
            this.musicManager = this.playlistClient.musicManager;
            this.toastManager = this.playlistClient.toastManager;
            this.wasReadyLastTick = true;
        }

        if (isReady.get()) {
            this.musicManager.tick();
            this.toastManager.tick();
            this.keyBindHandler.tick(this.musicManager);
        }

        this.checkGameMenuSwitch();
        this.checkIfSaveNeeded();
    }

    public void handleInGameRenderTick(GuiGraphics context, float delta) {
        if (!(this.minecraftClient.screen instanceof FluxScreen)) {
            FluxInstance.getInstance().tick(delta);
        }

        assert WidgetHandler.INSTANCE != null;

        WidgetHandler.INSTANCE.getWidgets().forEach((id, widget) -> {
            if (widget != null) {
                widget.render(context);
            }
        });
    }

    private void checkGameMenuSwitch() {
        if (this.didPlayerExistLastTick && this.minecraftClient.player == null) {
            this.didPlayerExistLastTick = false;
            this.musicManager.markNewDimension();
        } else if (!this.didPlayerExistLastTick && this.minecraftClient.player != null) {
            this.didPlayerExistLastTick = true;
            this.musicManager.markNewDimension();
        }
    }

    private void checkIfSaveNeeded() {
        if (this.minecraftClient.isPaused() && !this.isSaved) {
            LOGGER.info("Saving playlists...");
            this.playlistClient.playlistManager.saveAll();
            ConfigManager.saveConfig(this.playlistClient.stateManager);
            LOGGER.info("Playlists saved.");
            this.isSaved = true;
        }

        if (!this.minecraftClient.isPaused()) {
            this.isSaved = false;
        }
    }
}
