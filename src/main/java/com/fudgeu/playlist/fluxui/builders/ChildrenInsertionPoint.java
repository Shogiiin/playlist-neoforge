package com.fudgeu.playlist.fluxui.builders;

import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import java.util.Optional;

public class ChildrenInsertionPoint implements ElementBuilder {
    @Override
    public Optional<Element> build(String id, PropProvider propProvider) {
        return FluxInstance.getInstance().buildElement("Container", "_ChildInsertionPoint", new PropProvider());
    }

    @Override
    public PropTemplate getPropTemplate() {
        return null;
    }
}
