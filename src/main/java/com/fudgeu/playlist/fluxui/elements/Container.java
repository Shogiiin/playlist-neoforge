package com.fudgeu.playlist.fluxui.elements;

import com.fudgeu.playlist.fluxui.FluxHelper;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.backgrounddrawer.BackgroundDrawer;
import com.fudgeu.playlist.fluxui.backgrounddrawer.BackgroundDrawers;
import com.fudgeu.playlist.fluxui.clickables.Clickable;
import com.fudgeu.playlist.fluxui.clickables.Focusable;
import com.fudgeu.playlist.fluxui.clickables.KeyboardInputable;
import com.fudgeu.playlist.fluxui.clickables.MouseButton;
import com.fudgeu.playlist.fluxui.clickables.Scrollable;
import com.fudgeu.playlist.fluxui.style.InheritedStyles;
import com.fudgeu.playlist.fluxui.style.Style;
import com.fudgeu.playlist.fluxui.style.color.Color;
import com.fudgeu.playlist.fluxui.style.color.ColorStyle;
import com.fudgeu.playlist.fluxui.style.color.SolidColor;
import com.fudgeu.playlist.fluxui.style.container.Alignment;
import com.fudgeu.playlist.fluxui.style.container.ContentSpacing;
import com.fudgeu.playlist.fluxui.style.container.LayoutDirection;
import com.fudgeu.playlist.fluxui.style.sizing.Sizing;
import com.fudgeu.playlist.fluxui.transitions.TransitionHelper;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;

public class Container implements Element, Clickable, Scrollable, Focusable, KeyboardInputable {
    private final FluxInstance fluxInstance = FluxInstance.getInstance();
    private final TransitionHelper transitionHelper = this.fluxInstance.getTransitionHelper();
    private String id;
    private Element parent;
    private Style style;
    private final BackgroundDrawer backgroundDrawer;
    private final ArrayList<Element> children = new ArrayList<>();
    private boolean isScrollable = false;
    private int scrollOffset = 0;
    private UUID scrollHideTimeout;
    private Supplier<Float> scrollbarOpacity = () -> 0.9F;
    private boolean isWidthDirty = true;
    private boolean isHeightDirty = true;
    private int memoX = 0;
    private int memoY = 0;
    private int memoWidth = 0;
    private int memoHeight = 0;
    private boolean interactable = false;
    private Clickable.ClickHandler onClickHandler;
    private Clickable.ClickHandler onReleaseHandler;
    private Clickable.HoverHandler onHoverHandler;
    private Clickable.UnHoverHandler onUnHoverHandler;
    private boolean focusable = false;
    private boolean focused = false;
    private final Runnable onFocus;
    private final Runnable onUnFocus;
    private final KeyboardInputable.KeyboardHandler keyboardHandler;
    private final KeyboardInputable.CharacterHandler characterHandler;

    public Container(
        Style style,
        Clickable.ClickHandler onClickHandler,
        Clickable.HoverHandler onHoverHandler,
        Clickable.UnHoverHandler onUnHoverHandler,
        KeyboardInputable.KeyboardHandler keyboardHandler,
        KeyboardInputable.CharacterHandler characterHandler,
        boolean focusable,
        Runnable onFocus,
        Runnable onUnFocus
    ) {
        this.style = style;
        this.onClickHandler = onClickHandler;
        this.onHoverHandler = onHoverHandler;
        this.onUnHoverHandler = onUnHoverHandler;
        this.keyboardHandler = keyboardHandler;
        this.characterHandler = characterHandler;
        this.focusable = focusable;
        this.onFocus = onFocus;
        this.onUnFocus = onUnFocus;
        this.backgroundDrawer = BackgroundDrawers.SOLID;
        if (onClickHandler != null || focusable) {
            this.interactable = true;
        }
    }

    public Container(
        String id,
        Clickable.ClickHandler onClickHandler,
        Clickable.HoverHandler onHoverHandler,
        Clickable.UnHoverHandler onUnHoverHandler,
        KeyboardInputable.KeyboardHandler keyboardHandler,
        KeyboardInputable.CharacterHandler characterHandler,
        boolean focusable,
        Runnable onFocus,
        Runnable onUnFocus
    ) {
        this.id = id;
        this.style = FluxInstance.getInstance().getStyleOrDefault(id);
        this.onClickHandler = onClickHandler;
        this.onHoverHandler = onHoverHandler;
        this.onUnHoverHandler = onUnHoverHandler;
        this.keyboardHandler = keyboardHandler;
        this.characterHandler = characterHandler;
        this.focusable = focusable;
        this.onFocus = onFocus;
        this.onUnFocus = onUnFocus;
        this.backgroundDrawer = BackgroundDrawers.SOLID;
        this.resetScrollbarHideTimeout();
        if (onClickHandler != null || onHoverHandler != null || focusable) {
            this.interactable = true;
        }
    }

