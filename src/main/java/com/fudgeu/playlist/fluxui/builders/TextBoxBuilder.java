package com.fudgeu.playlist.fluxui.builders;

import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.elements.TextBox;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;

public class TextBoxBuilder implements ElementBuilder {
    private PropTemplate propTemplate = new PropTemplate();

    public TextBoxBuilder() {
        this.propTemplate.requireStringConsumer("onUpdate", s -> {});
        this.propTemplate.requireString("defaultText", "");
        this.propTemplate.optionalRunnable("onFocus");
        this.propTemplate.optionalRunnable("onUnFocus");
        this.propTemplate.requireBoolProp("numbersOnly", new BoolProp(false));
        this.propTemplate.requireInteger("maxLength", -1);
    }

    @Override
    public Optional<Element> build(String id, PropProvider propProvider) {
        Consumer<String> onUpdate = propProvider.getStringConsumer("onUpdate");
        String defaultText = propProvider.getString("defaultText");
        Runnable onFocus = propProvider.getRunnable("onFocus");
        Runnable onUnFocus = propProvider.getRunnable("onUnFocus");
        BoolProp numbersOnly = propProvider.getBoolProp("numbersOnly");
        int maxLength = propProvider.getInteger("maxLength");
        TextBox textBox = new TextBox(id, Component.literal(defaultText), onUpdate, onFocus, onUnFocus, numbersOnly, maxLength);
        return Optional.of(textBox);
    }

    @Override
    public PropTemplate getPropTemplate() {
        return this.propTemplate;
    }
}
