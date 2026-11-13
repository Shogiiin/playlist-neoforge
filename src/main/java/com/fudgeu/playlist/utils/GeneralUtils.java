package com.fudgeu.playlist.utils;

import com.fudgeu.playlist.MusicManager;
import com.fudgeu.playlist.client.PlaylistClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

public class GeneralUtils {
    public static String defaultTo(String input, String defaultTo) {
        return input != null && !input.equals("") ? input : defaultTo;
    }

    public static ResourceLocation matchUpAlbumToArt(String album) {
        return switch (album) {
            case "Volume Alpha" -> ResourceLocation.fromNamespaceAndPath("playlist", "textures/album_art/volumealpha.png");
            // "Boss" and "11" were never released on an album; show them with the Volume Beta cover.
            case "Volume Beta", "Non-Album Tracks" -> ResourceLocation.fromNamespaceAndPath("playlist", "textures/album_art/volumebeta.png");
            case "Nether Update" -> ResourceLocation.fromNamespaceAndPath("playlist", "textures/album_art/netherupdate.png");
            case "Caves & Cliffs" -> ResourceLocation.fromNamespaceAndPath("playlist", "textures/album_art/cavesandcliffs.png");
            case "Axolotl" -> ResourceLocation.fromNamespaceAndPath("playlist", "textures/album_art/axolotl.png");
            case "Dragon Fish" -> ResourceLocation.fromNamespaceAndPath("playlist", "textures/album_art/dragonfish.png");
            case "Shuniji" -> ResourceLocation.fromNamespaceAndPath("playlist", "textures/album_art/shuniji.png");
            case "Wild Update" -> ResourceLocation.fromNamespaceAndPath("playlist", "textures/album_art/wildupdate.png");
            case "Trails & Tales" -> ResourceLocation.fromNamespaceAndPath("playlist", "textures/album_art/trailsandtales.png");
            case "Tricky Trials" -> ResourceLocation.fromNamespaceAndPath("playlist", "textures/album_art/trickytrials.png");
            default -> ResourceLocation.fromNamespaceAndPath("playlist", "textures/album_art/unknown.png");
        };
    }

    public static String getBiomeName(ResourceLocation id) {
        String fixed = id.getPath();
        fixed = fixed.replace('_', ' ');
        return titleCase(fixed);
    }

    public static String titleCase(String text) {
        String[] arr = text.split(" ");
        StringBuffer sb = new StringBuffer();

        for (String s : arr) {
            sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        }

        return sb.toString().trim();
    }

    public static String wrap(String src, int lineLength, String newLineStr, boolean wrapLongWords, String longWordBreak, String longWordLinePrefix) {
        if (src == null) {
            return null;
        } else {
            if (newLineStr == null) {
                newLineStr = System.getProperty("line.separator");
            }

            if (longWordBreak == null) {
                longWordBreak = "";
            }

            if (longWordLinePrefix == null) {
                longWordLinePrefix = "";
            }

            lineLength -= newLineStr.length();
            if (lineLength < 1) {
                lineLength = 1;
            }

            if (wrapLongWords && lineLength - longWordBreak.length() - longWordLinePrefix.length() < 1) {
                lineLength += longWordBreak.length() + longWordLinePrefix.length();
            }

            int remaining = lineLength;
            int breakLength = longWordBreak.length();
            Matcher m = Pattern.compile(".+?[ \\t]|.+?(?:" + newLineStr + ")|.+?$").matcher(src);
            StringBuilder cache = new StringBuilder();

            while (m.find()) {
                String word;
                for (word = m.group(); wrapLongWords && word.length() > lineLength; remaining = lineLength) {
                    cache.append(word.substring(0, remaining - breakLength)).append(longWordBreak).append(newLineStr);
                    word = longWordLinePrefix + word.substring(remaining - breakLength);
                }

                if (word.length() > remaining) {
                    cache.append(newLineStr).append(word);
                    remaining = lineLength;
                } else {
                    cache.append(word);
                }

                remaining -= word.length();
            }

            return cache.toString();
        }
    }

    public static void changeVolume(float newVolume) {
        Minecraft mc = Minecraft.getInstance();
        MusicManager musicManager = PlaylistClient.instance.musicManager;
        if (mc != null && musicManager != null) {
            mc.options.getSoundSourceOptionInstance(SoundSource.MUSIC).set((double)newVolume);
            if (newVolume <= 0.0F) {
                musicManager.stopMusic();
            }
        }
    }
}