    @Override
    public void draw(GuiGraphics context, int x, int y, InheritedStyles inheritedStyles) {
        x += this.style.getTranslationX();
        y += this.style.getTranslationY();
        this.memoX = x;
        this.memoY = y;
        this.isWidthDirty = true;
        this.isHeightDirty = true;
        context.pose().scale(this.style.getScaleX(), this.style.getScaleY(), 0.0F);
        float xOffset = 1.0F / this.style.getScaleX();
        float yOffset = 1.0F / this.style.getScaleY();
        int originX = (int)(x + this.getWidth() * (1.0F - this.style.getScaleX()) / 2.0F);
        int originY = (int)(y + this.getHeight() * (1.0F - this.style.getScaleY()) / 2.0F);
        int realX = (int)(xOffset * originX);
        int realY = (int)(yOffset * originY);
        InheritedStyles childStyles = inheritedStyles.passDown(this.style);
        if (this.backgroundDrawer != null) {
            ColorStyle backgroundColor = this.style.getBackgroundColor();
            this.backgroundDrawer.draw(context, realX, realY, this.getWidth(), this.getHeight(), childStyles, backgroundColor);
        }

        LayoutDirection layoutDirection = this.style.getLayoutDirection();
        ContentSpacing contentSpacing = this.style.getContentSpacing();
        AtomicInteger mainAxisPos = new AtomicInteger(0);
        AtomicInteger secondaryAxisPos = new AtomicInteger(0);
        int contentGap = 0;
        switch (layoutDirection) {
            case HORIZONTAL:
                mainAxisPos.addAndGet(this.style.getPaddingLeft());
                secondaryAxisPos.addAndGet(this.style.getPaddingTop());
                break;
            case HORIZONTAL_REVERSED:
                mainAxisPos.addAndGet(this.style.getPaddingRight());
                secondaryAxisPos.addAndGet(this.style.getPaddingTop());
                break;
            case VERTICAL:
                mainAxisPos.addAndGet(this.style.getPaddingTop());
                secondaryAxisPos.addAndGet(this.style.getPaddingLeft());
                break;
            case VERTICAL_REVERSED:
                mainAxisPos.addAndGet(this.style.getPaddingBottom());
                secondaryAxisPos.addAndGet(this.style.getPaddingLeft());
        }

        int childrenMainDimension = 0;
        int containerMainDimension = 0;
        int containerSecondaryDimension = 0;
        switch (layoutDirection) {
            case HORIZONTAL:
            case HORIZONTAL_REVERSED:
                childrenMainDimension = this.getChildrenCombinedWidth();
                containerMainDimension = this.getViewportInternalWidth();
                containerSecondaryDimension = this.getViewportInternalHeight();
                break;
            case VERTICAL:
            case VERTICAL_REVERSED:
                childrenMainDimension = this.getChildrenCombinedHeight();
                containerMainDimension = this.getViewportInternalHeight();
                containerSecondaryDimension = this.getViewportInternalWidth();
        }

        int remainingSpace = containerMainDimension - childrenMainDimension;
        int amountChildren = this.children.size();
        switch (contentSpacing) {
            case ADJACENT:
                contentGap = this.style.getGap();
                break;
            case SPACED_BETWEEN:
                if (amountChildren < 2) {
                    return;
                }

                contentGap = remainingSpace / (this.children.size() - 1);
                break;
            case SPACED_AROUND: {
                if (amountChildren == 0) {
                    return;
                }

                int spacing = Math.round((float)remainingSpace / this.children.size());
                mainAxisPos.addAndGet(Math.round(spacing / 2.0F));
                contentGap = spacing;
                break;
            }
            case SPACED_EVENLY: {
                int spacing = remainingSpace / (this.children.size() + 1);
                mainAxisPos.addAndGet(spacing);
                contentGap = spacing;
            }
        }

        Container.ElementDrawer elementDrawer = (e, secondaryOffset) -> {};
        int paddingTop = this.style.getPaddingTop();
        switch (layoutDirection) {
            case HORIZONTAL:
                elementDrawer = (e, secondaryOffset) -> {
                    int drawX = originX + mainAxisPos.get();
                    int drawY = originY + secondaryAxisPos.get() + secondaryOffset;
                    e.draw(context, (int)(drawX * xOffset), (int)(drawY * yOffset), childStyles);
                    mainAxisPos.addAndGet((int)(e.getWidth() * this.style.getScaleX()));
                };
                break;
            case HORIZONTAL_REVERSED:
                elementDrawer = (e, secondaryOffset) -> {
                    int drawX = originX + this.memoWidth - mainAxisPos.get() - e.getWidth();
                    int drawY = originY + secondaryAxisPos.get() + secondaryOffset;
                    e.draw(context, (int)(drawX * xOffset), (int)(drawY * yOffset), childStyles);
                    mainAxisPos.addAndGet((int)(e.getWidth() * this.style.getScaleX()));
                };
                break;
            case VERTICAL:
                elementDrawer = (e, secondaryOffset) -> {
                    int drawX = originX + secondaryAxisPos.get() + secondaryOffset;
                    int drawY = originY + mainAxisPos.get() - this.scrollOffset;
                    int scaledDrawX = (int)(drawX * xOffset);
                    int scaledDrawY = (int)(drawY * yOffset);
                    int elementHeight = e.getHeight();
                    if (scaledDrawY + elementHeight * yOffset <= realY + (paddingTop + this.getViewportInternalHeight()) * yOffset
                        && scaledDrawY >= realY + paddingTop * yOffset) {
                        e.draw(context, scaledDrawX, scaledDrawY, childStyles);
                    }

                    mainAxisPos.addAndGet((int)(e.getHeight() * this.style.getScaleY()));
                };
        }

        for (Element e : this.children) {
            int secondaryAxisElementOffset = 0;
            int childSecondaryDimension = 0;
            switch (layoutDirection) {
                case HORIZONTAL:
                case HORIZONTAL_REVERSED:
                    childSecondaryDimension = e.getHeight();
                    break;
                case VERTICAL:
                case VERTICAL_REVERSED:
                    childSecondaryDimension = e.getWidth();
            }

            Alignment elementAlignment = e.getStyle().getSelfAlignment().orElse(this.style.getContentAlignment());
            switch (elementAlignment) {
                case CENTER:
                    secondaryAxisElementOffset = Math.round((containerSecondaryDimension - childSecondaryDimension) / 2.0F);
                    break;
                case END:
                    secondaryAxisElementOffset = containerSecondaryDimension - childSecondaryDimension;
            }

            elementDrawer.draw(e, secondaryAxisElementOffset);
            mainAxisPos.addAndGet(contentGap);
        }

        if (layoutDirection == LayoutDirection.VERTICAL && this.id != null) {
            int internalHeight = this.autoHeight() - this.style.getPaddingTop() - this.style.getPaddingBottom();
            this.isScrollable = internalHeight > this.getViewportInternalHeight();
            if (!this.isScrollable) {
                this.scrollOffset = 0;
            }
        }

        if (this.isScrollable) {
            childStyles.setBorderRadius(0);
            int scrollBarHeight = (int)((double)this.getViewportInternalHeight() / this.getTotalInternalHeight() * this.getViewportInternalHeight());
            int scrollBarOffset = (int)(
                (this.getViewportInternalHeight() - scrollBarHeight)
                    * ((double)this.scrollOffset / (this.getTotalInternalHeight() - this.getViewportInternalHeight()))
            );
            int scrollBarY = (int)((originY + this.style.getPaddingTop()) * yOffset) + scrollBarOffset + 1;
            int scrollBarX = originX + this.style.getPaddingLeft() + this.getViewportInternalWidth() - 3;
            BackgroundDrawers.SOLID
                .draw(
                    context,
                    scrollBarX,
                    scrollBarY,
                    1,
                    scrollBarHeight,
                    childStyles,
                    SolidColor.create(Color.create(1.0F, 1.0F, 1.0F, this.scrollbarOpacity.get()))
                );
        }
    }

