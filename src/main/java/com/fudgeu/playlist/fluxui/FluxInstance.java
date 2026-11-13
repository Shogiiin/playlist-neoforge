package com.fudgeu.playlist.fluxui;

import com.fudgeu.playlist.fluxui.builders.ChildrenInsertionPoint;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.builders.ContainerBuilder;
import com.fudgeu.playlist.fluxui.builders.ElementBuilder;
import com.fudgeu.playlist.fluxui.builders.FocusContainerBuilder;
import com.fudgeu.playlist.fluxui.builders.ImageBuilder;
import com.fudgeu.playlist.fluxui.builders.TextBoxBuilder;
import com.fudgeu.playlist.fluxui.builders.TextBuilder;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.style.GlobalStylesheet;
import com.fudgeu.playlist.fluxui.style.Style;
import com.fudgeu.playlist.fluxui.transitions.TransitionHelper;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FluxInstance {
    private static FluxInstance instance;
    private final ResourceManager resourceManager;
    private final Logger LOGGER = LogManager.getLogger();
    private Element rootElement;
    private final FluxTextureManager textureManager;
    private final GlobalStylesheet globalStylesheet = new GlobalStylesheet();
    private final FluxBuilder fluxBuilder = new FluxBuilder(this);
    private final TransitionHelper transitionHelper = new TransitionHelper();

    public static FluxInstance init(ResourceManager manager, FluxTextureManager textureManager) {
        return instance = new FluxInstance(manager, textureManager);
    }

    public FluxInstance(ResourceManager manager, FluxTextureManager textureManager) {
        this.resourceManager = manager;
        this.textureManager = textureManager;
        this.registerElementBuilder("Container", new ContainerBuilder());
        this.registerElementBuilder("FocusContainer", new FocusContainerBuilder());
        this.registerElementBuilder("Text", new TextBuilder());
        this.registerElementBuilder("TextBox", new TextBoxBuilder());
        this.registerElementBuilder("Children", new ChildrenInsertionPoint());
        this.registerElementBuilder("Image", new ImageBuilder());
    }

    public void tick(float delta) {
        this.transitionHelper.tick(delta);
    }

    public Style getStyleOrDefault(String id) {
        return this.globalStylesheet.getStyle(id).orElse(Style.create());
    }

    public void registerElementBuilder(String elementTag, ElementBuilder elementBuilder) {
        this.fluxBuilder.registerElementBuilder(elementTag, elementBuilder);
    }

    public void registerComponentBuilder(String componentTag, ComponentBuilder componentBuilder) {
        this.fluxBuilder.registerComponentBuilder(componentTag, componentBuilder);
    }

    public Optional<Element> buildElement(String tag, String id, PropProvider propProvider) {
        return this.fluxBuilder.buildElement(tag, id, propProvider);
    }

    public Optional<Element> buildComponent(String tag, String id, PropProvider propProvider, ArrayList<Element> children) {
        return this.fluxBuilder.buildComponent(tag, id, propProvider, children);
    }

    public Optional<Element> buildComponent(String tag, String id, PropProvider propProvider) {
        return this.fluxBuilder.buildComponent(tag, id, propProvider, new ArrayList<>());
    }

    public void loadElementAsRoot(ResourceLocation xmlLocation, PropProvider providedProps) {
        Optional<Element> generatedElement = this.loadElementFromXML(xmlLocation, providedProps);
        if (!generatedElement.isEmpty()) {
            this.rootElement = generatedElement.get();
        }
    }

    public Optional<Element> loadElementFromXML(ResourceLocation xmlLocation, PropProvider providedProps) {
        Optional<Element> generatedElement = this.fluxBuilder.buildElementFromXML(xmlLocation, providedProps);
        if (generatedElement.isEmpty()) {
            this.LOGGER.error("Was unable to generate element from location '" + xmlLocation.toString() + "'. Root element remains unchanged.");
            return Optional.empty();
        } else {
            return generatedElement;
        }
    }

    public void setElementAsRoot(Element element) {
        this.rootElement = element;
    }

    public ResourceLocation getTexture(String id) {
        return this.textureManager.getTexture(id);
    }

    public static FluxInstance getInstance() {
        return instance;
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public Element getRootElement() {
        return this.rootElement;
    }

    public void setRootElement(Element rootElement) {
        this.rootElement = rootElement;
    }

    public GlobalStylesheet getGlobalStylesheet() {
        return this.globalStylesheet;
    }

    public FluxBuilder getFluxBuilder() {
        return this.fluxBuilder;
    }

    public TransitionHelper getTransitionHelper() {
        return this.transitionHelper;
    }
}
