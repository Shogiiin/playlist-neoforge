package com.fudgeu.playlist.gui.widgets;

import com.fudgeu.playlist.MusicManager;
import com.fudgeu.playlist.PlaylistManager;
import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.objects.SongObj;
import com.fudgeu.playlist.utils.GeneralUtils;
import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Port addition: album browser shown in the Playlist screen's content area (the sidebar stays visible).
 * Grid view shows every album cover; clicking one opens its track list.
 */
public class AlbumBrowser extends AbstractWidget {
    private static final int TILE_COVER = 80;
    private static final int TILE_WIDTH = 96;
    private static final int TILE_HEIGHT = TILE_COVER + 34;
    private static final int CONTENT_TOP = 50;
    private static final int DETAIL_COVER = 96;
    private static final int TRACK_ROW_HEIGHT = 22;

    private static final String MUSIC_DISCS = "Music Discs";
    private static final List<String> RELEASE_ORDER = List.of(
        "Volume Alpha", "Volume Beta", "Dragon Fish", "Shuniji", "Axolotl", "Nether Update", "Caves & Cliffs",
        "Wild Update", "Trails & Tales", "Tricky Trials", "Non-Album Tracks"
    );
    private static final List<String> DISC_ORDER = List.of(
        "13", "cat", "blocks", "chirp", "far", "mall", "mellohi", "stal", "strad", "ward", "11", "wait",
        "otherside", "5", "pigstep", "relic", "creator", "creator_music_box", "precipice"
    );
    /** Number of tracks on the official soundtrack release (some are not in the game). */
    private static final Map<String, Integer> OFFICIAL_TRACK_COUNTS = Map.of(
        "Volume Alpha", 24, "Volume Beta", 30, "Nether Update", 5, "Caves & Cliffs", 10,
        "Wild Update", 4, "Trails & Tales", 5, "Tricky Trials", 12
    );
    private static final ResourceLocation DISC_COVER = ResourceLocation.withDefaultNamespace("textures/item/music_disc_13.png");

    private final Runnable onLayoutChange;
    private final Font font = Minecraft.getInstance().font;
    private final MusicManager musicManager = PlaylistClient.instance.musicManager;
    private final PlaylistManager playlistManager = PlaylistClient.instance.playlistManager;
    private final StateManager stateManager = PlaylistClient.instance.stateManager;
    private final Map<String, List<ResourceLocation>> albums = new LinkedHashMap<>();
    private String openAlbum = null;
    private double scroll = 0.0;

    /** @param onLayoutChange called when the view switches between grid and album, so the screen can rebuild its buttons. */
    public AlbumBrowser(Runnable onLayoutChange) {
        super(0, 0, 0, 0, Component.translatable("screen.playlist.albums"));
        this.onLayoutChange = onLayoutChange;

        // Group songs by album; albums in release order (unknown ones alphabetically after), songs by official track number.
        Map<String, List<ResourceLocation>> grouped = new TreeMap<>((a, b) -> {
            int ia = RELEASE_ORDER.indexOf(a);
            int ib = RELEASE_ORDER.indexOf(b);
            if (ia != ib) {
                return Integer.compare(ia == -1 ? Integer.MAX_VALUE : ia, ib == -1 ? Integer.MAX_VALUE : ib);
            }
            return a.compareToIgnoreCase(b);
        });
        List<ResourceLocation> discs = new ArrayList<>();
        for (ResourceLocation id : this.playlistManager.getRegisteredSongs()) {
            SongObj song = this.playlistManager.getRegisteredSongObj(id);
            grouped.computeIfAbsent(song.getAlbum(), k -> new ArrayList<>()).add(id);
            if ("Music Disc".equals(song.getType())) {
                discs.add(id);
            }
        }
        for (List<ResourceLocation> songs : grouped.values()) {
            songs.sort(Comparator.comparingInt(id -> {
                int track = this.playlistManager.getRegisteredSongObj(id).getTrackNumber();
                return track > 0 ? track : Integer.MAX_VALUE;
            }));
        }
        this.albums.putAll(grouped);

        // Virtual collection of every music disc, in creative inventory order.
        discs.sort(Comparator.comparingInt(id -> {
            String stem = id.getPath().substring(id.getPath().lastIndexOf('.') + 1);
            int index = DISC_ORDER.indexOf(stem);
            return index == -1 ? Integer.MAX_VALUE : index;
        }));
        if (!discs.isEmpty()) {
            this.albums.put(MUSIC_DISCS, discs);
        }
    }

