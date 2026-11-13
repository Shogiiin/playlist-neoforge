package com.fudgeu.playlist.utils;

import java.util.ArrayList;

public class VolumeBarTheme {
    public ArrayList<Wave> waves = new ArrayList<>();

    public void addWave(float position, RGBA color) {
        this.waves.add(new Wave(position, color));
    }
}
