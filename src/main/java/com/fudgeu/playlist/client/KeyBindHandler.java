package com.fudgeu.playlist.client;

import com.fudgeu.playlist.MusicManager;
import com.fudgeu.playlist.gui.PlaylistScreen;
import com.fudgeu.playlist.ui.widgets.SongWidgetHandler;
import com.fudgeu.playlist.utils.GeneralUtils;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import com.mojang.blaze3d.platform.InputConstants.Type;

public class KeyBindHandler {
    public static KeyBindHandler INSTANCE;
    private final Minecraft minecraftClient = Minecraft.getInstance();
    private final PlaylistClient playlistClient = PlaylistClient.instance;
    private final SongWidgetHandler songWidgetHandler = SongWidgetHandler.INSTANCE;
    public KeyMapping guiOpenKeyBinding;
    public KeyMapping previousKeyBinding;
    public KeyMapping playStopKeyBinding;
    public KeyMapping forwardKeyBinding;
    public KeyMapping volumeUpKeyBinding;
    public KeyMapping volumeDownKeyBinding;

    private KeyBindHandler() {
    }

    public static KeyBindHandler init() {
        INSTANCE = new KeyBindHandler();
        INSTANCE.registerKeyBinds();
        return INSTANCE;
    }

    private void registerKeyBinds() {
        this.guiOpenKeyBinding = new KeyMapping("playlist.key.open_menu", Type.KEYSYM, 71, "playlist.category.playlist");
        this.previousKeyBinding = new KeyMapping("playlist.key.previous", Type.KEYSYM, 48, "playlist.category.playlist");
        this.playStopKeyBinding = new KeyMapping("playlist.key.play_stop", Type.KEYSYM, 45, "playlist.category.playlist");
        this.forwardKeyBinding = new KeyMapping("playlist.key.forward", Type.KEYSYM, 61, "playlist.category.playlist");
        this.volumeUpKeyBinding = new KeyMapping("playlist.key.volume_up", Type.KEYSYM, -1, "playlist.category.playlist");
        this.volumeDownKeyBinding = new KeyMapping("playlist.key.volume_down", Type.KEYSYM, -1, "playlist.category.playlist");
    }

    public void register(RegisterKeyMappingsEvent event) {
        event.register(this.guiOpenKeyBinding);
        event.register(this.previousKeyBinding);
        event.register(this.playStopKeyBinding);
        event.register(this.forwardKeyBinding);
        event.register(this.volumeUpKeyBinding);
        event.register(this.volumeDownKeyBinding);
    }

    public void tick(MusicManager musicManager) {
        while (this.guiOpenKeyBinding.consumeClick()) {
            this.playlistClient.playlistScreenInstance = new PlaylistScreen();
            this.minecraftClient.setScreen(this.playlistClient.playlistScreenInstance);
        }

        while (this.previousKeyBinding.consumeClick()) {
            musicManager.previous();
            this.songWidgetHandler.displayWidget(musicManager.getCurrentSongId(), SongWidgetHandler.SongWidgetDisplayMode.REWIND);
        }

        while (this.playStopKeyBinding.consumeClick()) {
            if (musicManager.isMusicStopped()) {
                musicManager.playMusic();
                this.songWidgetHandler.displayWidget(musicManager.getCurrentSongId(), SongWidgetHandler.SongWidgetDisplayMode.PLAY);
                break;
            }

            musicManager.stopMusic();
        }

        while (this.forwardKeyBinding.consumeClick()) {
            musicManager.skip();
            this.songWidgetHandler.displayWidget(musicManager.getCurrentSongId(), SongWidgetHandler.SongWidgetDisplayMode.SKIP);
        }

        while (this.volumeUpKeyBinding.consumeClick()) {
            float newVolume = Math.min(this.minecraftClient.options.getSoundSourceVolume(SoundSource.MUSIC) + 0.15F, 1.0F);
            GeneralUtils.changeVolume(newVolume);
            this.songWidgetHandler.displayVolumeWidget();
        }

        while (this.volumeDownKeyBinding.consumeClick()) {
            float newVolume = Math.max(this.minecraftClient.options.getSoundSourceVolume(SoundSource.MUSIC) - 0.15F, 0.0F);
            GeneralUtils.changeVolume(newVolume);
            this.songWidgetHandler.displayVolumeWidget();
        }
    }
}