    public void setBounds(int left, int width, int height) {
        this.setX(left);
        this.setY(0);
        this.setWidth(width);
        this.setHeight(height);
        this.scroll = Math.min(this.scroll, this.maxScroll());
    }

    /** Buttons for the current view; the owning screen adds them as widgets after (re)building. */
    public List<TransparentButton> createButtons() {
        List<TransparentButton> buttons = new ArrayList<>();
        if (this.openAlbum == null) {
            return buttons;
        }
        RGBA accent = RGBA.hex(this.stateManager.getStateInt("accentHex"));

        TransparentButton back = new TransparentButton(this.getX() + 15, 15, 20, 20, Component.literal("<"), b -> this.showGrid());
        back.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
        buttons.add(back);

        int textX = this.detailTextX();
        int buttonsY = CONTENT_TOP + 58;
        TransparentButton play = new TransparentButton(textX, buttonsY, 70, 20, Component.translatable("generic.play"), b -> this.playAlbum(false));
        play.setIcon(PlaylistClient.instance.textureManager.getTexture("play"));
        play.setDisplayMode(TransparentButton.DisplayMode.ICON_AND_TEXT);
        play.setDefaultColor(accent);
        play.setHoveredColor(accent.lighter());
        buttons.add(play);

        TransparentButton shuffle = new TransparentButton(textX + 75, buttonsY, 70, 20, Component.translatable("screen.playlist.shuffle"), b -> this.playAlbum(true));
        shuffle.setIcon(PlaylistClient.instance.textureManager.getTexture("categoryBased"));
        shuffle.setDisplayMode(TransparentButton.DisplayMode.ICON_AND_TEXT);
        buttons.add(shuffle);
        return buttons;
    }

    // ---------------------------------------------------------------- navigation & playback

    public boolean isAlbumOpen() {
        return this.openAlbum != null;
    }

    public void showGrid() {
        this.openAlbum = null;
        this.scroll = 0.0;
        this.onLayoutChange.run();
    }

    private void openAlbum(String album) {
        this.openAlbum = album;
        this.scroll = 0.0;
        this.onLayoutChange.run();
    }

    private void playAlbum(boolean shuffle) {
        List<ResourceLocation> songs = new ArrayList<>(this.albums.get(this.openAlbum));
        if (shuffle) {
            Collections.shuffle(songs);
        }
        this.musicManager.playSongList(songs);
    }

    private void playFromTrack(int index) {
        List<ResourceLocation> songs = this.albums.get(this.openAlbum);
        this.musicManager.playSongList(songs.subList(index, songs.size()));
    }

    // ---------------------------------------------------------------- layout helpers

    private int right() {
        return this.getX() + this.width;
    }

    private int bottom() {
        return this.getY() + this.height - 10;
    }

    private int detailTextX() {
        return this.getX() + 20 + DETAIL_COVER + 15;
    }

    private int columns() {
        return Math.max(1, (this.width - 20) / TILE_WIDTH);
    }

    private int gridLeft() {
        return this.getX() + (this.width - this.columns() * TILE_WIDTH) / 2;
    }

    private int trackListTop() {
        return CONTENT_TOP + DETAIL_COVER + 15;
    }

    private int viewportTop() {
        return this.openAlbum == null ? CONTENT_TOP : this.trackListTop();
    }

    private int contentHeight() {
        if (this.openAlbum == null) {
            return (this.albums.size() + this.columns() - 1) / this.columns() * TILE_HEIGHT;
        }
        return this.albums.get(this.openAlbum).size() * TRACK_ROW_HEIGHT;
    }

    private double maxScroll() {
        return Math.max(0, this.contentHeight() - (this.bottom() - this.viewportTop()));
    }

    /** Index of the album tile / track row under the mouse, or -1. */
    private int hitIndex(double mouseX, double mouseY) {
        int top = this.viewportTop();
        if (mouseY < top || mouseY > this.bottom()) {
            return -1;
        }
        double y = mouseY - top + this.scroll;
        if (this.openAlbum == null) {
            if (mouseX < this.gridLeft()) {
                return -1;
            }
            int col = (int) ((mouseX - this.gridLeft()) / TILE_WIDTH);
            if (col >= this.columns()) {
                return -1;
            }
            int index = (int) (y / TILE_HEIGHT) * this.columns() + col;
            return index < this.albums.size() ? index : -1;
        }
        if (mouseX < this.getX() + 20 || mouseX > this.right() - 20) {
            return -1;
        }
        int index = (int) (y / TRACK_ROW_HEIGHT);
        return index < this.albums.get(this.openAlbum).size() ? index : -1;
    }

