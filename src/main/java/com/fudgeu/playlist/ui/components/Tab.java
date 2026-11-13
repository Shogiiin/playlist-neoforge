package com.fudgeu.playlist.ui.components;

import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;

public class Tab implements ComponentBuilder {
    private final ResourceLocation componentLocation = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/tab_group/tab/layout.xml");
    private final PropTemplate propTemplate = new PropTemplate();

    public Tab() {
        this.propTemplate.requireString("tabId");
        this.propTemplate.requireString("tabLabel");
        this.propTemplate.requireBoolProp("selected");
        this.propTemplate.requireStringConsumer("onSelect");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        BoolProp selected = propProvider.getBoolProp("selected");
        PropProvider childProps = new PropProvider();
        childProps.setString("tabLabel", propProvider.getString("tabLabel"));
        childProps.setHoverHandler("onHover", (e, x, y) -> {
            if (!selected.get()) {
                Container tab = (Container)e;
                tab.setId("HoveredTab");
            }
        });
        childProps.setUnHoverHandler("onUnHover", e -> {
            if (!selected.get()) {
                Container tab = (Container)e;
                tab.setId("Tab");
            }
        });
        Consumer<String> onSelect = propProvider.getStringConsumer("onSelect");
        String tabId = propProvider.getString("tabId");
        childProps.setClickHandler("onClick", (e, m, x, y) -> onSelect.accept(tabId));
        return childProps;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
        BoolProp selected = propProvider.getBoolProp("selected");
        this.updateIfSelected(self, selected.get());
        selected.addUpdateListener((oldVal, newVal) -> this.updateIfSelected(self, newVal));
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

    private void updateIfSelected(Container self, boolean selected) {
        self.getChildAt(0).ifPresent(tabGroupEl -> {
            Container tabGroup = (Container)tabGroupEl;
            tabGroup.setId(selected ? "SelectedTab" : "Tab");
        });
    }
}
