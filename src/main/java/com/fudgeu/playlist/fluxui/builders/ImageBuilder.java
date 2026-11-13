package com.fudgeu.playlist.fluxui.builders;

import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.elements.Image;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import java.util.Optional;

public class ImageBuilder implements ElementBuilder {
    private final PropTemplate propTemplate = new PropTemplate();

    public ImageBuilder() {
        this.propTemplate.requireString("src");
        this.propTemplate.requireInteger("width", 8);
        this.propTemplate.requireInteger("height", 8);
    }

    @Override
    public Optional<Element> build(String id, PropProvider propProvider) {
        String source = propProvider.getString("src");
        int width = propProvider.getInteger("width");
        int height = propProvider.getInteger("height");
        return Optional.of(new Image(id, source, width, height));
    }

    @Override
    public PropTemplate getPropTemplate() {
        return this.propTemplate;
    }
}
