package com.fudgeu.playlist.fluxui;

import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.builders.ElementBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import com.fudgeu.playlist.fluxui.style.Style;
import com.fudgeu.playlist.fluxui.style.StyleBuilder;
import com.fudgeu.playlist.fluxui.style.sizing.Sizing;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class FluxBuilder {
    private final Logger LOGGER = LogManager.getLogger();
    private final FluxInstance fluxInstance;
    private final HashMap<String, ElementBuilder> elementBuilders = new HashMap<>();
    private final HashMap<String, ComponentBuilder> componentBuilders = new HashMap<>();

    public FluxBuilder(FluxInstance fluxInstance) {
        this.fluxInstance = fluxInstance;
    }

    protected Optional<com.fudgeu.playlist.fluxui.elements.Element> buildElementFromXML(ResourceLocation xmlFile, PropProvider providedProps) {
        Optional<Element> preparedElementOptional = this.prepareXMLElement(xmlFile);
        return preparedElementOptional.isEmpty() ? Optional.empty() : this.buildElementFromExtractedXML(preparedElementOptional.get(), providedProps);
    }

    private Optional<Element> prepareXMLElement(ResourceLocation xmlFile) {
        ResourceManager manager = this.fluxInstance.getResourceManager();
        Optional<Resource> xmlResourceOpt = manager.getResource(xmlFile);
        if (xmlResourceOpt.isEmpty()) {
            this.LOGGER.error("Unable to find provided XML file for element building: " + xmlFile.toString());
            return Optional.empty();
        } else {
            Resource xmlResource = xmlResourceOpt.get();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException var20) {
                throw new RuntimeException(var20);
            }

            Document dom;
            try {
                dom = builder.parse(xmlResource.open());
            } catch (SAXException var18) {
                throw new RuntimeException(var18);
            } catch (IOException var19) {
                this.LOGGER.error("Unable to find provided XML file for element building: " + xmlFile.toString());
                return Optional.empty();
            }

            Element rootElement = dom.getDocumentElement();
            NodeList stylesheetNodes = rootElement.getElementsByTagName("Stylesheet");
            HashMap<String, Style> stylesheet = this.getStylesheetLocations(xmlFile, stylesheetNodes);
            this.fluxInstance.getGlobalStylesheet().addStyles(stylesheet);
            NodeList contentNodes = rootElement.getElementsByTagName("Content");
            if (contentNodes.getLength() == 0) {
                this.LOGGER.error("No Content node in '" + xmlFile.toString() + "'. Cannot generate element.");
                return Optional.empty();
            } else {
                Node contentNode = contentNodes.item(0);
                if (contentNode.getNodeType() != 1) {
                    this.LOGGER.error("Improper Content node in '" + xmlFile.toString() + "'. Cannot generate element.");
                    return Optional.empty();
                } else {
                    Element content = (Element)contentNode;
                    Element firstElement = null;
                    NodeList contentNodeChildren = content.getChildNodes();

                    for (int o = 0; o < contentNodeChildren.getLength(); o++) {
                        Node child = contentNodeChildren.item(o);
                        if (child.getNodeType() == 1) {
                            firstElement = (Element)child;
                        }
                    }

                    if (firstElement == null) {
                        this.LOGGER.error("No element found in Content node in '" + xmlFile.toString() + "'. Cannot generate element.");
                        return Optional.empty();
                    } else {
                        return Optional.of(firstElement);
                    }
                }
            }
        }
    }

    private Optional<com.fudgeu.playlist.fluxui.elements.Element> buildElementFromExtractedXML(Element xmlElement, PropProvider providedProps) {
        if (providedProps == null) {
            providedProps = new PropProvider();
        }

        HashMap<String, String> rawProps = new HashMap<>();
        String tag = xmlElement.getTagName();
        String elementId = xmlElement.getAttribute("id");
        Node childNode = xmlElement.getFirstChild();
        String childText = "";
        if (childNode != null && childNode.getNodeType() == 3) {
            Text textNode = (Text)childNode;
            childText = textNode.getTextContent();
            childText = childText.trim();
        }

        providedProps.setChildText(childText);
        NamedNodeMap attributeList = xmlElement.getAttributes();

        for (int a = 0; a < attributeList.getLength(); a++) {
            Node attributeNode = attributeList.item(a);
            if (attributeNode.getNodeType() == 2) {
                Attr attribute = (Attr)attributeNode;
                rawProps.put(attribute.getName(), attribute.getValue());
            }
        }

        ComponentBuilder componentBuilder = null;
        ElementBuilder elementBuilder = null;
        PropTemplate propTemplate;
        if (this.isComponent(tag)) {
            componentBuilder = this.componentBuilders.get(tag);
            if (componentBuilder == null) {
                this.LOGGER.error("Unknown element tag '" + xmlElement.getTagName() + "'. Not building element.");
                return Optional.empty();
            }

            propTemplate = componentBuilder.getPropTemplate();
        } else {
            elementBuilder = this.elementBuilders.get(tag);
            if (elementBuilder == null) {
                this.LOGGER.error("Unknown element tag '" + xmlElement.getTagName() + "'. Not building element.");
                return Optional.empty();
            }

            propTemplate = elementBuilder.getPropTemplate();
        }

        PropProvider fixedProps = new PropProvider();
        if (propTemplate != null) {
            fixedProps = propTemplate.buildProviderFrom(rawProps, providedProps);
            if (fixedProps == null) {
                return Optional.empty();
            }
        }

        fixedProps.setChildText(providedProps.getChildText());
        boolean addChildren = true;
        ArrayList<com.fudgeu.playlist.fluxui.elements.Element> children = this.getChildrenAndBuild(xmlElement, providedProps);
        Optional<com.fudgeu.playlist.fluxui.elements.Element> builtElementOpt;
        if (componentBuilder != null) {
            builtElementOpt = this.buildComponent(componentBuilder, elementId, fixedProps, children);
            addChildren = false;
        } else {
            builtElementOpt = elementBuilder.build(elementId, fixedProps);
        }

        if (builtElementOpt.isEmpty()) {
            this.LOGGER.error("Unable to build element '" + xmlElement.getTagName() + "', received empty object from builder");
            return Optional.empty();
        } else {
            com.fudgeu.playlist.fluxui.elements.Element element = builtElementOpt.get();
            if (addChildren && element instanceof Container container) {
                children.forEach(container::addChild);
                return Optional.of(container);
            } else {
                return Optional.of(element);
            }
        }
    }

    private ArrayList<com.fudgeu.playlist.fluxui.elements.Element> getChildrenAndBuild(Element parentXMLElement, PropProvider providedProps) {
        ArrayList<com.fudgeu.playlist.fluxui.elements.Element> builtChildren = new ArrayList<>();
        NodeList childElementNodes = parentXMLElement.getChildNodes();

        for (int i = 0; i < childElementNodes.getLength(); i++) {
            Node childNode = childElementNodes.item(i);
            if (childNode.getNodeType() == 1) {
                Optional<com.fudgeu.playlist.fluxui.elements.Element> elementOptional = this.buildElementFromExtractedXML((Element)childNode, providedProps);
                if (!elementOptional.isEmpty()) {
                    builtChildren.add(elementOptional.get());
                }
            }
        }

        return builtChildren;
    }

    private HashMap<String, Style> getStylesheetLocations(ResourceLocation currentDir, NodeList stylesheetNodes) {
        ResourceManager manager = this.fluxInstance.getResourceManager();
        HashMap<String, Style> allGeneratedStyles = new HashMap<>();

        for (int i = 0; i < stylesheetNodes.getLength(); i++) {
            Node stylesheetNode = stylesheetNodes.item(i);
            if (stylesheetNode.getNodeType() == 1) {
                Element stylesheetElement = (Element)stylesheetNode;
                String location = stylesheetElement.getAttribute("location");
                File file = new File(currentDir.getPath());
                file = new File(file.getParent());
                file = new File(file, location);
                ResourceLocation stylesheetLocation = currentDir.withPath(file.getPath().replace('\\', '/'));

                HashMap<String, Style> styles;
                try {
                    styles = StyleBuilder.buildStylesheet(stylesheetLocation, manager);
                } catch (IOException var13) {
                    this.LOGGER.error("Unable to find stylesheet: " + file.getAbsolutePath());
                    continue;
                }

                allGeneratedStyles.putAll(styles);
            }
        }

        return allGeneratedStyles;
    }

    protected void registerElementBuilder(String elementTag, ElementBuilder elementBuilder) {
        this.elementBuilders.put(elementTag, elementBuilder);
    }

    protected void registerComponentBuilder(String componentTag, ComponentBuilder componentBuilder) {
        this.componentBuilders.put(componentTag, componentBuilder);
    }

    protected boolean isComponent(String componentTag) {
        return this.componentBuilders.containsKey(componentTag);
    }

    private Optional<com.fudgeu.playlist.fluxui.elements.Element> buildComponent(
        ComponentBuilder builder, String id, PropProvider propProvider, ArrayList<com.fudgeu.playlist.fluxui.elements.Element> children
    ) {
        ResourceLocation xmlFile = builder.getComponentLocation();
        Optional<com.fudgeu.playlist.fluxui.elements.Element> containerOpt = this.buildElement("Container", id, new PropProvider());
        if (containerOpt.isEmpty()) {
            this.LOGGER
                .error("Cannot build component from '" + xmlFile + "', cannot build empty parent Container (If you see this, something has gone really wrong)");
            return Optional.empty();
        } else {
            Container container = (Container)containerOpt.get();
            PropProvider childrenProps = builder.build(id, container, propProvider, new State(() -> {}));
            if (childrenProps == null) {
                childrenProps = new PropProvider();
            }

            Optional<Element> preparedElementOptional = this.prepareXMLElement(xmlFile);
            if (preparedElementOptional.isEmpty()) {
                this.LOGGER.error("Cannot build component from '" + xmlFile + "', unable to load child elements properly");
                return Optional.empty();
            } else {
                Optional<com.fudgeu.playlist.fluxui.elements.Element> elementOpt = this.buildElementFromExtractedXML(
                    preparedElementOptional.get(), childrenProps
                );
                if (elementOpt.isEmpty()) {
                    this.LOGGER.error("Cannot build component from '" + xmlFile + "', unable to build child elements");
                    return Optional.empty();
                } else {
                    com.fudgeu.playlist.fluxui.elements.Element element = elementOpt.get();
                    container.addChild(element);
                    if (!children.isEmpty()) {
                        Optional<com.fudgeu.playlist.fluxui.elements.Element> insertionPointOpt = container.findChildById("_ChildInsertionPoint");
                        if (insertionPointOpt.isPresent()) {
                            com.fudgeu.playlist.fluxui.elements.Element insertionPoint = insertionPointOpt.get();
                            Optional<com.fudgeu.playlist.fluxui.elements.Element> insertionPointParentOpt = insertionPoint.getParent();
                            if (insertionPointParentOpt.isPresent() && insertionPointParentOpt.get() instanceof Container parent) {
                                Optional<Integer> positionOpt = parent.getChildPosition("_ChildInsertionPoint");
                                if (positionOpt.isPresent()) {
                                    int position = positionOpt.get();

                                    for (int i = 0; i < children.size(); i++) {
                                        parent.insertChildAfter(position + i, children.get(i));
                                    }

                                    parent.removeChildAt(position);
                                }
                            }
                        }
                    }

                    builder.onLoadFinished(container, propProvider);
                    container.getChildAt(0).ifPresent(component -> {
                        Sizing compWidth = component.getStyle().getWidth();
                        Sizing compHeight = component.getStyle().getHeight();
                        if (compWidth.getSizingType() == Sizing.SizingType.DEPENDENT) {
                            container.getStyle().setWidth(Sizing.percent(1.0F));
                        }

                        if (compHeight.getSizingType() == Sizing.SizingType.DEPENDENT) {
                            container.getStyle().setHeight(Sizing.percent(1.0F));
                        }
                    });
                    return Optional.of(container);
                }
            }
        }
    }

    protected Optional<com.fudgeu.playlist.fluxui.elements.Element> buildElement(String tag, String id, PropProvider propProvider) {
        if (propProvider == null) {
            propProvider = new PropProvider();
        }

        ElementBuilder elementBuilder = this.elementBuilders.get(tag);
        if (elementBuilder == null) {
            this.LOGGER.error("Unable to find registered builder '" + tag + "', not building component");
            return Optional.empty();
        } else {
            if (!elementBuilder.getPropTemplate().checkSelfAgainst(propProvider)) {
                this.LOGGER.error("Unable to build element '" + tag + "' with id '" + id + "', props are incomplete");
            }

            return elementBuilder.build(id, propProvider);
        }
    }

    protected Optional<com.fudgeu.playlist.fluxui.elements.Element> buildComponent(
        String tag, String id, PropProvider propProvider, ArrayList<com.fudgeu.playlist.fluxui.elements.Element> children
    ) {
        ComponentBuilder componentBuilder = this.componentBuilders.get(tag);
        if (componentBuilder == null) {
            this.LOGGER.error("Unable to find registered builder '" + tag + "', not building component");
            return Optional.empty();
        } else {
            return this.buildComponent(componentBuilder, id, propProvider, children);
        }
    }
}
