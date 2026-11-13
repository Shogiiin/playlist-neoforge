package com.fudgeu.playlist.fluxui.clickables;

import com.fudgeu.playlist.fluxui.elements.Element;
import java.util.Optional;

public interface FocusController {
    Optional<Element> getControlledChild();
}
