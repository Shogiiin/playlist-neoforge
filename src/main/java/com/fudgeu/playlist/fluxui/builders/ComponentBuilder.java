package com.fudgeu.playlist.fluxui.builders;

import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import net.minecraft.resources.ResourceLocation;

public interface ComponentBuilder {
    PropProvider build(String var1, Container var2, PropProvider var3, State var4);

    void onLoadFinished(Container var1, PropProvider var2);

    void refresh();

    PropTemplate getPropTemplate();

    ResourceLocation getComponentLocation();
}