    @Override
    public Optional<String> getId() {
        return Optional.ofNullable(this.id);
    }

    public void setId(String id) {
        this.id = id;
        this.style = this.fluxInstance.getStyleOrDefault(id);
    }

    @Override
    public int getWidth() {
        Sizing dimensionWidth = this.style.getWidth();
        this.memoWidth = dimensionWidth.calculate(Sizing.Dimension.WIDTH, this.parent, this::autoWidth);
        this.isWidthDirty = false;
        return this.memoWidth;
    }

    @Override
    public int getHeight() {
        Sizing dimensionHeight = this.style.getHeight();
        this.memoHeight = dimensionHeight.calculate(Sizing.Dimension.HEIGHT, this.parent, this::autoHeight);
        Sizing dimensionMaxHeight = this.style.getMaxHeight();
        if (dimensionMaxHeight.getSizingType() != Sizing.SizingType.AUTO) {
            int maxHeight = dimensionMaxHeight.calculate(Sizing.Dimension.HEIGHT, this.parent, this::autoHeight);
            this.memoHeight = Math.min(this.memoHeight, maxHeight);
        }

        this.isHeightDirty = false;
        return this.memoHeight;
    }

    @Override
    public void setParent(Element parent) {
        this.parent = parent;
    }

    @Override
    public Optional<Element> getParent() {
        return Optional.ofNullable(this.parent);
    }

