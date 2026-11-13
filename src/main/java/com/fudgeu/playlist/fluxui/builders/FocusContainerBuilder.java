package com.fudgeu.playlist.fluxui.builders;

import com.fudgeu.playlist.fluxui.clickables.Clickable;
import com.fudgeu.playlist.fluxui.clickables.KeyboardInputable;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.elements.FocusContainer;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import java.util.Optional;

public class FocusContainerBuilder implements ElementBuilder {
    private PropTemplate propTemplate = new PropTemplate();

    public FocusContainerBuilder() {
        this.propTemplate.optionalClickHandler("onClick");
        this.propTemplate.requireHoverHandler("onHover", (e, x, y) -> {});
        this.propTemplate.requireUnHoverHandler("onUnHover", e -> {});
        this.propTemplate.optionalKeyboardHandler("onKeyboardInput");
        this.propTemplate.optionalCharacterHandler("onCharacterInput");
        this.propTemplate.optionalRunnable("onFocus");
        this.propTemplate.optionalRunnable("onUnFocus");
        this.propTemplate.requireString("controlledChild");
    }

    @Override
    public Optional<Element> build(String id, PropProvider propProvider) {
        Clickable.ClickHandler onClickHandler = propProvider.getClickHandler("onClick");
        Clickable.HoverHandler onHoverHandler = propProvider.getHoverHandler("onHover");
        Clickable.UnHoverHandler onUnHoverHandler = propProvider.getUnHoverHandler("onUnHover");
        KeyboardInputable.KeyboardHandler keyboardHandler = propProvider.getKeyboardHandler("onKeyboardInput");
        KeyboardInputable.CharacterHandler characterHandler = propProvider.getCharacterHandler("onCharacterInput");
        Runnable onFocus = propProvider.getRunnable("onFocus");
        Runnable onUnFocus = propProvider.getRunnable("onUnFocus");
        String controlledChild = propProvider.getString("controlledChild");
        boolean focusable = true;
        return Optional.of(
            new FocusContainer(
                id, controlledChild, onClickHandler, onHoverHandler, onUnHoverHandler, keyboardHandler, characterHandler, focusable, onFocus, onUnFocus
            )
        );
    }

    @Override
    public PropTemplate getPropTemplate() {
        return this.propTemplate;
    }
}