    private String fit(String text, int maxWidth) {
        if (this.font.width(text) <= maxWidth) {
            return text;
        }
        return this.font.plainSubstrByWidth(text, Math.max(0, maxWidth - this.font.width("..."))) + "...";
    }

    private Component songCount(int count) {
        return Component.translatable(count == 1 ? "screen.playlist.album_song" : "screen.playlist.album_songs", count).withStyle(ChatFormatting.ITALIC);
    }

    private void drawCover(GuiGraphics context, String album, int x, int y, int size) {
        if (MUSIC_DISCS.equals(album)) {
            RenderUtils.drawRect(x, y, x + size, y + size, 0.12F, 0.12F, 0.12F, 1.0F);
            RenderSystem.enableBlend();
            int inset = size / 8;
            int inner = size - 2 * inset;
            context.blit(DISC_COVER, x + inset, y + inset, 0.0F, 0.0F, inner, inner, inner, inner);
            RenderSystem.disableBlend();
        } else {
            context.blit(GeneralUtils.matchUpAlbumToArt(album), x, y, 0.0F, 0.0F, size, size, size, size);
        }
    }

    private String currentAlbum() {
        ResourceLocation id = this.musicManager.getCurrentSongId();
        SongObj song = id != null ? this.playlistManager.getRegisteredSongObj(id) : null;
        return song != null ? song.getAlbum() : null;
    }

