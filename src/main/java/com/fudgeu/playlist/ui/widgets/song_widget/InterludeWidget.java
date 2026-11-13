package com.fudgeu.playlist.ui.widgets.song_widget;

import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import net.minecraft.resources.ResourceLocation;

public class InterludeWidget implements ComponentBuilder {
    private final ResourceLocation componentLocation = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/song_widget/interlude_widget/layout.xml");
    private final PropTemplate propTemplate = new PropTemplate();

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        return new PropProvider();
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
        return this.componentLocation;
    }
}
