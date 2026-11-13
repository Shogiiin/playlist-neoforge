package com.fudgeu.playlist.fluxui.props;

import com.fudgeu.playlist.fluxui.clickables.Clickable;
import com.fudgeu.playlist.fluxui.clickables.KeyboardInputable;
import com.fudgeu.playlist.fluxui.elements.Element;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropTemplate {
    private final Logger LOGGER = LogManager.getLogger();
    private final ArrayList<String> requiredIntegers = new ArrayList<>();
    private final HashMap<String, Integer> integerDefaults = new HashMap<>();
    private final ArrayList<String> requiredStrings = new ArrayList<>();
    private final ArrayList<String> optionalStrings = new ArrayList<>();
    private final HashMap<String, String> stringDefaults = new HashMap<>();
    private final ArrayList<String> requiredClickHandlers = new ArrayList<>();
    private final ArrayList<String> optionalClickHandlers = new ArrayList<>();
    private final HashMap<String, Clickable.ClickHandler> clickHandlerDefaults = new HashMap<>();
    private final ArrayList<String> requiredHoverHandlers = new ArrayList<>();
    private final ArrayList<String> optionalHoverHandlers = new ArrayList<>();
    private final HashMap<String, Clickable.HoverHandler> hoverHandlerDefaults = new HashMap<>();
    private final ArrayList<String> requiredUnHoverHandlers = new ArrayList<>();
    private final ArrayList<String> optionalUnHoverHandlers = new ArrayList<>();
    private final HashMap<String, Clickable.UnHoverHandler> unHoverHandlerDefaults = new HashMap<>();
    private final ArrayList<String> requiredKeyboardHandlers = new ArrayList<>();
    private final ArrayList<String> optionalKeyboardHandlers = new ArrayList<>();
    private final HashMap<String, KeyboardInputable.KeyboardHandler> keyboardHandlerDefaults = new HashMap<>();
    private final ArrayList<String> requiredCharacterHandlers = new ArrayList<>();
    private final ArrayList<String> optionalCharacterHandlers = new ArrayList<>();
    private final HashMap<String, KeyboardInputable.CharacterHandler> characterHandlerDefaults = new HashMap<>();
    private final ArrayList<String> requiredElementConsumers = new ArrayList<>();
    private final HashMap<String, Consumer<Element>> elementConsumerDefaults = new HashMap<>();
    private final ArrayList<String> requiredStringConsumers = new ArrayList<>();
    private final HashMap<String, Consumer<String>> stringConsumerDefaults = new HashMap<>();
    private final ArrayList<String> requiredRunnables = new ArrayList<>();
    private final ArrayList<String> optionalRunnables = new ArrayList<>();
    private final HashMap<String, Runnable> runnableDefaults = new HashMap<>();
    private final ArrayList<String> requiredBoolProps = new ArrayList<>();
    private final HashMap<String, BoolProp> boolPropDefaults = new HashMap<>();
    private final ArrayList<String> requiredStringMaps = new ArrayList<>();
    private final ArrayList<String> optionalStringMaps = new ArrayList<>();
    private final HashMap<String, HashMap<String, String>> stringMapDefaults = new HashMap<>();
    private boolean requireChildText = false;
    private String defaultChildText;
    private boolean requireChildElement = false;
    private Element defaultChildElement;

    public void requireInteger(String id) {
        this.requiredIntegers.add(id);
    }

    public void requireInteger(String id, int defaultInteger) {
        this.requireInteger(id);
        this.integerDefaults.put(id, defaultInteger);
    }

    public void requireString(String id) {
        this.requiredStrings.add(id);
    }

    public void requireString(String id, String defaultString) {
        this.requireString(id);
        this.stringDefaults.put(id, defaultString);
    }

    public void optionalString(String id) {
        this.optionalStrings.add(id);
    }

    public void requireClickHandler(String id) {
        this.requiredClickHandlers.add(id);
    }

    public void requireClickHandler(String id, Clickable.ClickHandler defaultClickHandler) {
        this.requireClickHandler(id);
        this.clickHandlerDefaults.put(id, defaultClickHandler);
    }

    public void optionalClickHandler(String id) {
        this.optionalClickHandlers.add(id);
    }

    public void requireHoverHandler(String id) {
        this.requiredHoverHandlers.add(id);
    }

    public void optionalHoverHandler(String id) {
        this.optionalHoverHandlers.add(id);
    }

    public void requireHoverHandler(String id, Clickable.HoverHandler defaultHoverHandler) {
        this.requireHoverHandler(id);
        this.hoverHandlerDefaults.put(id, defaultHoverHandler);
    }

    public void requireUnHoverHandler(String id) {
        this.requiredUnHoverHandlers.add(id);
    }

    public void optionalUnHoverHandler(String id) {
        this.optionalUnHoverHandlers.add(id);
    }

    public void requireUnHoverHandler(String id, Clickable.UnHoverHandler defaultUnHoverHandler) {
        this.requireUnHoverHandler(id);
        this.unHoverHandlerDefaults.put(id, defaultUnHoverHandler);
    }

    public void requireKeyboardHandler(String id) {
        this.requiredKeyboardHandlers.add(id);
    }

    public void optionalKeyboardHandler(String id) {
        this.optionalKeyboardHandlers.add(id);
    }

    public void requireKeyboardHandler(String id, KeyboardInputable.KeyboardHandler defaultKeyboardHandler) {
        this.requireKeyboardHandler(id);
        this.keyboardHandlerDefaults.put(id, defaultKeyboardHandler);
    }

    public void requireCharacterHandler(String id) {
        this.requiredCharacterHandlers.add(id);
    }

    public void optionalCharacterHandler(String id) {
        this.optionalCharacterHandlers.add(id);
    }

    public void requireCharacterHandler(String id, KeyboardInputable.CharacterHandler defaultCharacterHandler) {
        this.requireCharacterHandler(id);
        this.characterHandlerDefaults.put(id, defaultCharacterHandler);
    }

    public void requireElementConsumer(String id) {
        this.requiredElementConsumers.add(id);
    }

    public void requireElementConsumer(String id, Consumer<Element> defaultConsumer) {
        this.requireElementConsumer(id);
        this.elementConsumerDefaults.put(id, defaultConsumer);
    }

    public void requireStringConsumer(String id) {
        this.requiredStringConsumers.add(id);
    }

    public void requireStringConsumer(String id, Consumer<String> defaultConsumer) {
        this.requireStringConsumer(id);
        this.stringConsumerDefaults.put(id, defaultConsumer);
    }

    public void requireRunnable(String id) {
        this.requiredRunnables.add(id);
    }

    public void optionalRunnable(String id) {
        this.optionalRunnables.add(id);
    }

    public void requireRunnable(String id, Runnable defaultRunnable) {
        this.requireRunnable(id);
        this.runnableDefaults.put(id, defaultRunnable);
    }

    public void requireBoolProp(String id) {
        this.requiredBoolProps.add(id);
    }

    public void requireBoolProp(String id, BoolProp defaultBoolProp) {
        this.requireBoolProp(id);
        this.boolPropDefaults.put(id, defaultBoolProp);
    }

    public void requireStringMap(String id) {
        this.requiredStringMaps.add(id);
    }

    public void optionalStringMap(String id) {
        this.optionalStringMaps.add(id);
    }

    public void requireStringMap(String id, HashMap<String, String> defaultStringMap) {
        this.requireStringMap(id);
        this.stringMapDefaults.put(id, defaultStringMap);
    }

    public void requireChildText() {
        this.requireChildText = true;
    }

    public void requireChildText(String defaultText) {
        this.requireChildText();
        this.defaultChildText = defaultText;
    }

    public void requireChildElement() {
        this.requireChildElement = true;
    }

    public void requireChildElement(Element defaultElement) {
        this.requireChildText();
        this.defaultChildElement = defaultElement;
    }

    public PropProvider buildProviderFrom(HashMap<String, String> rawProps, PropProvider givenProps) {
        PropProvider newPropProvider = new PropProvider();

        for (String propId : this.requiredIntegers) {
            String rawValue = rawProps.get(propId);
            if (rawValue != null && rawValue.startsWith("$")) {
                int realValue = givenProps.getInteger(rawValue.substring(1));
                newPropProvider.setInteger(propId, realValue);
            } else if (rawValue != null) {
                newPropProvider.setInteger(propId, Integer.parseInt(rawValue));
            }
        }

        for (String propIdx : this.requiredStrings) {
            String rawValue = rawProps.get(propIdx);
            if (rawValue != null && rawValue.startsWith("$")) {
                String realValue = givenProps.getString(rawValue.substring(1));
                newPropProvider.setString(propIdx, realValue);
            } else {
                newPropProvider.setString(propIdx, rawValue);
            }
        }

        for (String propIdxx : this.optionalStrings) {
            String rawValue = rawProps.get(propIdxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                String realValue = givenProps.getString(rawValue.substring(1));
                newPropProvider.setString(propIdxx, realValue);
            } else {
                newPropProvider.setString(propIdxx, rawValue);
            }
        }

        for (String propIdxxx : this.requiredClickHandlers) {
            String rawValue = rawProps.get(propIdxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                Clickable.ClickHandler realValue = givenProps.getClickHandler(rawValue.substring(1));
                newPropProvider.setClickHandler(propIdxxx, realValue);
            }
        }

        for (String propIdxxxx : this.optionalClickHandlers) {
            String rawValue = rawProps.get(propIdxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                Clickable.ClickHandler realValue = givenProps.getClickHandler(rawValue.substring(1));
                newPropProvider.setClickHandler(propIdxxxx, realValue);
            }
        }

        for (String propIdxxxxx : this.requiredHoverHandlers) {
            String rawValue = rawProps.get(propIdxxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                Clickable.HoverHandler realValue = givenProps.getHoverHandler(rawValue.substring(1));
                newPropProvider.setHoverHandler(propIdxxxxx, realValue);
            }
        }

        for (String propIdxxxxxx : this.optionalHoverHandlers) {
            String rawValue = rawProps.get(propIdxxxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                Clickable.HoverHandler realValue = givenProps.getHoverHandler(rawValue.substring(1));
                newPropProvider.setHoverHandler(propIdxxxxxx, realValue);
            }
        }

        for (String propIdxxxxxxx : this.requiredUnHoverHandlers) {
            String rawValue = rawProps.get(propIdxxxxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                Clickable.UnHoverHandler realValue = givenProps.getUnHoverHandler(rawValue.substring(1));
                newPropProvider.setUnHoverHandler(propIdxxxxxxx, realValue);
            }
        }

        for (String propIdxxxxxxxx : this.optionalUnHoverHandlers) {
            String rawValue = rawProps.get(propIdxxxxxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                Clickable.UnHoverHandler realValue = givenProps.getUnHoverHandler(rawValue.substring(1));
                newPropProvider.setUnHoverHandler(propIdxxxxxxxx, realValue);
            }
        }

        for (String propIdxxxxxxxxx : this.requiredKeyboardHandlers) {
            String rawValue = rawProps.get(propIdxxxxxxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                KeyboardInputable.KeyboardHandler realValue = givenProps.getKeyboardHandler(rawValue.substring(1));
                newPropProvider.setKeyboardHandler(propIdxxxxxxxxx, realValue);
            }
        }

        for (String propIdxxxxxxxxxx : this.requiredCharacterHandlers) {
            String rawValue = rawProps.get(propIdxxxxxxxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                KeyboardInputable.CharacterHandler realValue = givenProps.getCharacterHandler(rawValue.substring(1));
                newPropProvider.setCharacterHandler(propIdxxxxxxxxxx, realValue);
            }
        }

        for (String propIdxxxxxxxxxxx : this.optionalKeyboardHandlers) {
            String rawValue = rawProps.get(propIdxxxxxxxxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                KeyboardInputable.KeyboardHandler realValue = givenProps.getKeyboardHandler(rawValue.substring(1));
                newPropProvider.setKeyboardHandler(propIdxxxxxxxxxxx, realValue);
            }
        }

        for (String propIdxxxxxxxxxxxx : this.requiredElementConsumers) {
            String rawValue = rawProps.get(propIdxxxxxxxxxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                Consumer<Element> realValue = givenProps.getElementConsumer(rawValue.substring(1));
                newPropProvider.setElementConsumer(propIdxxxxxxxxxxxx, realValue);
            }
        }

        for (String propIdxxxxxxxxxxxxx : this.requiredStringConsumers) {
            String rawValue = rawProps.get(propIdxxxxxxxxxxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                Consumer<String> realValue = givenProps.getStringConsumer(rawValue.substring(1));
                newPropProvider.setStringConsumer(propIdxxxxxxxxxxxxx, realValue);
            }
        }

        for (String propIdxxxxxxxxxxxxxx : this.requiredRunnables) {
            String rawValue = rawProps.get(propIdxxxxxxxxxxxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                Runnable realValue = givenProps.getRunnable(rawValue.substring(1));
                newPropProvider.setRunnable(propIdxxxxxxxxxxxxxx, realValue);
            }
        }

        for (String propIdxxxxxxxxxxxxxxx : this.optionalRunnables) {
            String rawValue = rawProps.get(propIdxxxxxxxxxxxxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                Runnable realValue = givenProps.getRunnable(rawValue.substring(1));
                newPropProvider.setRunnable(propIdxxxxxxxxxxxxxxx, realValue);
            }
        }

        for (String propIdxxxxxxxxxxxxxxxx : this.requiredBoolProps) {
            String rawValue = rawProps.get(propIdxxxxxxxxxxxxxxxx);
            if (rawValue != null) {
                BoolProp parsedValue = null;
                if (rawValue.startsWith("$")) {
                    parsedValue = givenProps.getBoolProp(rawValue.substring(1));
                } else if (rawValue.equals("true")) {
                    parsedValue = new BoolProp(true);
                } else if (rawValue.equals("false")) {
                    parsedValue = new BoolProp(false);
                }

                if (parsedValue != null) {
                    newPropProvider.setBoolProp(propIdxxxxxxxxxxxxxxxx, parsedValue);
                }
            }
        }

        for (String propIdxxxxxxxxxxxxxxxxx : this.requiredStringMaps) {
            String rawValue = rawProps.get(propIdxxxxxxxxxxxxxxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                HashMap<String, String> realValue = givenProps.getStringMap(rawValue.substring(1));
                newPropProvider.setStringMap(propIdxxxxxxxxxxxxxxxxx, realValue);
            }
        }

        for (String propIdxxxxxxxxxxxxxxxxxx : this.optionalStringMaps) {
            String rawValue = rawProps.get(propIdxxxxxxxxxxxxxxxxxx);
            if (rawValue != null && rawValue.startsWith("$")) {
                HashMap<String, String> realValue = givenProps.getStringMap(rawValue.substring(1));
                newPropProvider.setStringMap(propIdxxxxxxxxxxxxxxxxxx, realValue);
            }
        }

        return !this.checkSelfAgainst(newPropProvider) ? null : newPropProvider;
    }

    public boolean checkSelfAgainst(PropProvider propProvider) {
        return this.verifyPropsInType(this.requiredIntegers, this.integerDefaults, propProvider::getInteger, propProvider::setInteger)
            && this.verifyPropsInType(this.requiredStrings, this.stringDefaults, propProvider::getString, propProvider::setString)
            && this.verifyPropsInType(this.requiredClickHandlers, this.clickHandlerDefaults, propProvider::getClickHandler, propProvider::setClickHandler)
            && this.verifyPropsInType(this.requiredHoverHandlers, this.hoverHandlerDefaults, propProvider::getHoverHandler, propProvider::setHoverHandler)
            && this.verifyPropsInType(
                this.requiredUnHoverHandlers, this.unHoverHandlerDefaults, propProvider::getUnHoverHandler, propProvider::setUnHoverHandler
            )
            && this.verifyPropsInType(
                this.requiredKeyboardHandlers, this.keyboardHandlerDefaults, propProvider::getKeyboardHandler, propProvider::setKeyboardHandler
            )
            && this.verifyPropsInType(
                this.requiredCharacterHandlers, this.characterHandlerDefaults, propProvider::getCharacterHandler, propProvider::setCharacterHandler
            )
            && this.verifyPropsInType(
                this.requiredElementConsumers, this.elementConsumerDefaults, propProvider::getElementConsumer, propProvider::setElementConsumer
            )
            && this.verifyPropsInType(
                this.requiredStringConsumers, this.stringConsumerDefaults, propProvider::getStringConsumer, propProvider::setStringConsumer
            )
            && this.verifyPropsInType(this.requiredRunnables, this.runnableDefaults, propProvider::getRunnable, propProvider::setRunnable)
            && this.verifyPropsInType(this.requiredBoolProps, this.boolPropDefaults, propProvider::getBoolProp, propProvider::setBoolProp)
            && this.verifyPropsInType(this.requiredStringMaps, this.stringMapDefaults, propProvider::getStringMap, propProvider::setStringMap)
            && this.verifyChild(this.requireChildText, this.defaultChildText, propProvider::getChildText, propProvider::setChildText)
            && this.verifyChild(this.requireChildElement, this.defaultChildElement, propProvider::getChildElement, propProvider::setChildElement);
    }

    private <T> boolean verifyPropsInType(
        ArrayList<String> allRequiredProps, HashMap<String, T> propDefaults, Function<String, T> propGetter, BiConsumer<String, T> propSetter
    ) {
        for (String id : allRequiredProps) {
            if (propGetter.apply(id) == null) {
                T defaultValue = propDefaults.get(id);
                if (defaultValue == null) {
                    this.LOGGER.error("Failed to verify missing element prop: " + id);
                    return false;
                }

                propSetter.accept(id, defaultValue);
            }
        }

        return true;
    }

    private <T> boolean verifyChild(boolean isRequired, T defaultChild, Supplier<T> propGetter, Consumer<T> propSetter) {
        if (!isRequired) {
            return true;
        } else {
            if (propGetter.get() == null && defaultChild != null) {
                propSetter.accept(defaultChild);
            }

            this.LOGGER.error("Failed to verify missing element prop child, errors may occur");
            return false;
        }
    }
}
