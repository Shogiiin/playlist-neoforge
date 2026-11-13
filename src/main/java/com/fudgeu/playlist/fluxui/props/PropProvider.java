package com.fudgeu.playlist.fluxui.props;

import com.fudgeu.playlist.fluxui.clickables.Clickable;
import com.fudgeu.playlist.fluxui.clickables.KeyboardInputable;
import com.fudgeu.playlist.fluxui.elements.Element;
import java.util.HashMap;
import java.util.function.Consumer;

public class PropProvider {
    private final HashMap<String, Clickable.ClickHandler> clickHandlers = new HashMap<>();
    private final HashMap<String, Clickable.HoverHandler> hoverHandlers = new HashMap<>();
    private final HashMap<String, Clickable.UnHoverHandler> unHoverHandlers = new HashMap<>();
    private final HashMap<String, KeyboardInputable.KeyboardHandler> keyboardHandlers = new HashMap<>();
    private final HashMap<String, KeyboardInputable.CharacterHandler> characterHandlers = new HashMap<>();
    private final HashMap<String, Integer> integers = new HashMap<>();
    private final HashMap<String, String> strings = new HashMap<>();
    private final HashMap<String, Consumer<Element>> elementConsumers = new HashMap<>();
    private final HashMap<String, Consumer<String>> stringConsumers = new HashMap<>();
    private final HashMap<String, Runnable> runnables = new HashMap<>();
    private final HashMap<String, BoolProp> boolProps = new HashMap<>();
    private final HashMap<String, HashMap<String, String>> stringMaps = new HashMap<>();
    private String childText = "";
    private Element childElement;

    public void setClickHandler(String id, Clickable.ClickHandler clickHandler) {
        this.clickHandlers.put(id, clickHandler);
    }

    public void setHoverHandler(String id, Clickable.HoverHandler hoverHandler) {
        this.hoverHandlers.put(id, hoverHandler);
    }

    public void setUnHoverHandler(String id, Clickable.UnHoverHandler unHoverHandler) {
        this.unHoverHandlers.put(id, unHoverHandler);
    }

    public void setKeyboardHandler(String id, KeyboardInputable.KeyboardHandler keyboardHandler) {
        this.keyboardHandlers.put(id, keyboardHandler);
    }

    public void setCharacterHandler(String id, KeyboardInputable.CharacterHandler characterHandler) {
        this.characterHandlers.put(id, characterHandler);
    }

    public void setInteger(String id, Integer integer) {
        this.integers.put(id, integer);
    }

    public void setString(String id, String string) {
        this.strings.put(id, string);
    }

    public void setElementConsumer(String id, Consumer<Element> elementConsumer) {
        this.elementConsumers.put(id, elementConsumer);
    }

    public void setStringConsumer(String id, Consumer<String> stringConsumer) {
        this.stringConsumers.put(id, stringConsumer);
    }

    public void setRunnable(String id, Runnable runnable) {
        this.runnables.put(id, runnable);
    }

    public void setBoolProp(String id, BoolProp boolProp) {
        this.boolProps.put(id, boolProp);
    }

    public void setStringMap(String id, HashMap<String, String> stringMap) {
        this.stringMaps.put(id, stringMap);
    }

    public Clickable.ClickHandler getClickHandler(String id) {
        return this.clickHandlers.get(id);
    }

    public Clickable.HoverHandler getHoverHandler(String id) {
        return this.hoverHandlers.get(id);
    }

    public Clickable.UnHoverHandler getUnHoverHandler(String id) {
        return this.unHoverHandlers.get(id);
    }

    public KeyboardInputable.KeyboardHandler getKeyboardHandler(String id) {
        return this.keyboardHandlers.get(id);
    }

    public KeyboardInputable.CharacterHandler getCharacterHandler(String id) {
        return this.characterHandlers.get(id);
    }

    public Integer getInteger(String id) {
        return this.integers.get(id);
    }

    public String getString(String id) {
        return this.strings.get(id);
    }

    public Consumer<Element> getElementConsumer(String id) {
        return this.elementConsumers.get(id);
    }

    public Consumer<String> getStringConsumer(String id) {
        return this.stringConsumers.get(id);
    }

    public Runnable getRunnable(String id) {
        return this.runnables.get(id);
    }

    public BoolProp getBoolProp(String id) {
        return this.boolProps.get(id);
    }

    public HashMap<String, String> getStringMap(String id) {
        return this.stringMaps.get(id);
    }

    public String getChildText() {
        return this.childText;
    }

    public void setChildText(String childText) {
        this.childText = childText;
    }

    public Element getChildElement() {
        return this.childElement;
    }

    public void setChildElement(Element childElement) {
        this.childElement = childElement;
    }
}
