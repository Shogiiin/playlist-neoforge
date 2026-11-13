package com.fudgeu.playlist;

import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.objects.PlaylistObj;
import com.fudgeu.playlist.utils.Mode;
import com.fudgeu.playlist.utils.PlayMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.client.gui.screens.TitleScreen;

public class MusicManager {
    private final Minecraft mc;
    private final ConditionManager conditionManager;
    private PlaylistManager playlistManager;
    private final StateManager stateManager;
    private final PlayMode playMode;
    private final List<ResourceLocation> songHistory = new ArrayList<>();
    private final List<ResourceLocation> songQueue = new ArrayList<>();
    private int currentPlaylist;
    private ResourceLocation currentSongId;
    private SoundInstance currentSong;
    private int timeUntilNextSong;
    private int initialTimerValue;
    public boolean timerRunning = false;
    private boolean isMusicStopped = false;
    private boolean newDimension = true;
    private boolean isMuted = false;
    private float savedVolume = 1.0F;
    // Port addition: while true, queued songs play back-to-back (album playback) instead of waiting for the interlude timer.
    private boolean continuousQueue = false;

    public MusicManager() {
        this.mc = Minecraft.getInstance();
        this.conditionManager = PlaylistClient.instance.conditionManager;
        this.playlistManager = PlaylistClient.instance.playlistManager;
        this.stateManager = PlaylistClient.instance.stateManager;
        this.playMode = PlaylistClient.instance.playMode;
        this.currentPlaylist = this.playlistManager.getSafeDefaultPlaylist();
    }

    public void tick() {
        String activeCondition = this.conditionManager.checkActiveConditions();
        if (activeCondition != null && !this.isMusicStopped) {
            this.timerRunning = false;
            this.playSongNow(this.getNextSong(activeCondition));
        } else {
            if (this.currentSongId != null && !this.mc.getSoundManager().isActive(this.currentSong) && !this.isMusicStopped) {
                this.currentSongId = null;
                if (this.continuousQueue && !this.songQueue.isEmpty()) {
                    this.playSong(this.getNextSong());
                } else {
                    this.continuousQueue = false;
                    this.scheduleNextSong();
                }
            }

            if (this.currentSongId == null && !this.timerRunning && !this.isMusicStopped) {
                this.scheduleNextSong();
            }

            if (this.timerRunning && this.timeUntilNextSong-- <= 0) {
                this.timerRunning = false;
                this.playSong(this.getNextSong());
            }

            if (this.newDimension) {
                this.scheduleNextSong();
            }
        }
    }

    private void playSong(ResourceLocation id) {
        this.newDimension = false;
        this.isMusicStopped = false;
        this.currentSongId = id;
        this.currentSong = SimpleSoundInstance.forMusic(
            (SoundEvent)PlaylistClient.instance.playlistManager.getRegisteredSongObj(id).getMusicSound().getEvent().value()
        );
        this.mc.getSoundManager().play(this.currentSong);
        this.songHistory.add(id);
        this.stateManager.setStateString("currentSongCategory", this.stateManager.getStateString("lastSelectedCategory"));
        this.stateManager.setStateString("currentSongCondition", this.stateManager.getStateString("lastSelectedCategory"));
    }

    public boolean setCurrentPlaylist(int id) {
        if (PlaylistClient.instance.playlistManager.getRegisteredPlaylist(id).getSongCount() != 0) {
            this.currentPlaylist = id;
            this.clearSongList();
            return true;
        } else {
            return false;
        }
    }

    private ResourceLocation getNextSong() {
        Mode mode = this.playMode.getPlayMode();
        if (mode == Mode.REPEAT || mode == Mode.REPEAT_ONCE) {
            if (mode == Mode.REPEAT_ONCE) {
                this.playMode.set(Mode.CATEGORY_BASED);
            }

            if (this.songHistory.size() >= 1) {
                return this.songHistory.get(this.songHistory.size() - 1);
            }
        }

        if (this.songQueue.size() > 0) {
            ResourceLocation nextSong = this.songQueue.get(0);
            this.songQueue.remove(0);
            return nextSong;
        } else {
            return PlaylistClient.instance.playlistManager.getRegisteredPlaylist(this.currentPlaylist).getRandomSongBasedOnConditions();
        }
    }

    private ResourceLocation getNextSong(String conditionId) {
        return this.playlistManager.getRegisteredPlaylist(this.currentPlaylist).getRandomSongBasedOn(conditionId);
    }

