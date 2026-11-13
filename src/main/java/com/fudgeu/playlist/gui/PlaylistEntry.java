package com.fudgeu.playlist.gui;

import com.fudgeu.playlist.MusicManager;
import com.fudgeu.playlist.PlaylistManager;
import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.TextureManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.menu.ConfirmationPrompt;
import com.fudgeu.playlist.gui.screens.playlistoptions.PlaylistOptionsScreen;
import com.fudgeu.playlist.gui.widgets.ContextMenu;
import com.fudgeu.playlist.gui.widgets.ListWidget;
import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.RenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class PlaylistEntry extends ListWidget.Entry<PlaylistEntry> {
    private final int playlistId;
    private final String title;
    private final Minecraft mc;
    private final PlaylistScreen playlistScreen;
    private final PlaylistManager playlistManager;
    private final MusicManager musicManager;
    private final TextureManager textureManager;
    private final RGBA accent;

    public PlaylistEntry(PlaylistScreen playlistScreen, int playlistId) {
        this.playlistId = playlistId;
        this.title = PlaylistClient.instance.playlistManager.getRegisteredPlaylist(playlistId).getTitle();
        this.mc = Minecraft.getInstance();
        this.musicManager = PlaylistClient.instance.musicManager;
        this.playlistManager = PlaylistClient.instance.playlistManager;
        this.playlistScreen = playlistScreen;
        this.textureManager = PlaylistClient.instance.textureManager;
        StateManager stateManager = PlaylistClient.instance.stateManager;
        this.accent = RGBA.hex(stateManager.getStateInt("accentHex"));
    }

    @Override
    public void render(GuiGraphics context, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        context.drawString(this.mc.font, this.title, x + 10, y + (entryHeight / 2 - 9 / 2), 16777215, false);
        if (this.playlistManager.getDefaultPlaylist() == this.playlistId) {
            context.blit(this.textureManager.getTexture("default"), x + entryWidth - 16, y + entryHeight / 2 - 4, 0.0F, 0.0F, 8, 8, 8, 8);
        }

        if (this.musicManager.getCurrentPlaylist() == this.playlistId) {
            RenderUtils.drawRect(x, y + 5, x + 1, y + entryHeight - 5, this.accent.r, this.accent.g, this.accent.b, 1.0F);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.playlistScreen.setSelectedPlaylist(this.playlistId);
            this.playlistScreen.allSongsButton.overrideFocusedStatus(false);
            return true;
        } else if (button != 1) {
            return false;
        } else {
            ContextMenu contextMenu = PlaylistClient.instance.playlistScreenInstance.createContextMenu((int)mouseX, (int)mouseY);
            contextMenu.addOption(this.textureManager.getTexture("play"), Component.translatable("generic.play"), true, selfContextMenu -> {
                if (PlaylistClient.instance.musicManager.setCurrentPlaylist(this.playlistId)) {
                    PlaylistClient.instance.musicManager.skipWithInterlude();
                } else {
                    PlaylistClient.instance.toastManager.addToast(Component.translatable("toast.cannot_play_empty"), 60);
                }
            });
            contextMenu.addOption(
                this.textureManager.getTexture("settingsGear"),
                Component.translatable("generic.options"),
                true,
                onPress -> PlaylistClient.instance.playlistScreenInstance.setScreen(new PlaylistOptionsScreen(this.playlistScreen, this.playlistId))
            );
            boolean shouldDefaultBeEnabled = this.playlistManager.getDefaultPlaylist() != this.playlistId
                && this.playlistManager.getRegisteredPlaylist(this.playlistId).getSongCount() != 0;
            contextMenu.addOption(
                this.textureManager.getTexture("default"),
                Component.translatable("screen.playlist.make_default"),
                shouldDefaultBeEnabled,
                onPress -> this.playlistManager.setDefaultPlaylist(this.playlistId)
            );
            boolean shouldDeleteBeEnabled = this.playlistManager.getRegisteredPlaylists().size() != 1
                && this.playlistManager.getDefaultPlaylist() != this.playlistId;
            contextMenu.addOption(
                this.textureManager.getTexture("trash"),
                Component.translatable("generic.delete"),
                shouldDeleteBeEnabled,
                onPress -> PlaylistClient.instance
                    .playlistScreenInstance
                    .setScreen(
                        new ConfirmationPrompt(
                            this.playlistScreen,
                            Component.translatable("screen.delete_playlist.title"),
                            Component.translatable(
                                "screen.delete_playlist.confirmation",
                                new Object[]{PlaylistClient.instance.playlistManager.getRegisteredPlaylist(this.playlistId).getTitle()}
                            ),
                            () -> PlaylistClient.instance.playlistManager.removeRegisteredPlaylist(this.playlistId),
                            () -> {}
                        )
                    )
            );
            contextMenu.addCloseAction(onClose -> this.isSelfContextMenuOpen = false);
            contextMenu.build();
            this.isSelfContextMenuOpen = true;
            return true;
        }
    }

    public void setFocused(boolean focused) {
    }

    public boolean isFocused() {
        return false;
    }
}
