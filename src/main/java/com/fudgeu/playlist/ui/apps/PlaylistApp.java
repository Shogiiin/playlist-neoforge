package com.fudgeu.playlist.ui.apps;

import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import com.fudgeu.playlist.fluxui.transitions.QuickTransition;
import com.fudgeu.playlist.fluxui.transitions.TransitionHelper;
import java.util.Optional;
import java.util.Stack;
import net.minecraft.resources.ResourceLocation;

public class PlaylistApp implements ComponentBuilder {
    private final FluxInstance fluxInstance = FluxInstance.getInstance();
    private final TransitionHelper transition = this.fluxInstance.getTransitionHelper();
    private final ResourceLocation xmlFile = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/playlist_app/layout.xml");
    private final PropTemplate propTemplate = new PropTemplate();

    public PlaylistApp() {
        this.propTemplate.requireRunnable("onExit");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        Stack<Element> elementStack = new Stack<>();
        PropProvider childProps = new PropProvider();
        childProps.setElementConsumer("goTo", e -> {
            Optional<Element> currentViewOpt = self.getChildAt(0);
            if (!currentViewOpt.isEmpty()) {
                Element currentView = currentViewOpt.get();
                elementStack.push(currentView);
                QuickTransition.scaleOut(currentView, this.transition, 0.9F, 2);
                this.transition.setTimeout(() -> {
                    self.removeAllChildren();
                    self.addChild(e);
                    QuickTransition.scaleIn(e, this.transition, 1.1F, 2);
                }, 2);
            }
        });
        Runnable onExit = propProvider.getRunnable("onExit");
        childProps.setRunnable("goBack", () -> {
            Optional<Element> currentViewOpt = self.getChildAt(0);
            if (!currentViewOpt.isEmpty()) {
                Element currentView = currentViewOpt.get();
                QuickTransition.scaleOut(currentView, this.transition, 1.1F, 2);
                this.transition.setTimeout(() -> {
                    self.removeAllChildren();
                    if (elementStack.isEmpty()) {
                        onExit.run();
                    } else {
                        Element previousElement = elementStack.pop();
                        self.addChild(previousElement);
                        QuickTransition.scaleIn(previousElement, this.transition, 0.9F, 2);
                    }
                }, 2);
            }
        });
        return childProps;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
        self.getChildAt(0).ifPresent(e -> QuickTransition.scaleIn(e, this.transition, 1.1F, 2));
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
        return this.xmlFile;
    }
}
