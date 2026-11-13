package com.fudgeu.playlist.gui.screens.changevolumebartheme;

import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.VolumeBarThemeManager;
import com.fudgeu.playlist.client.ConfigManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.menu.MenuScreen;
import com.fudgeu.playlist.gui.widgets.SelectionList;
import java.util.LinkedHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.Screen;

public class ChangeVolumeBarThemeScreen extends MenuScreen {
    private final VolumeBarThemeManager volumeBarThemeManager;
    private final StateManager stateManager;

    public ChangeVolumeBarThemeScreen(Component title, Screen previousScreen) {
        super(title, previousScreen);
        this.volumeBarThemeManager = PlaylistClient.instance.volumeBarThemeManager;
        this.stateManager = PlaylistClient.instance.stateManager;
    }

    @Override
    public void init() {
        this.setMainContentHeight(80);
        super.init();
        this.addSingleLineTextLabel(0, 0, Component.translatable("screen.change_volume_bar_theme.select"), 16777215);
        LinkedHashMap<String, Component> selections = new LinkedHashMap<>();

        for (ResourceLocation id : this.volumeBarThemeManager.themes.keySet()) {
            selections.put(id.toString(), Component.translatable(id.getPath()));
        }

        SelectionList selectionList = this.addSelectionList(0, 15, this.menuWidth, this.menuHeight, 12, selections);
        selectionList.setSelected(ConfigManager.INSTANCE.volumeBarTheme.get());
        selectionList.setSelectionAction(idx -> ConfigManager.INSTANCE.volumeBarTheme.set(idx));
        this.setPrimaryMenuButton(Component.translatable("generic.close"), onPress -> this.closeScreen());
    }
}
