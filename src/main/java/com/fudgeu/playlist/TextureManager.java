package com.fudgeu.playlist;

import com.fudgeu.playlist.client.ConfigManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.fluxui.FluxInstance;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class TextureManager {
    private final Map<String, String> textures = new HashMap<>();

    public void addTexture(String id, String name) {
        this.textures.put(id, name);
    }

    public ResourceLocation getTexture(String id) {
        ResourceManager resourceManager = FluxInstance.getInstance().getResourceManager();
        StateManager stateManager = PlaylistClient.instance.stateManager;
        String currentTheme = ConfigManager.INSTANCE.iconTheme.get();
        String basePath = "playlist:textures/gui/";
        String defaultTheme = "light";
        String textureName = this.textures.get(id);
        ResourceLocation path = ResourceLocation.parse(basePath + textureName + "/" + currentTheme + ".png");
        return resourceManager.getResource(path).isPresent() ? path : ResourceLocation.parse(basePath + textureName + "/" + defaultTheme + ".png");
    }

    public boolean exists(String id) {
        return this.textures.containsKey(id);
    }
}