    private void scheduleNextSong() {
        this.playlistManager = PlaylistClient.instance.playlistManager;
        if (!this.playlistManager.getRegisteredPlaylists().contains(this.currentPlaylist)) {
            this.currentPlaylist = this.playlistManager.getSafeDefaultPlaylist();
        }

        PlaylistObj playlist = PlaylistClient.instance.playlistManager.getRegisteredPlaylist(this.currentPlaylist);
        Mode mode = this.playMode.getPlayMode();
        if (mode != Mode.REPEAT && mode != Mode.REPEAT_ONCE) {
            Random random = new Random();
            int timeVariance = 0;
            int timeBetweenSongs;
            if (this.newDimension) {
                if (this.mc.screen instanceof TitleScreen) {
                    timeBetweenSongs = playlist.getMenuLeadTime();
                } else {
                    timeBetweenSongs = playlist.getGameLeadTime();
                }
            } else if (this.mc.screen instanceof TitleScreen) {
                timeBetweenSongs = playlist.getMenuTimeBetweenSongs();
            } else {
                timeBetweenSongs = playlist.getTimeBetweenSongs();
                timeVariance = playlist.getTimeVariance();
            }

            this.timeUntilNextSong = this.initialTimerValue = 20
                * (timeBetweenSongs + random.nextInt((timeVariance + 1) * 2) - timeVariance + (this.newDimension ? 1 : 0));
            this.newDimension = false;
            this.timerRunning = true;
        } else {
            this.timeUntilNextSong = 0;
            this.newDimension = false;
            this.timerRunning = true;
        }
    }

    /**
     * Port addition: plays the given songs in order, back-to-back (used for album playback).
     * Afterwards, playback returns to the current playlist as usual.
     */
    public void playSongList(List<ResourceLocation> songs) {
        if (songs.isEmpty()) {
            return;
        }

        if (this.currentPlaylist == -2) {
            this.currentPlaylist = this.playlistManager.getSafeDefaultPlaylist();
        }

        this.songQueue.clear();
        this.songQueue.addAll(songs.subList(1, songs.size()));
        this.continuousQueue = true;
        this.playSongNow(songs.get(0));
    }

    public void clearSongList() {
        if (this.continuousQueue) {
            this.songQueue.clear();
            this.continuousQueue = false;
        }
    }

    public boolean isPlayingSongList() {
        return this.continuousQueue;
    }

    public void addSongToQueue(ResourceLocation id) {
        this.songQueue.add(id);
    }

    public void playSongNow(ResourceLocation id) {
        this.isMusicStopped = false;
        this.mc.getSoundManager().stop(this.currentSongId, SoundSource.MUSIC);
        this.timerRunning = false;
        this.playSong(id);
    }

    public void skip() {
        if (this.currentPlaylist == -2) {
            this.currentPlaylist = this.playlistManager.getSafeDefaultPlaylist();
        }

        this.playSongNow(this.getNextSong());
    }

    public void skipWithInterlude() {
        if (this.currentPlaylist == -2) {
            this.currentPlaylist = this.playlistManager.getSafeDefaultPlaylist();
        }

        if (!this.isMusicStopped && this.stateManager.getStateBool("skipInterlude")) {
            this.playSongNow(this.getNextSong());
        } else {
            this.isMusicStopped = false;
            this.mc.getSoundManager().stop(this.currentSongId, SoundSource.MUSIC);
            this.currentSongId = null;
            this.scheduleNextSong();
        }
    }

    public void previous() {
        if (this.songHistory.size() > 1) {
            this.playSongNow(this.songHistory.get(this.songHistory.size() - 2));
            this.songHistory.remove(this.songHistory.size() - 2);
            this.songHistory.remove(this.songHistory.size() - 2);
        } else if (this.currentSong != null) {
            this.playSongNow(this.currentSongId);
        }
    }

    public void stopMusic() {
        if (this.currentSong != null) {
            this.mc.getSoundManager().stop(ResourceLocation.parse("minecraft:music.credits"), SoundSource.MUSIC);
            this.mc.getSoundManager().stop(this.currentSongId, SoundSource.MUSIC);
            this.currentSong = null;
        }

        this.currentSongId = null;
        this.clearSongList();
        this.isMusicStopped = true;
        this.currentPlaylist = -2;
        this.timerRunning = false;
        this.timeUntilNextSong = 0;
    }

    public void playMusic() {
        if (this.isMusicStopped) {
            this.isMusicStopped = false;
            this.currentPlaylist = this.playlistManager.getSafeDefaultPlaylist();
            if (this.stateManager.getStateBool("skipInterlude")) {
                this.skip();
            }
        }
    }

    public void markNewDimension() {
        this.newDimension = true;
    }

    public boolean isMusicStopped() {
        return this.isMusicStopped;
    }

    public int getCurrentPlaylist() {
        return this.currentPlaylist;
    }

    public ResourceLocation getCurrentSongId() {
        return this.currentSongId;
    }

    public boolean isInInterlude() {
        return this.timerRunning;
    }

    public boolean toggleMute() {
        this.isMuted = !this.isMuted;
        if (this.isMuted) {
            this.savedVolume = this.mc.options.getSoundSourceVolume(SoundSource.MUSIC);
            this.mc.getSoundManager().updateSourceVolume(SoundSource.MUSIC, 0.0F);
            this.stopMusic();
            return true;
        } else {
            this.mc.getSoundManager().updateSourceVolume(SoundSource.MUSIC, this.savedVolume);
            return false;
        }
    }

    public int getTimeUntilNextSong() {
        return this.timeUntilNextSong;
    }

    public int getInitialTimerValue() {
        return this.initialTimerValue;
    }
}