    @Override
    public Style getStyle() {
        return this.style;
    }

    @Override
    public ArrayList<Element> getElementsAt(int x, int y) {
        ArrayList<Element> elements = new ArrayList<>();
        if (FluxHelper.isInside(x, y, this.memoX, this.memoY, this.memoWidth, this.memoHeight)) {
            elements.add(this);
            this.children.forEach(child -> elements.addAll(child.getElementsAt(x, y)));
        }

        return elements;
    }

    @Override
    public boolean interactable() {
        return this.interactable;
    }

    @Override
    public void onClick(MouseButton mouseButton, float x, float y) {
        if (this.onClickHandler != null) {
            this.onClickHandler.handle(this, mouseButton, x, y);
        }
    }

    @Override
    public void onRelease(MouseButton mouseButton, float x, float y) {
        if (this.onReleaseHandler != null) {
            this.onReleaseHandler.handle(this, mouseButton, x, y);
        }
    }

    @Override
    public void onHover(Supplier<Float> x, Supplier<Float> y) {
        if (this.onHoverHandler != null) {
            this.onHoverHandler.handle(this, x, y);
        }
    }

    @Override
    public void onUnHover() {
        if (this.onUnHoverHandler != null) {
            this.onUnHoverHandler.handle(this);
        }
    }

    public Container addChild(Element e) {
        e.setParent(this);
        this.children.add(e);
        this.isWidthDirty = true;
        this.isHeightDirty = true;
        return this;
    }

    public Container insertChildAfter(int pos, Element e) {
        e.setParent(this);
        this.isWidthDirty = true;
        this.isHeightDirty = true;
        this.children.add(pos + 1, e);
        return this;
    }

    public void removeAllChildren() {
        this.children.clear();
        this.isWidthDirty = true;
        this.isHeightDirty = true;
    }

    public void removeChildAt(int pos) {
        if (pos >= 0 && pos < this.children.size()) {
            this.children.remove(pos);
        }
    }

    public Optional<Integer> getChildPosition(String id) {
        for (int i = 0; i < this.children.size(); i++) {
            Optional<String> childId = this.children.get(i).getId();
            if (childId.isPresent() && childId.get().equals(id)) {
                return Optional.of(i);
            }
        }

        return Optional.empty();
    }

    public Optional<Element> getChildAt(int index) {
        return Optional.ofNullable(this.children.get(index));
    }

