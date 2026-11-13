package com.fudgeu.playlist.ui.components;

import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import com.fudgeu.playlist.fluxui.style.color.Color;
import com.fudgeu.playlist.fluxui.style.color.SolidColor;
import java.util.Optional;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

public class CharacterButton implements ComponentBuilder {
    private static final ResourceLocation xmlFile = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/character_button/layout.xml");
    private PropTemplate propTemplate;
    private final SolidColor gray = SolidColor.create(Color.create(0.6F));

    public CharacterButton() {
        this.propTemplate = new PropTemplate();
        this.propTemplate.requireRunnable("onPress");
        this.propTemplate.requireBoolProp("soundOnClick", new BoolProp(true));
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        String childText = propProvider.getChildText();
        Runnable pressAction = propProvider.getRunnable("onPress");
        boolean playSoundOnClick = propProvider.getBoolProp("soundOnClick").get();
        PropProvider childProps = new PropProvider();
        childProps.setString("buttonText", childText);
        childProps.setClickHandler("buttonPress", (e, m, x, y) -> {
            if (playSoundOnClick) {
                this.playDownSound();
            }

            pressAction.run();
        });
        childProps.setHoverHandler("buttonHover", (e, x, y) -> this.changeTextColor(e, SolidColor.WHITE));
        childProps.setUnHoverHandler("buttonUnHover", e -> this.changeTextColor(e, this.gray));
        return childProps;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
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

    private void changeTextColor(Element e, SolidColor color) {
        if (e instanceof Container c) {
            Optional<Element> characterTextOpt = c.findChildById("CharacterText");
            if (characterTextOpt.isEmpty()) {
                return;
            }

            Element characterText = characterTextOpt.get();
            characterText.getStyle().setForegroundColor(color);
        }
    }
}
