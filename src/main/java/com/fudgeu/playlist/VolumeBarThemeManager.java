package com.fudgeu.playlist;

import com.fudgeu.playlist.client.ConfigManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.VolumeBarTheme;
import java.util.LinkedHashMap;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public class VolumeBarThemeManager {
    public LinkedHashMap<ResourceLocation, VolumeBarTheme> themes = new LinkedHashMap<>();
    private final ResourceLocation accentTheme = ResourceLocation.fromNamespaceAndPath("playlist", "theme.accent");
    private final StateManager stateManager;

    public VolumeBarThemeManager() {
        this.stateManager = PlaylistClient.instance.stateManager;
        RGBA thirdColor = new RGBA(0.3F);
        RGBA secondColor = thirdColor.lighter();
        RGBA firstColor = secondColor.lighter();
        VolumeBarTheme grayscale = new VolumeBarTheme();
        grayscale.addWave(0.0F, firstColor);
        grayscale.addWave(0.05F, secondColor);
        grayscale.addWave(0.1F, thirdColor);
        this.themes.put(ResourceLocation.fromNamespaceAndPath("playlist", "theme.grayscale"), grayscale);
        this.generateAccentTheme();
        VolumeBarTheme vaporwave = new VolumeBarTheme();
        vaporwave.addWave(0.0F, RGBA.hex(16722285));
        vaporwave.addWave(0.1F, RGBA.hex(383208));
        vaporwave.addWave(0.2F, RGBA.hex(3277116));
        vaporwave.addWave(0.3F, RGBA.hex(65834));
        this.themes.put(ResourceLocation.fromNamespaceAndPath("playlist", "theme.vaporwave"), vaporwave);
        VolumeBarTheme exo = new VolumeBarTheme();
        exo.addWave(0.0F, RGBA.hex(16742144));
        exo.addWave(0.15F, RGBA.hex(14699777));
        exo.addWave(0.3F, RGBA.hex(2762064));
        exo.addWave(0.45F, RGBA.hex(2432314));
        this.themes.put(ResourceLocation.fromNamespaceAndPath("playlist", "theme.exoplanet_ocean"), exo);
        VolumeBarTheme retro = new VolumeBarTheme();
        retro.addWave(0.0F, RGBA.hex(16115402));
        retro.addWave(0.15F, RGBA.hex(16487168));
        retro.addWave(0.3F, RGBA.hex(16074568));
        retro.addWave(0.45F, RGBA.hex(3424086));
        this.themes.put(ResourceLocation.fromNamespaceAndPath("playlist", "theme.retro"), retro);
        VolumeBarTheme lgbtq = new VolumeBarTheme();
        lgbtq.addWave(0.0F, RGBA.hex(15007744));
        lgbtq.addWave(0.2F, RGBA.hex(16747776));
        lgbtq.addWave(0.4F, RGBA.hex(16772608));
        lgbtq.addWave(0.6F, RGBA.hex(33057));
        lgbtq.addWave(0.8F, RGBA.hex(19711));
        lgbtq.addWave(1.0F, RGBA.hex(7733640));
        this.themes.put(ResourceLocation.fromNamespaceAndPath("playlist", "theme.lgbtq"), lgbtq);
        VolumeBarTheme trans = new VolumeBarTheme();
        trans.addWave(0.0F, RGBA.hex(9492979));
        trans.addWave(0.25F, RGBA.hex(15241913));
        trans.addWave(0.5F, RGBA.hex(16777215));
        trans.addWave(0.75F, RGBA.hex(15241913));
        trans.addWave(1.0F, RGBA.hex(9492979));
        this.themes.put(ResourceLocation.fromNamespaceAndPath("playlist", "theme.trans"), trans);
        VolumeBarTheme bi = new VolumeBarTheme();
        bi.addWave(0.0F, RGBA.hex(15608698));
        bi.addWave(0.45F, RGBA.hex(10633632));
        bi.addWave(0.75F, RGBA.hex(2179226));
        this.themes.put(ResourceLocation.fromNamespaceAndPath("playlist", "theme.bi"), bi);
        VolumeBarTheme lgbtq2 = new VolumeBarTheme();
        lgbtq2.addWave(0.0F, RGBA.hex(15007744));
        lgbtq2.addWave(0.05F, RGBA.hex(16747776));
        lgbtq2.addWave(0.1F, RGBA.hex(16772608));
        lgbtq2.addWave(0.15F, RGBA.hex(33057));
        lgbtq2.addWave(0.2F, RGBA.hex(19711));
        lgbtq2.addWave(0.25F, RGBA.hex(6624666));
        this.themes.put(ResourceLocation.fromNamespaceAndPath("playlist", "theme.lgbtq2"), lgbtq2);
        VolumeBarTheme trans2 = new VolumeBarTheme();
        trans2.addWave(0.0F, RGBA.hex(9492979));
        trans2.addWave(0.05F, RGBA.hex(15241913));
        trans2.addWave(0.1F, RGBA.hex(16777215));
        trans2.addWave(0.15F, RGBA.hex(15241913));
        trans2.addWave(0.2F, RGBA.hex(9492979));
        this.themes.put(ResourceLocation.fromNamespaceAndPath("playlist", "theme.trans2"), trans2);
        VolumeBarTheme bi2 = new VolumeBarTheme();
        bi2.addWave(0.0F, RGBA.hex(15608698));
        bi2.addWave(0.05F, RGBA.hex(10633632));
        bi2.addWave(0.1F, RGBA.hex(2179226));
        this.themes.put(ResourceLocation.fromNamespaceAndPath("playlist", "theme.bi2"), bi2);
    }

    public VolumeBarTheme getCurrentTheme() {
        return this.themes.get(this.getCurrentThemeID());
    }

    public ResourceLocation getCurrentThemeID() {
        ResourceLocation currentTheme = ResourceLocation.parse(ConfigManager.INSTANCE.volumeBarTheme.get());
        if (!this.themes.containsKey(currentTheme)) {
            PlaylistClient.LOGGER.warn("Invalid volume bar theme " + currentTheme.toString() + "! Changing to default.");
            currentTheme = this.accentTheme;
            ConfigManager.INSTANCE.volumeBarTheme.set(currentTheme.toString());
        }

        return currentTheme;
    }

    public Optional<VolumeBarTheme> getTheme(ResourceLocation id) {
        return Optional.ofNullable(this.themes.get(id));
    }

    public void generateAccentTheme() {
        this.themes.remove(this.accentTheme);
        VolumeBarTheme accent = new VolumeBarTheme();
        RGBA accentColor = RGBA.hex(this.stateManager.getStateInt("accentHex"));
        accent.addWave(0.0F, accentColor);
        accent.addWave(0.06F, accentColor.darker().darker());
        accent.addWave(0.1F, accentColor.darker().darker().darker().darker());
        this.themes.put(this.accentTheme, accent);
    }
}