    public Optional<Element> findChildById(String id) {
        for (Element child : this.children) {
            Optional<String> childIdOpt = child.getId();
            if (!childIdOpt.isEmpty()) {
                String childId = childIdOpt.get();
                if (childId.equals(id)) {
                    return Optional.of(child);
                }

                if (child instanceof Container childContainer) {
                    Optional<Element> potentialFind = childContainer.findChildById(id);
                    if (potentialFind.isPresent()) {
                        return potentialFind;
                    }
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Alignment> getSelfAlignment() {
        return this.style.getSelfAlignment();
    }

    private int autoHeight() {
        int rawHeight = 0;
        switch (this.style.getLayoutDirection()) {
            case HORIZONTAL:
            case HORIZONTAL_REVERSED:
                for (Element e : this.children) {
                    int childHeight = e.getHeight();
                    if (childHeight > rawHeight) {
                        rawHeight = childHeight;
                    }
                }
                break;
            case VERTICAL:
            case VERTICAL_REVERSED:
                rawHeight = this.getChildrenCombinedHeight();
                if (this.style.getContentSpacing() == ContentSpacing.ADJACENT) {
                    int amountOfGaps = this.children.size() - 1;
                    rawHeight += this.style.getGap() * amountOfGaps;
                }
        }

        return rawHeight + this.style.getPaddingTop() + this.style.getPaddingBottom();
    }

    private int autoWidth() {
        int rawWidth = 0;
        switch (this.style.getLayoutDirection()) {
            case HORIZONTAL:
            case HORIZONTAL_REVERSED:
                rawWidth = this.getChildrenCombinedWidth();
                if (this.style.getContentSpacing() == ContentSpacing.ADJACENT) {
                    int amountOfGaps = this.children.size() - 1;
                    rawWidth += this.style.getGap() * amountOfGaps;
                }
                break;
            case VERTICAL:
            case VERTICAL_REVERSED:
                for (Element e : this.children) {
                    int childWidth = e.getWidth();
                    if (childWidth > rawWidth) {
                        rawWidth = childWidth;
                    }
                }
        }

        return rawWidth + this.style.getPaddingLeft() + this.style.getPaddingRight();
    }

    private int getViewportInternalWidth() {
        return this.getWidth() - this.style.getPaddingLeft() - this.style.getPaddingRight();
    }

    private int getViewportInternalHeight() {
        return this.getHeight() - this.style.getPaddingTop() - this.style.getPaddingBottom();
    }

    private int getChildrenCombinedWidth() {
        int combinedWidth = 0;

        for (Element e : this.children) {
            combinedWidth += e.getWidth();
        }

        return combinedWidth;
    }

    private int getChildrenCombinedHeight() {
        int combinedHeight = 0;

        for (Element e : this.children) {
            combinedHeight += e.getHeight();
        }

        return combinedHeight;
    }

    private int getTotalInternalHeight() {
        return this.autoHeight() - this.style.getPaddingTop() - this.style.getPaddingBottom();
    }

    @Override
    public boolean isScrollable() {
        return this.isScrollable;
    }

    @Override
    public void onScroll(int x, int y, int amount) {
        int internalHeight = this.autoHeight() - this.style.getPaddingTop() - this.style.getPaddingBottom();
        this.scrollOffset -= 7 * amount;
        this.scrollOffset = Math.max(0, this.scrollOffset);
        this.scrollOffset = Math.min(internalHeight - this.getViewportInternalHeight(), this.scrollOffset);
        this.resetScrollbarHideTimeout();
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public void onFocus() {
        if (this.onFocus != null) {
            this.onFocus.run();
        }
    }

    @Override
    public void onUnFocus() {
        if (this.onUnFocus != null) {
            this.onUnFocus.run();
        }
    }

    @Override
    public void onKeyPressed(int keyCode) {
        if (this.keyboardHandler != null) {
            this.keyboardHandler.handle(this, keyCode);
        }
    }

    @Override
    public void onCharPressed(char c) {
        if (this.characterHandler != null) {
            this.characterHandler.handle(this, c);
        }
    }

    public String getTree(int depth) {
        StringBuilder tree = new StringBuilder(new String(new char[depth * 2]).replace("\u0000", " "));
        String temp = this.id != null && this.id.equals("") ? "Container" : this.id;
        tree.append("-> ").append(temp).append("\n");
        depth++;

        for (Element child : this.children) {
            if (child instanceof Container c) {
                tree.append(c.getTree(depth));
            } else {
                tree.append(new String(new char[depth * 2]).replace("\u0000", " "));
                String temp2 = child.getId().orElse("Element");
                temp2 = temp2.equals("") ? "Element" : temp2;
                tree.append("-> ").append(temp2).append("\n");
            }
        }

        return tree.toString();
    }

    private void resetScrollbarHideTimeout() {
        this.transitionHelper.removeTimeout(this.scrollHideTimeout);
        this.scrollbarOpacity = () -> 0.8F;
        this.scrollHideTimeout = this.transitionHelper.setTimeout(() -> this.scrollbarOpacity = this.transitionHelper.linear(0.8F, 0.1F, 30), 40);
    }

    public Container setInteractable(boolean interactable) {
        this.interactable = interactable;
        return this;
    }

    public Container setOnClickHandler(Clickable.ClickHandler onClickHandler) {
        this.onClickHandler = onClickHandler;
        return this;
    }

    public Container setOnReleaseHandler(Clickable.ClickHandler onReleaseHandler) {
        this.onReleaseHandler = onReleaseHandler;
        return this;
    }

    public Container setOnHoverHandler(Clickable.HoverHandler onHoverHandler) {
        this.onHoverHandler = onHoverHandler;
        return this;
    }

    public Container setOnUnHoverHandler(Clickable.UnHoverHandler onUnHoverHandler) {
        this.onUnHoverHandler = onUnHoverHandler;
        return this;
    }

    private interface ElementDrawer {
        void draw(Element var1, int var2);
    }
}
