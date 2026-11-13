package com.fudgeu.playlist.client;

import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.StringState;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigManager {
    private static final String configLocation = "/config/playlist.json";
    private static final Logger LOGGER = LogManager.getLogger();
    public static ConfigManager INSTANCE;
    public final StringState volumeBarTheme = new StringState("playlist:theme.accent");
    public final StringState iconTheme = new StringState("light");
    public final BoolProp showAltNames = new BoolProp(true);

    private ConfigManager() {
    }

    public static ConfigManager init() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigManager();
        }

        return INSTANCE;
    }

    public static void saveConfig(StateManager stateManager) {
        JsonObject json = new JsonObject();
        json.addProperty("accent_hex", RGBA.hexString(stateManager.getStateInt("accentHex")));
        json.addProperty("darken_background", stateManager.getStateBool("darkenBackground"));
        json.addProperty("volume_bar_theme", INSTANCE.volumeBarTheme.get());
        json.addProperty("skip_interlude", stateManager.getStateBool("skipInterlude"));
        json.addProperty("show_quick_player", stateManager.getStateBool("showQuickPlayer"));
        json.addProperty("icon_theme", INSTANCE.iconTheme.get());
        json.addProperty("show_alt_names", INSTANCE.showAltNames.get());

        try {
            String fullLocation = FMLPaths.GAMEDIR.get() + "/config/playlist.json";
            File file = new File(fullLocation);
            file.delete();
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(fullLocation));
            String serializedJson = new GsonBuilder().setPrettyPrinting().create().toJson(json);
            writer.write(serializedJson);
            writer.close();
        } catch (Exception var6) {
            LOGGER.error("Failed to save config file!");
            var6.printStackTrace();
        }
    }

    public static void loadConfig(StateManager stateManager) {
        File orderFile = new File(FMLPaths.GAMEDIR.get().toString() + "/config/playlist.json");

        try {
            JsonObject json = JsonParser.parseReader(new BufferedReader(new InputStreamReader(new FileInputStream(orderFile)))).getAsJsonObject();
            JsonElement accentHexStringJson = json.get("accent_hex");
            String accentHexString = "#00c839";
            if (accentHexStringJson != null) {
                accentHexString = accentHexStringJson.getAsString();
            }

            stateManager.setStateInt("accentHex", RGBA.getHexFromString(accentHexString));
            JsonElement darkenBackgroundJson = json.get("darken_background");
            boolean darkenBackground = ModList.get().isLoaded("rainbowify");
            if (darkenBackgroundJson != null) {
                darkenBackground = darkenBackgroundJson.getAsBoolean();
            }

            stateManager.setStateBool("darkenBackground", darkenBackground);
            JsonElement volumeBarThemeJson = json.get("volume_bar_theme");
            ResourceLocation volumeBarTheme = ResourceLocation.parse("playlist:theme.accent");
            if (volumeBarThemeJson != null) {
                volumeBarTheme = ResourceLocation.parse(volumeBarThemeJson.getAsString());
            }

            INSTANCE.volumeBarTheme.set(volumeBarTheme.toString());
            JsonElement skipInterludeJson = json.get("skip_interlude");
            boolean skipInterlude = true;
            if (skipInterludeJson != null) {
                skipInterlude = skipInterludeJson.getAsBoolean();
            }

            stateManager.setStateBool("skipInterlude", skipInterlude);
            JsonElement showQuickPlayerJson = json.get("show_quick_player");
            boolean showQuickPlayer = true;
            if (showQuickPlayerJson != null) {
                showQuickPlayer = showQuickPlayerJson.getAsBoolean();
            }

            stateManager.setStateBool("showQuickPlayer", showQuickPlayer);
            JsonElement iconThemeJson = json.get("icon_theme");
            String iconTheme = "light";
            if (iconThemeJson != null) {
                iconTheme = iconThemeJson.getAsString();
            }

            INSTANCE.iconTheme.set(iconTheme);
            JsonElement showAltNamesJson = json.get("slow_alt_names");
            boolean showAltNames = true;
            if (showAltNamesJson != null) {
                showAltNames = showAltNamesJson.getAsBoolean();
            }

            INSTANCE.showAltNames.set(showAltNames);
        } catch (Exception var17) {
            LOGGER.error("Failed to load config file, using default values");
            stateManager.setStateInt("accentHex", 51257);
            stateManager.setStateBool("darkenBackground", ModList.get().isLoaded("rainbowify"));
            stateManager.setStateIdentifier("volumeBarTheme", ResourceLocation.parse("playlist:theme.accent"));
            stateManager.setStateBool("skipInterlude", true);
            stateManager.setStateBool("showQuickPlayer", true);
            INSTANCE.iconTheme.set("light");
            INSTANCE.volumeBarTheme.set("playlist:theme.accent");
            INSTANCE.showAltNames.set(true);
        }
    }
}
