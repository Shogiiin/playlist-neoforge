package com.fudgeu.playlist.ui.components;

import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.VolumeBarThemeManager;
import com.fudgeu.playlist.client.ConfigManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.elements.FText;
import com.fudgeu.playlist.fluxui.elements.TextBox;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import com.fudgeu.playlist.fluxui.style.color.Color;
import com.fudgeu.playlist.fluxui.style.color.SolidColor;
import com.fudgeu.playlist.ui.IconTheme;
import com.fudgeu.playlist.utils.RGBA;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SettingsMenu implements ComponentBuilder {
    private static final ResourceLocation xmlFile = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/settings_menu/layout.xml");
    private final PropTemplate requiredProps;
    private StateManager stateManager;
    private VolumeBarThemeManager volumeBarThemeManager;
    private final SolidColor COLOR_INVALID = SolidColor.create(Color.fromHex(16746624));

    public SettingsMenu() {
        this.requiredProps = new PropTemplate();
        this.requiredProps.requireElementConsumer("goTo");
        this.requiredProps.requireRunnable("goBack");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        this.stateManager = PlaylistClient.instance.stateManager;
        this.volumeBarThemeManager = PlaylistClient.instance.volumeBarThemeManager;
        FluxInstance fluxInstance = FluxInstance.getInstance();
        Consumer<Element> goTo = propProvider.getElementConsumer("goTo");
        PropProvider childProps = new PropProvider();
        Runnable goBack = propProvider.getRunnable("goBack");
        childProps.setRunnable("exitScreen", goBack);
        int accentHex = this.stateManager.getStateInt("accentHex");
        String accentHexString = RGBA.hexString(accentHex);
        accentHexString = accentHexString.replace("#", "");
        childProps.setString("accentDefaultText", accentHexString);
        childProps.setStringConsumer("onAccentUpdate", newString -> {
            Optional<Element> hexSymbol = self.findChildById("HexSymbol");
            Optional<Element> textBox = self.findChildById("AccentTextBox");
            boolean accentUpdated = this.onAccentUpdate(newString);
            if (!accentUpdated) {
                hexSymbol.ifPresent(e -> e.getStyle().setForegroundColor(this.COLOR_INVALID));
                textBox.ifPresent(e -> e.getStyle().setForegroundColor(this.COLOR_INVALID));
            } else {
                Optional<Element> accentPreviewOpt = self.findChildById("AccentPreview");
                accentPreviewOpt.ifPresent(e -> {
                    int hex = this.stateManager.getStateInt("accentHex");
                    e.getStyle().setBackgroundColor(SolidColor.create(Color.fromHex(hex)));
                });
                hexSymbol.ifPresent(e -> e.getStyle().setForegroundColor(SolidColor.WHITE));
                textBox.ifPresent(e -> e.getStyle().setForegroundColor(SolidColor.WHITE));
            }
        });
        childProps.setRunnable("AccentTextBoxUnfocus", () -> this.onAccentTextBoxUnfocus(self));
        Component themeLabel = this.getVolumeBarThemeLabel(ConfigManager.INSTANCE.volumeBarTheme.get());
        ConfigManager.INSTANCE
            .volumeBarTheme
            .addUpdateListener((oldVal, newVal) -> this.textUpdater(self, "VolumeBarThemeLabel", this.getVolumeBarThemeLabel(newVal)));
        childProps.setString("volumeBarThemeLabel", themeLabel.getString());
        Runnable handleVolumeBarThemeButton = () -> fluxInstance.buildComponent("VolumeBarThemeMenu", "ChangeVolumeThemeMenu", propProvider).ifPresent(goTo);
        childProps.setRunnable("handleVolumeBarThemeButton", handleVolumeBarThemeButton);
        String currentIconTheme = ConfigManager.INSTANCE.iconTheme.get();
        ConfigManager.INSTANCE.iconTheme.addUpdateListener((oldVal, newVal) -> this.textUpdater(self, "IconThemeLabel", this.getIconThemeLabel(newVal)));
        childProps.setString("iconThemeLabel", this.getIconThemeLabel(currentIconTheme).getString());
        Runnable handleIconThemeButton = () -> fluxInstance.buildComponent("IconThemeMenu", "ChangeIconThemeMenu", propProvider).ifPresent(goTo);
        childProps.setRunnable("handleIconThemeButton", handleIconThemeButton);
        this.addToggle("darkenBackground", "darkenBackgroundToggle", "darkenBackgroundValue", childProps);
        this.addToggle("skipInterlude", "skipInterludeToggle", "skipInterludeValue", childProps);
        this.addToggle("showQuickPlayer", "showQuickPlayerToggle", "showQuickPlayerValue", childProps);
        this.addToggleNewType(ConfigManager.INSTANCE.showAltNames, "showAltNamesToggle", "showAltNamesValue", childProps);
        childProps.setRunnable("reportBug", () -> Util.getPlatform().openUri("http://www.github.com/fudgeu/playlist/issues"));
        return childProps;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
        Optional<Element> accentPreviewOpt = self.findChildById("AccentPreview");
        accentPreviewOpt.ifPresent(e -> {
            int hex = this.stateManager.getStateInt("accentHex");
            e.getStyle().setBackgroundColor(SolidColor.create(Color.fromHex(hex)));
        });
    }

    @Override
    public void refresh() {
    }

    @Override
    public PropTemplate getPropTemplate() {
        return this.requiredProps;
    }

    @Override
    public ResourceLocation getComponentLocation() {
        return xmlFile;
    }

    private void addToggle(String stateBoolId, String runnableKey, String valueKey, PropProvider childProps) {
        boolean currentToggleValue = this.stateManager.getStateBool(stateBoolId);
        BoolProp toggleValue = new BoolProp(currentToggleValue);
        toggleValue.addUpdateListener((oldVal, newVal) -> this.stateManager.setStateBool(stateBoolId, newVal));
        Runnable onToggle = toggleValue::invert;
        childProps.setRunnable(runnableKey, onToggle);
        childProps.setBoolProp(valueKey, toggleValue);
    }

    private void addToggleNewType(BoolProp configBool, String runnableKey, String valueKey, PropProvider childProps) {
        Runnable onToggle = configBool::invert;
        childProps.setRunnable(runnableKey, onToggle);
        childProps.setBoolProp(valueKey, configBool);
    }

    private boolean onAccentUpdate(String newString) {
        Integer hex = RGBA.getHexFromString(newString);
        if (hex == null) {
            return false;
        } else {
            this.stateManager.setStateInt("accentHex", hex);
            this.volumeBarThemeManager.generateAccentTheme();
            return true;
        }
    }

    private void onAccentTextBoxUnfocus(Container self) {
        Optional<Element> textBoxOpt = self.findChildById("AccentTextBox");
        textBoxOpt.ifPresent(e -> {
            if (e instanceof TextBox textBox) {
                if (!this.onAccentUpdate(textBox.getText())) {
                    int hex = this.stateManager.getStateInt("accentHex");
                    textBox.setText(RGBA.hexString(hex));
                }
            }
        });
    }

    private Component getVolumeBarThemeLabel(String themeId) {
        ResourceLocation id = ResourceLocation.parse(themeId);
        return Component.translatable(id.getPath());
    }

    private Component getIconThemeLabel(String commonName) {
        for (IconTheme theme : IconTheme.values()) {
            if (theme.commonName.equals(commonName)) {
                return Component.translatable(theme.translationKey);
            }
        }

        return Component.literal("Unknown");
    }

    private void textUpdater(Container self, String textId, Component newVal) {
        self.findChildById(textId).ifPresent(e -> {
            if (e instanceof FText text) {
                text.setText(newVal);
            }
        });
    }
}
