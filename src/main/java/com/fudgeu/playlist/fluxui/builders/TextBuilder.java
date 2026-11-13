package com.fudgeu.playlist.fluxui.builders;

import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.elements.FText;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import java.util.Optional;
import net.minecraft.network.chat.Component;

public class TextBuilder implements ElementBuilder {
    private final PropTemplate propTemplate = new PropTemplate();

    public TextBuilder() {
        this.propTemplate.optionalString("content");
    }

    @Override
    public Optional<Element> build(String id, PropProvider propProvider) {
        String content = propProvider.getString("content");
        Component text;
        if (content != null) {
            text = Component.literal(content);
        } else {
            text = Component.literal(propProvider.getChildText());
        }

        return Optional.of(new FText(id, text));
    }

    @Override
    public PropTemplate getPropTemplate() {
        return this.propTemplate;
    }
}
