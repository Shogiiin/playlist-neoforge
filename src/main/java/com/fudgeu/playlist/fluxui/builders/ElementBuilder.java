package com.fudgeu.playlist.fluxui.builders;

import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import java.util.Optional;

public interface ElementBuilder {
    Optional<Element> build(String var1, PropProvider var2);

    PropTemplate getPropTemplate();
}
