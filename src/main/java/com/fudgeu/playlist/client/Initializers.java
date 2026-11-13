package com.fudgeu.playlist.client;

import com.fudgeu.playlist.ConditionManager;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.FluxUI;
import com.fudgeu.playlist.ui.TextureManager;
import com.fudgeu.playlist.ui.apps.CreatePlaylistApp;
import com.fudgeu.playlist.ui.apps.PlaylistApp;
import com.fudgeu.playlist.ui.components.Button;
import com.fudgeu.playlist.ui.components.CharacterButton;
import com.fudgeu.playlist.ui.components.CreatePlaylistMenu;
import com.fudgeu.playlist.ui.components.Menu;
import com.fudgeu.playlist.ui.components.PlaylistTimings;
import com.fudgeu.playlist.ui.components.SettingsMenu;
import com.fudgeu.playlist.ui.components.SettingsMenuToggle;
import com.fudgeu.playlist.ui.components.Tab;
import com.fudgeu.playlist.ui.components.TabGroup;
import com.fudgeu.playlist.ui.components.ToggleSwitch;
import com.fudgeu.playlist.ui.components.icon_theme_menu.IconThemeEntry;
import com.fudgeu.playlist.ui.components.icon_theme_menu.IconThemeMenu;
import com.fudgeu.playlist.ui.components.volume_bar_theme_menu.VolumeBarThemeEntry;
import com.fudgeu.playlist.ui.components.volume_bar_theme_menu.VolumeBarThemeMenu;
import com.fudgeu.playlist.ui.widgets.song_widget.InterludeWidget;
import com.fudgeu.playlist.ui.widgets.song_widget.SongWidget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.WinScreen;

public class Initializers {
    public static void initConditions(ConditionManager conditionManager) {
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation overworld = ResourceLocation.fromNamespaceAndPath("minecraft", "overworld");
        ResourceLocation nether = ResourceLocation.fromNamespaceAndPath("minecraft", "the_nether");
        ResourceLocation theEnd = ResourceLocation.fromNamespaceAndPath("minecraft", "the_end");
        conditionManager.registerCondition(
            "category.overworld", true, () -> mc.player != null ? mc.player.getCommandSenderWorld().dimension().location().equals(overworld) : false
        );
        conditionManager.registerCondition(
            "category.nether", true, () -> mc.player != null ? mc.player.getCommandSenderWorld().dimension().location().equals(nether) : false
        );
        conditionManager.registerCondition(
            "category.the_end", true, () -> mc.player != null ? mc.player.getCommandSenderWorld().dimension().location().equals(theEnd) : false
        );
        conditionManager.registerActiveCondition("category.credits", true, () -> mc.screen instanceof WinScreen);
        conditionManager.registerCondition("category.menu", true, () -> mc.screen instanceof TitleScreen);
        conditionManager.registerCondition(
            "condition.creative",
            false,
            () -> mc.player != null && mc.player.getCommandSenderWorld().dimension().location().equals(overworld) && mc.player.isCreative()
        );
        conditionManager.registerCondition(
            "condition.aquatic",
            false,
            () -> mc.player != null && mc.player.getCommandSenderWorld().dimension().location().equals(overworld) && mc.player.isUnderWater()
        );
        conditionManager.registerActiveCondition("condition.end_dragon", false, () -> mc.gui.getBossOverlay().shouldPlayMusic());
    }

    public static FluxInstance initFlux(ResourceManager resourceManager) {
        FluxUI.provideMinecraftClient(Minecraft.getInstance());
        TextureManager.init();
        FluxInstance fluxInstance = FluxInstance.init(resourceManager, TextureManager.INSTANCE);
        fluxInstance.registerComponentBuilder("PlaylistApp", new PlaylistApp());
        fluxInstance.registerComponentBuilder("SettingsMenu", new SettingsMenu());
        fluxInstance.registerComponentBuilder("ToggleSwitch", new ToggleSwitch());
        fluxInstance.registerComponentBuilder("Button", new Button());
        fluxInstance.registerComponentBuilder("CharacterButton", new CharacterButton());
        fluxInstance.registerComponentBuilder("SettingsMenuToggle", new SettingsMenuToggle());
        fluxInstance.registerComponentBuilder("VolumeBarThemeMenu", new VolumeBarThemeMenu());
        fluxInstance.registerComponentBuilder("VolumeBarTheme", new VolumeBarThemeEntry());
        fluxInstance.registerComponentBuilder("Menu", new Menu());
        fluxInstance.registerComponentBuilder("CreatePlaylistMenu", new CreatePlaylistMenu());
        fluxInstance.registerComponentBuilder("CreatePlaylistApp", new CreatePlaylistApp());
        fluxInstance.registerComponentBuilder("TabGroup", new TabGroup());
        fluxInstance.registerComponentBuilder("Tab", new Tab());
        fluxInstance.registerComponentBuilder("PlaylistTimings", new PlaylistTimings());
        fluxInstance.registerComponentBuilder("SongWidget", new SongWidget());
        fluxInstance.registerComponentBuilder("InterludeWidget", new InterludeWidget());
        fluxInstance.registerComponentBuilder("IconThemeMenu", new IconThemeMenu());
        fluxInstance.registerComponentBuilder("IconThemeEntry", new IconThemeEntry());
        return fluxInstance;
    }
}
