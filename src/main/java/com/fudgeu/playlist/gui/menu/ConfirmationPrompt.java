package com.fudgeu.playlist.gui.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;

public class ConfirmationPrompt extends MenuScreen {
    private Component message;
    private ConfirmationPrompt.SimplePressAction confirmAction;
    private ConfirmationPrompt.SimplePressAction denyAction;

    public ConfirmationPrompt(
        Screen previousScreen,
        Component title,
        Component message,
        ConfirmationPrompt.SimplePressAction confirmAction,
        ConfirmationPrompt.SimplePressAction denyAction
    ) {
        super(title, previousScreen);
        this.message = message;
        this.confirmAction = confirmAction;
        this.denyAction = denyAction;
    }

    @Override
    public void init() {
        this.setMainContentHeight(12);
        super.init();
        this.addTextLabel(0, 0, this.message, 16777215);
        this.setPrimaryMenuButton(Component.translatable("generic.no"), onPress -> {
            this.denyAction.onPress();
            this.closeScreen();
        });
        this.setSecondaryMenuButton(Component.translatable("generic.yes"), onPress -> {
            this.confirmAction.onPress();
            this.closeScreen();
        });
    }

    public interface SimplePressAction {
        void onPress();
    }
}
