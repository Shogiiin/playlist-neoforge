package com.fudgeu.playlist.ui.components;

import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;

public class TabGroup implements ComponentBuilder {
    private FluxInstance fluxInstance;
    private final ResourceLocation componentLocation = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/tab_group/layout.xml");
    private final PropTemplate propTemplate = new PropTemplate();

    public TabGroup() {
        this.propTemplate.requireStringMap("tabs");
        this.propTemplate.requireStringConsumer("onChange", s -> {});
        this.propTemplate.requireString("defaultSelection");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        return null;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
        this.fluxInstance = FluxInstance.getInstance();
        ArrayList<Element> tabElements = new ArrayList<>();
        HashMap<String, String> tabs = propProvider.getStringMap("tabs");
        String selectedTab = propProvider.getString("defaultSelection");
        Consumer<String> onChange = propProvider.getStringConsumer("onChange");
        HashMap<String, BoolProp> selectedStates = new HashMap<>();

        for (String tabId : tabs.keySet()) {
            BoolProp selected = new BoolProp(tabId.equals(selectedTab));
            selectedStates.put(tabId, selected);
            PropProvider tabProps = new PropProvider();
            tabProps.setString("tabId", tabId);
            tabProps.setString("tabLabel", tabs.get(tabId));
            tabProps.setBoolProp("selected", selected);
            tabProps.setStringConsumer("onSelect", newSelection -> {
                onChange.accept(newSelection);
                this.updateSelectedStates(selectedStates, newSelection);
            });
            Optional<Element> tabOpt = this.fluxInstance.buildComponent("Tab", null, tabProps);
            if (!tabOpt.isEmpty()) {
                tabElements.add(tabOpt.get());
            }
        }

        self.findChildById("TabGroup").ifPresent(element -> {
            if (element instanceof Container tabGroup) {
                tabElements.forEach(tabGroup::addChild);
            }
        });
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

    private void updateSelectedStates(HashMap<String, BoolProp> states, String selectedTabId) {
        states.forEach((tabId, tabValue) -> {
            if (tabId.equals(selectedTabId)) {
                tabValue.set(true);
            } else {
                tabValue.set(false);
            }
        });
    }
}
