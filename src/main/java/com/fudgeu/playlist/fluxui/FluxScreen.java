package com.fudgeu.playlist.fluxui;

import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.fluxui.clickables.Clickable;
import com.fudgeu.playlist.fluxui.clickables.FocusController;
import com.fudgeu.playlist.fluxui.clickables.Focusable;
import com.fudgeu.playlist.fluxui.clickables.KeyboardInputable;
import com.fudgeu.playlist.fluxui.clickables.MouseButton;
import com.fudgeu.playlist.fluxui.clickables.Scrollable;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.style.InheritedStyles;
import com.fudgeu.playlist.fluxui.style.Style;
import com.fudgeu.playlist.fluxui.style.container.Alignment;
import com.fudgeu.playlist.fluxui.style.container.ContentSpacing;
import com.fudgeu.playlist.fluxui.style.sizing.Sizing;
import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.RenderUtils;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class FluxScreen extends Screen {
    private final FluxInstance fluxInstance;
    private final Minecraft mcClient;
    private final StateManager stateManager;
    private Container root;
    private final Element toAdd;
    private Clickable currentlyHovered;
    private Element focusedElement;

    protected FluxScreen(Component title, Element e) {
        super(title);
        this.toAdd = e;
        this.fluxInstance = FluxInstance.getInstance();
        this.mcClient = Minecraft.getInstance();
        this.stateManager = PlaylistClient.instance.stateManager;
    }

    public void init() {
        super.init();
        Style rootStyle = Style.create()
            .setWidth(Sizing.px(this.width))
            .setContentSpacing(ContentSpacing.SPACED_EVENLY)
            .setContentAlignment(Alignment.CENTER)
            .setHeight(Sizing.px(this.height));
        this.root = new Container(rootStyle, null, null, null, null, null, false, null, null);
        this.root.addChild(this.toAdd);
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        // Background is drawn explicitly at the start of render(); Screen.render() would otherwise draw it (and blur) twice.
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        boolean darkBackground = this.stateManager.getStateBool("darkenBackground");
        if (darkBackground) {
            RenderUtils.drawRect(0, 0, this.width, this.height, 0.0F, 0.0F, 0.0F, 0.65F);
        }

        RenderUtils.drawRect(0, 0, 108, 12, RGBA.hex(3019023));
        context.drawString(this.mcClient.font, "FluxUI Dev v0.0.1", 12, 2, 16777215, false);
        this.root.draw(context, 0, 0, InheritedStyles.from(this.root.getStyle()));
        this.fluxInstance.tick(delta);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.focusedElement != null) {
            Focusable focusable = (Focusable)this.focusedElement;
            focusable.setFocused(false);
            focusable.onUnFocus();
            this.checkAndToggleFocusController(this.focusedElement, false);
            this.focusedElement = null;
        }

        Element topMostClickable = null;

        for (Element element : this.toAdd.getElementsAt((int)mouseX, (int)mouseY)) {
            if (element instanceof Clickable clickable && clickable.interactable()) {
                topMostClickable = element;
            }
        }

        if (topMostClickable == null) {
            return false;
        } else {
            ((Clickable)topMostClickable).onClick(MouseButton.PRIMARY_CLICK, (int)mouseX, (int)mouseY);
            if (topMostClickable instanceof Focusable focusable) {
                focusable.setFocused(true);
                focusable.onFocus();
                this.focusedElement = topMostClickable;
                Optional<Element> controlledChildOpt = this.checkAndToggleFocusController(this.focusedElement, true);
                Optional<Clickable> clickableOpt = controlledChildOpt.map(elementx -> elementx instanceof Clickable c ? c : null);
                clickableOpt.ifPresent(c -> c.onClick(MouseButton.PRIMARY_CLICK, (int)mouseX, (int)mouseY));
            }

            return true;
        }
    }

    public void mouseMoved(double mouseX, double mouseY) {
        Clickable topMostHoverable = null;

        for (Element element : this.toAdd.getElementsAt((int)mouseX, (int)mouseY)) {
            if (element instanceof Clickable clickable && clickable.interactable()) {
                topMostHoverable = clickable;
            }
        }

        if (this.currentlyHovered != null && topMostHoverable != this.currentlyHovered) {
            this.currentlyHovered.onUnHover();
            this.currentlyHovered = null;
        }

        if (topMostHoverable != this.currentlyHovered) {
            topMostHoverable.onHover(() -> (float)mouseX, () -> (float)mouseY);
            this.currentlyHovered = topMostHoverable;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double amount) {
        Scrollable topMostScrollable = null;

        for (Element element : this.toAdd.getElementsAt((int)mouseX, (int)mouseY)) {
            if (element instanceof Scrollable scrollable && scrollable.isScrollable()) {
                topMostScrollable = scrollable;
            }
        }

        if (topMostScrollable == null) {
            return false;
        } else {
            topMostScrollable.onScroll((int)mouseX, (int)mouseY, (int)amount);
            return true;
        }
    }

    public boolean charTyped(char chr, int modifiers) {
        if (this.focusedElement == null) {
            return false;
        } else if (this.focusedElement instanceof KeyboardInputable inputable) {
            inputable.onCharPressed(chr);
            if (this.focusedElement instanceof FocusController focusController) {
                Optional<KeyboardInputable> controlledChildOpt = focusController.getControlledChild().map(e -> e instanceof KeyboardInputable k ? k : null);
                controlledChildOpt.ifPresent(k -> k.onCharPressed(chr));
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else if (this.focusedElement == null) {
            return false;
        } else if (this.focusedElement instanceof KeyboardInputable inputable) {
            inputable.onKeyPressed(keyCode);
            if (this.focusedElement instanceof FocusController focusController) {
                Optional<KeyboardInputable> controlledChildOpt = focusController.getControlledChild().map(e -> e instanceof KeyboardInputable k ? k : null);
                controlledChildOpt.ifPresent(k -> k.onKeyPressed(keyCode));
            }

            return true;
        } else {
            return false;
        }
    }

    private Optional<Element> checkAndToggleFocusController(Element e, boolean focused) {
        if (e instanceof FocusController controller) {
            Optional<Element> elementOpt = controller.getControlledChild();
            Optional<Focusable> focusableOpt = elementOpt.map(element -> element instanceof Focusable f ? f : null);
            focusableOpt.ifPresent(f -> {
                f.setFocused(focused);
                if (focused) {
                    f.onFocus();
                } else {
                    f.onUnFocus();
                }
            });
            return elementOpt;
        } else {
            return Optional.empty();
        }
    }
}
