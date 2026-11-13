package com.fudgeu.playlist.fluxui.elements;

import com.fudgeu.playlist.fluxui.clickables.Clickable;
import com.fudgeu.playlist.fluxui.clickables.FocusController;
import com.fudgeu.playlist.fluxui.clickables.KeyboardInputable;
import java.util.Optional;

public class FocusContainer extends Container implements FocusController {
    private final String controlledChild;

    public FocusContainer(
        String id,
        String controlledChild,
        Clickable.ClickHandler onClickHandler,
        Clickable.HoverHandler onHoverHandler,
        Clickable.UnHoverHandler onUnHoverHandler,
        KeyboardInputable.KeyboardHandler keyboardHandler,
        KeyboardInputable.CharacterHandler characterHandler,
        boolean focusable,
        Runnable onFocus,
        Runnable onUnFocus
    ) {
        super(id, onClickHandler, onHoverHandler, onUnHoverHandler, keyboardHandler, characterHandler, focusable, onFocus, onUnFocus);
        this.controlledChild = controlledChild;
    }

    @Override
    public Optional<Element> getControlledChild() {
        return this.findChildById(this.controlledChild);
    }
}
