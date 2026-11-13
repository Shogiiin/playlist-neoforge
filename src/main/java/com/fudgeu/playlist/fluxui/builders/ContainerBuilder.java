package com.fudgeu.playlist.fluxui.builders;

import com.fudgeu.playlist.fluxui.clickables.Clickable;
import com.fudgeu.playlist.fluxui.clickables.KeyboardInputable;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import java.util.Optional;

public class ContainerBuilder implements ElementBuilder {
    private PropTemplate propTemplate = new PropTemplate();

    public ContainerBuilder() {
        this.propTemplate.optionalClickHandler("onClick");
        this.propTemplate.optionalHoverHandler("onHover");
        this.propTemplate.optionalUnHoverHandler("onUnHover");
        this.propTemplate.optionalKeyboardHandler("onKeyboardInput");
        this.propTemplate.optionalCharacterHandler("onCharacterInput");
        this.propTemplate.requireBoolProp("focusable", new BoolProp(false));
        this.propTemplate.optionalRunnable("onFocus");
        this.propTemplate.optionalRunnable("onUnFocus");
    }

    @Override
    public Optional<Element> build(String id, PropProvider propProvider) {
        Clickable.ClickHandler onClickHandler = propProvider.getClickHandler("onClick");
        Clickable.HoverHandler onHoverHandler = propProvider.getHoverHandler("onHover");
        Clickable.UnHoverHandler onUnHoverHandler = propProvider.getUnHoverHandler("onUnHover");
        KeyboardInputable.KeyboardHandler keyboardHandler = propProvider.getKeyboardHandler("onKeyboardInput");
        KeyboardInputable.CharacterHandler characterHandler = propProvider.getCharacterHandler("onCharacterInput");
        boolean focusable = propProvider.getBoolProp("focusable").get();
        Runnable onFocus = propProvider.getRunnable("onFocus");
        Runnable onUnFocus = propProvider.getRunnable("onUnFocus");
        return Optional.of(
            new Container(id, onClickHandler, onHoverHandler, onUnHoverHandler, keyboardHandler, characterHandler, focusable, onFocus, onUnFocus)
        );
    }

    @Override
    public PropTemplate getPropTemplate() {
        return this.propTemplate;
    }
}