    // ---------------------------------------------------------------- rendering

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.openAlbum == null) {
            this.renderGrid(context, mouseX, mouseY);
        } else {
            this.renderAlbum(context, mouseX, mouseY);
        }
    }

    private void renderGrid(GuiGraphics context, int mouseX, int mouseY) {
        int headerX = this.getX() + 20;
        context.drawString(this.font, this.getMessage(), headerX, 21, 0xFFFFFF, false);
        context.drawString(this.font, Component.translatable("screen.playlist.album_count", this.albums.size()).withStyle(ChatFormatting.ITALIC),
            headerX + this.font.width(this.getMessage()) + 7, 21, 0xAAAAAA, false);

        int hovered = this.hitIndex(mouseX, mouseY);
        String playingAlbum = this.currentAlbum();
        RGBA accent = RGBA.hex(this.stateManager.getStateInt("accentHex"));

        context.enableScissor(this.getX(), CONTENT_TOP, this.right(), this.bottom());
        int i = 0;
        for (Map.Entry<String, List<ResourceLocation>> album : this.albums.entrySet()) {
            int x = this.gridLeft() + (i % this.columns()) * TILE_WIDTH;
            int y = CONTENT_TOP + (i / this.columns()) * TILE_HEIGHT - (int) this.scroll;
            if (y + TILE_HEIGHT >= CONTENT_TOP && y <= this.bottom()) {
                int coverX = x + (TILE_WIDTH - TILE_COVER) / 2;
                if (i == hovered) {
                    RenderUtils.drawRect(x + 2, y, x + TILE_WIDTH - 2, y + TILE_HEIGHT - 4, new RGBA(1.0F, 0.08F));
                }
                this.drawCover(context, album.getKey(), coverX, y + 4, TILE_COVER);
                if (album.getKey().equals(playingAlbum)) {
                    RenderUtils.drawRect(coverX, y + 4 + TILE_COVER, coverX + TILE_COVER, y + 6 + TILE_COVER, accent);
                }
                context.drawString(this.font, this.fit(album.getKey(), TILE_WIDTH - 8), coverX, y + TILE_COVER + 9, 0xFFFFFF, false);
                context.drawString(this.font, this.songCount(album.getValue().size()), coverX, y + TILE_COVER + 20, 0xAAAAAA, false);
            }
            i++;
        }
        context.disableScissor();
    }

    private void renderAlbum(GuiGraphics context, int mouseX, int mouseY) {
        List<ResourceLocation> songs = this.albums.get(this.openAlbum);
        boolean collection = MUSIC_DISCS.equals(this.openAlbum);
        RGBA accent = RGBA.hex(this.stateManager.getStateInt("accentHex"));
        int textX = this.detailTextX();
        int textMax = this.right() - textX - 20;

        context.drawString(this.font, this.getMessage(), this.getX() + 40, 21, 0xAAAAAA, false);
        this.drawCover(context, this.openAlbum, this.getX() + 20, CONTENT_TOP, DETAIL_COVER);

        context.pose().pushPose();
        context.pose().scale(1.5F, 1.5F, 1.5F);
        context.drawString(this.font, this.fit(this.openAlbum, (int) (textMax / 1.5F)), (int) (textX / 1.5F), (int) ((CONTENT_TOP + 4) / 1.5F), 0xFFFFFF, false);
        context.pose().popPose();

        LinkedHashSet<String> artists = new LinkedHashSet<>();
        for (ResourceLocation id : songs) {
            artists.add(this.playlistManager.getRegisteredSongObj(id).getArtist());
        }
        context.drawString(this.font, this.fit(String.join(", ", artists), textMax), textX, CONTENT_TOP + 24, 0xAAAAAA, false);
        Integer official = OFFICIAL_TRACK_COUNTS.get(this.openAlbum);
        Component count = official != null && official != songs.size()
            ? Component.translatable("screen.playlist.album_tracks_in_game", songs.size(), official).withStyle(ChatFormatting.ITALIC)
            : this.songCount(songs.size());
        context.drawString(this.font, count, textX, CONTENT_TOP + 37, 0xAAAAAA, false);

        int top = this.trackListTop();
        int rowLeft = this.getX() + 20;
        int rowRight = this.right() - 20;
        RenderUtils.drawRect(rowLeft, top - 6, rowRight, top - 5, 0.24F, 0.24F, 0.24F, 1.0F);
        int hovered = this.hitIndex(mouseX, mouseY);
        ResourceLocation playing = this.musicManager.getCurrentSongId();
        int artistX = rowLeft + Math.max(170, (rowRight - rowLeft) / 2);
        int titleColor = ((int) (accent.r * 255) << 16) | ((int) (accent.g * 255) << 8) | (int) (accent.b * 255);

        context.enableScissor(this.getX(), top, this.right(), this.bottom());
        for (int i = 0; i < songs.size(); i++) {
            int y = top + i * TRACK_ROW_HEIGHT - (int) this.scroll;
            if (y + TRACK_ROW_HEIGHT < top || y > this.bottom()) {
                continue;
            }
            SongObj song = this.playlistManager.getRegisteredSongObj(songs.get(i));
            boolean isPlaying = songs.get(i).equals(playing);
            if (i == hovered) {
                RenderUtils.drawRect(rowLeft, y, rowRight, y + TRACK_ROW_HEIGHT, new RGBA(1.0F, 0.08F));
            }
            if (isPlaying) {
                RenderUtils.drawRect(rowLeft, y + 3, rowLeft + 2, y + TRACK_ROW_HEIGHT - 3, accent);
            }
            int textY = y + (TRACK_ROW_HEIGHT - 8) / 2;
            String number = collection ? String.valueOf(i + 1) : (song.getTrackNumber() > 0 ? String.valueOf(song.getTrackNumber()) : "-");
            context.drawString(this.font, number, rowLeft + 10, textY, 0x888888, false);
            context.drawString(this.font, this.fit(song.getTitle(), artistX - rowLeft - 42), rowLeft + 32, textY, isPlaying ? titleColor : 0xFFFFFF, false);
            String detail = collection
                ? song.getArtist() + " - " + song.getAlbum()
                : song.getArtist() + ("Music Disc".equals(song.getType()) ? " (Music Disc)" : "");
            context.drawString(this.font, this.fit(detail, rowRight - artistX - 10), artistX, textY, 0xAAAAAA, false);
        }
        context.disableScissor();
    }

    // ---------------------------------------------------------------- input

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible || button != 0 || !this.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        int index = this.hitIndex(mouseX, mouseY);
        if (index == -1) {
            return false;
        }
        if (this.openAlbum == null) {
            this.openAlbum(new ArrayList<>(this.albums.keySet()).get(index));
        } else {
            this.playFromTrack(index);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scroll = Math.max(0.0, Math.min(this.maxScroll(), this.scroll - scrollY * 20.0));
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
