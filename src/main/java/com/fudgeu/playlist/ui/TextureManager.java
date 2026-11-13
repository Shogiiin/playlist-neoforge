package com.fudgeu.playlist.ui;

import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.client.ConfigManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.FluxTextureManager;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class TextureManager implements FluxTextureManager {
    public static TextureManager INSTANCE;
    private final StateManager stateManager;
    private UUID updateKey = UUID.randomUUID();

    private TextureManager() {
        this.stateManager = PlaylistClient.instance.stateManager;
    }

    public static TextureManager init() {
        return INSTANCE != null ? INSTANCE : (INSTANCE = new TextureManager());
    }

    @Override
    public ResourceLocation getTexture(String id) {
        if (id.startsWith("playlist:")) {
            return ResourceLocation.parse(id);
        } else {
            ResourceManager resourceManager = FluxInstance.getInstance().getResourceManager();
            String currentTheme = ConfigManager.INSTANCE.iconTheme.get();
            String basePath = "playlist:textures/gui/";
            String defaultTheme = "light";
            ResourceLocation path = ResourceLocation.parse(basePath + id + "/" + currentTheme + ".png");
            return resourceManager.getResource(path).isPresent() ? path : ResourceLocation.parse(basePath + id + "/" + defaultTheme + ".png");
        }
    }

    public void requestUpdate() {
        this.updateKey = UUID.randomUUID();
    }

    public UUID getUpdateKey() {
        return this.updateKey;
    }
}
