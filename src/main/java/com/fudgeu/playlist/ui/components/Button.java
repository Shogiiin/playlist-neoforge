package com.fudgeu.playlist.ui.components;

import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import com.fudgeu.playlist.fluxui.style.color.Color;
import com.fudgeu.playlist.fluxui.style.color.ColorStyle;
import com.fudgeu.playlist.fluxui.style.color.SolidColor;
import java.util.Optional;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

public class Button implements ComponentBuilder {
    private static final ResourceLocation xmlFile = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/button/layout.xml");
    private final PropTemplate propTemplate;
    private final ColorStyle DEFAULT_COLOR = SolidColor.create(Color.create(0.1F));
    private final ColorStyle HOVERED_COLOR = SolidColor.create(Color.create(0.15F));
    private final SolidColor DEFAULT_TEXT_COLOR = SolidColor.WHITE;
    private final ColorStyle DISABLED_COLOR = SolidColor.create(Color.create(0.085F));
    private final SolidColor DISABLED_TEXT_COLOR = SolidColor.create(Color.create(0.4F));

    public Button() {
        this.propTemplate = new PropTemplate();
        this.propTemplate.requireRunnable("onPress");
        this.propTemplate.requireBoolProp("enabled", new BoolProp(true));
        this.propTemplate.requireBoolProp("soundOnClick", new BoolProp(true));
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        PropProvider childProps = new PropProvider();
        String childText = propProvider.getChildText();
        Runnable pressAction = propProvider.getRunnable("onPress");
        BoolProp isEnabled = propProvider.getBoolProp("enabled");
        boolean playSoundOnClick = propProvider.getBoolProp("soundOnClick").get();
        childProps.setString("buttonText", childText);
        childProps.setClickHandler("buttonPress", (e, m, x, y) -> {
            if (isEnabled.get()) {
                if (playSoundOnClick) {
                    this.playDownSound();
                }

                pressAction.run();
            }
        });
        childProps.setHoverHandler("buttonHover", (e, x, y) -> {
            if (isEnabled.get()) {
                e.getStyle().setBackgroundColor(this.HOVERED_COLOR);
            }
        });
        childProps.setUnHoverHandler("buttonUnHover", e -> {
            if (isEnabled.get()) {
                e.getStyle().setBackgroundColor(this.DEFAULT_COLOR);
            }
        });
        isEnabled.addUpdateListener((oldVal, enabled) -> {
            if (enabled) {
                this.applyEnabledStyle(self);
            } else {
                this.applyDisabledStyle(self);
            }
        });
        return childProps;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
        BoolProp isEnabled = propProvider.getBoolProp("enabled");
        if (!isEnabled.get()) {
            this.applyDisabledStyle(self);
        }
    }

    @Override
    public void refresh() {
    }

    @Override
    public PropTemplate getPropTemplate() {
        return this.propTemplate;
    }

    @Override
    public ResourceLocation getComponentLocation() {
        return xmlFile;
    }

    private void playDownSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    private void applyEnabledStyle(Container self) {
        Optional<Element> buttonOpt = self.getChildAt(0);
        if (!buttonOpt.isEmpty()) {
            Element button = buttonOpt.get();
            button.getStyle().setBackgroundColor(this.DEFAULT_COLOR);
            button.getStyle().setForegroundColor(this.DEFAULT_TEXT_COLOR);
        }
    }

    private void applyDisabledStyle(Container self) {
        Optional<Element> buttonOpt = self.getChildAt(0);
        if (!buttonOpt.isEmpty()) {
            Element button = buttonOpt.get();
            button.getStyle().setBackgroundColor(this.DISABLED_COLOR);
            button.getStyle().setForegroundColor(this.DISABLED_TEXT_COLOR);
        }
    }
}
