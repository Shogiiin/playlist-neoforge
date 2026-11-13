package com.fudgeu.playlist.gui.screens.settingsmenu;

import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.TextureManager;
import com.fudgeu.playlist.VolumeBarThemeManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.menu.MenuScreen;
import com.fudgeu.playlist.gui.screens.changevolumebartheme.ChangeVolumeBarThemeScreen;
import com.fudgeu.playlist.gui.widgets.RadioGroup;
import com.fudgeu.playlist.gui.widgets.TransparentButton;
import com.fudgeu.playlist.gui.widgets.TransparentTextField;
import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.RenderUtils;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class SettingsMenuScreen extends MenuScreen {
    private Minecraft mc = Minecraft.getInstance();
    private TextureManager textureManager;
    private StateManager stateManager;
    private VolumeBarThemeManager volumeBarThemeManager;

    public SettingsMenuScreen(Component title, Screen previousScreen) {
        super(title, previousScreen);
        this.textureManager = PlaylistClient.instance.textureManager;
        this.stateManager = PlaylistClient.instance.stateManager;
        this.volumeBarThemeManager = PlaylistClient.instance.volumeBarThemeManager;
    }

    @Override
    public void init() {
        this.setMainContentHeight(108);
        super.init();
        this.addSingleLineTextLabel(0, 2, Component.translatable("screen.settings_menu.accent_color"), 16777215);
        TransparentTextField accentTextField = this.addTextBox(
            this.menuWidth - 70, 0, 70, 12, Component.literal(RGBA.hexString(this.stateManager.getStateInt("accentHex")))
        );
        TransparentButton resetButton = this.addButton(this.menuWidth - 20, 16, 8, 8, null, onPress -> {
            this.stateManager.setStateInt("accentHex", 51257);
            accentTextField.setLabel(Component.literal(RGBA.hexString(51257)));
            accentTextField.setValue("");
        });
        resetButton.setIcon(this.textureManager.getTexture("reset"));
        resetButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        resetButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
        TransparentButton setButton = this.addButton(this.menuWidth - 8, 16, 8, 8, null, onPress -> {
            Integer hex = RGBA.getHexFromString(accentTextField.getValue());
            if (hex != null) {
                this.stateManager.setStateInt("accentHex", hex);
                this.volumeBarThemeManager.generateAccentTheme();
                accentTextField.setLabel(Component.literal(accentTextField.getValue()));
                accentTextField.setValue("");
            } else {
                accentTextField.setValue("");
            }
        });
        setButton.setIcon(this.textureManager.getTexture("checkmark"));
        setButton.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        setButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
        this.addSingleLineTextLabel(0, 40, Component.translatable("screen.settings_menu.darken_background"), 16777215);
        RadioGroup darkenBackgroundRadioGroup = this.addRadioGroup(this.menuWidth - 8, 40, 8, 8, self -> {
            ArrayList<String> selected = self.getSelectedButtons();
            this.stateManager.setStateBool("darkenBackground", selected.contains("darkenBackground"));
        });
        darkenBackgroundRadioGroup.addRadioButton(0, 0, "darkenBackground", Component.literal(""));
        darkenBackgroundRadioGroup.setRadioButton("darkenBackground", this.stateManager.getStateBool("darkenBackground"));
        this.addSingleLineTextLabel(0, 68, Component.translatable("screen.settings_menu.volume_bar_theme"), 16777215);
        TransparentButton editVolumeBarTheme = this.addButton(
            this.menuWidth - 8,
            68,
            8,
            8,
            null,
            onPress -> this.mc.setScreen(new ChangeVolumeBarThemeScreen(Component.translatable("screen.change_volume_bar_theme"), this))
        );
        editVolumeBarTheme.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
        editVolumeBarTheme.setIcon(this.textureManager.getTexture("edit"));
        editVolumeBarTheme.setDisplayMode(TransparentButton.DisplayMode.ICON_ONLY);
        Component currentVolumeBarTheme = Component.translatable(this.stateManager.getStateIdentifier("volumeBarTheme").getPath())
            .withStyle(ChatFormatting.ITALIC);
        this.addSingleLineTextLabel(this.menuWidth - 12 - this.mc.font.width(currentVolumeBarTheme), 68, currentVolumeBarTheme, 11184810);
        this.addSingleLineTextLabel(
            0, 96, Component.literal(String.format("Playlist %s - By Fudgeu", PlaylistClient.modVersion)).withStyle(ChatFormatting.ITALIC), 11184810
        );
        Component issueText = Component.literal("Report issues here").withStyle(ChatFormatting.ITALIC);
        TransparentButton issueButton = this.addButton(
            0,
            108,
            this.mc.font.width(issueText),
            12,
            issueText,
            self -> Util.getPlatform().openUri("http://www.github.com/fudgeu/playlist/issues")
        );
        issueButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
        issueButton.setDefaultTextColor(8947967);
        issueButton.setHoveredTextColor(11184895);
        this.setPrimaryMenuButton(Component.translatable("generic.close"), onPress -> this.closeScreen());
    }

    @Override
    public void postRender(GuiGraphics context, int left, int top, int mouseX, int mouseY, float delta) {
        RGBA accentColor = RGBA.hex(this.stateManager.getStateInt("accentHex"));
        RenderUtils.drawRect(left + this.menuWidth - 80, top + 3, left + this.menuWidth - 74, top + 9, accentColor.r, accentColor.g, accentColor.b, 1.0F);
    }
}
