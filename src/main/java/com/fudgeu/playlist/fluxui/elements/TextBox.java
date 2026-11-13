package com.fudgeu.playlist.fluxui.elements;

import com.fudgeu.playlist.fluxui.FluxHelper;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.FluxUI;
import com.fudgeu.playlist.fluxui.clickables.Clickable;
import com.fudgeu.playlist.fluxui.clickables.Focusable;
import com.fudgeu.playlist.fluxui.clickables.KeyboardInputable;
import com.fudgeu.playlist.fluxui.clickables.MouseButton;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.style.InheritedStyles;
import com.fudgeu.playlist.fluxui.style.Style;
import com.fudgeu.playlist.fluxui.style.color.SolidColor;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class TextBox implements Element, Clickable, Focusable, KeyboardInputable {
    private final Minecraft client;
    private final String id;
    private final Style style;
    private Element parent;
    private Component defaultLabel;
    private Component text;
    private final Consumer<String> onTextUpdate;
    private final Runnable onFocus;
    private final Runnable onUnFocus;
    private boolean numbersOnly;
    private final int maxLength;
    private boolean cursorDrawDirty = true;
    private int cursorPosition = 0;
    private int cursorDrawX = 0;
    private boolean isFocused = false;
    private boolean isDirty = true;
    private int memoX = 0;
    private int memoY = 0;
    private int memoW = 0;
    private int memoH = 0;
    private long framesDrawn = 0L;

    public TextBox(String id, Component defaultLabel, Consumer<String> onTextUpdate, Runnable onFocus, Runnable onUnFocus, BoolProp numbersOnly, int maxLength) {
        this.id = id;
        this.defaultLabel = defaultLabel;
        this.text = defaultLabel;
        this.onTextUpdate = onTextUpdate;
        this.onFocus = onFocus;
        this.onUnFocus = onUnFocus;
        this.numbersOnly = numbersOnly.get();
        this.maxLength = maxLength < 0 ? Integer.MAX_VALUE : maxLength;
        this.style = FluxInstance.getInstance().getStyleOrDefault(id);
        this.client = FluxUI.getMinecraftClient().orElseThrow();
    }

    @Override
    public void draw(GuiGraphics context, int x, int y, InheritedStyles inheritedStyles) {
        this.memoX = x;
        this.memoY = y;
        if (this.isDirty) {
            this.getWidth();
            this.getHeight();
        }

        SolidColor foregroundColor = this.style.getForegroundColor().orElse(inheritedStyles.getForegroundColor());
        int hex = foregroundColor.getColor().hex();
        int mixedOpacity = Math.round(255.0F * inheritedStyles.getOpacity()) << 24;
        mixedOpacity = (int)Math.max(Integer.toUnsignedLong(mixedOpacity), 268435456L);
        context.drawString(this.client.font, this.text, this.memoX, this.memoY, hex + mixedOpacity, false);
        if (this.cursorDrawDirty) {
            this.cursorDrawDirty = false;
            String cutText = this.text.getString().substring(0, this.cursorPosition);
            this.cursorDrawX = this.client.font.width(cutText);
        }

        if (this.isFocused && this.framesDrawn / 60L % 2L == 0L) {
            FluxHelper.drawRectangle(context, this.memoX + this.cursorDrawX, y, 1, this.memoH, 0.8F, 0.8F, 0.8F, 0.8F);
        }

        this.framesDrawn++;
    }

    @Override
    public int getWidth() {
        if (!this.isDirty) {
            return this.memoW;
        } else {
            this.isDirty = false;
            return this.memoW = this.client.font.width(this.text) + 1;
        }
    }

    @Override
    public int getHeight() {
        this.memoH = 8;
        return this.memoH;
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
        ArrayList<Element> list = new ArrayList<>();
        if (FluxHelper.isInside(x, y, this.memoX, this.memoY, this.memoW, this.memoH)) {
            list.add(this);
        }

        return list;
    }

    @Override
    public Optional<String> getId() {
        return Optional.of(this.id);
    }

    @Override
    public boolean interactable() {
        return true;
    }

    @Override
    public void onClick(MouseButton mouseButton, float x, float y) {
        if (x > this.memoX + this.memoW) {
            this.cursorPosition = this.text.getString().length();
            this.cursorDrawDirty = true;
        } else if (x < this.memoX) {
            this.cursorPosition = 0;
            this.cursorDrawDirty = true;
        } else {
            for (int i = 0; i <= this.text.getString().length(); i++) {
                String truncatedString = this.text.getString(i);
                Component truncatedText = Component.literal(truncatedString);
                int truncatedTextWidth = this.client.font.width(truncatedText);
                if (x <= this.memoX + truncatedTextWidth - 2) {
                    this.cursorPosition = i - 1;
                    this.cursorDrawDirty = true;
                    break;
                }

                if (x <= this.memoX + truncatedTextWidth) {
                    this.cursorPosition = i;
                    this.cursorDrawDirty = true;
                    break;
                }
            }
        }
    }

    @Override
    public void onRelease(MouseButton mouseButton, float x, float y) {
    }

    @Override
    public void onHover(Supplier<Float> x, Supplier<Float> y) {
    }

    @Override
    public void onUnHover() {
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public boolean isFocused() {
        return this.isFocused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.isFocused = focused;
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
        if (this.isFocused) {
            switch (keyCode) {
                case 259:
                    String textAsString = this.text.getString();
                    if (textAsString.length() < 1 || this.cursorPosition < 1) {
                        return;
                    }

                    String firstSubSection = textAsString.substring(0, this.cursorPosition - 1);
                    String secondSubSection = textAsString.substring(this.cursorPosition);
                    String newString = firstSubSection + secondSubSection;
                    this.cursorPosition--;
                    this.cursorDrawDirty = true;
                    this.setText(newString);
                case 260:
                default:
                    break;
                case 261: {
                        String textAsStringDel = this.text.getString();
                        if (textAsStringDel.length() < 1 || this.cursorPosition >= textAsStringDel.length()) {
                            return;
                        }

                        String firstSubSectionDel = textAsStringDel.substring(0, this.cursorPosition);
                        String secondSubSectionDel = textAsStringDel.substring(this.cursorPosition + 1);
                        String newStringDel = firstSubSectionDel + secondSubSectionDel;
                        this.setText(newStringDel);
                        break;
                }
                case 262:
                    this.cursorPosition = Math.min(this.text.getString().length(), this.cursorPosition + 1);
                    this.cursorDrawDirty = true;
                    break;
                case 263:
                    this.cursorPosition = Math.max(0, this.cursorPosition - 1);
                    this.cursorDrawDirty = true;
            }

            this.isDirty = true;
        }
    }

    @Override
    public void onCharPressed(char c) {
        if (this.isFocused) {
            if (this.text.getString().length() < this.maxLength) {
                if (!this.numbersOnly || c >= '0' && c <= '9') {
                    String textAsString = this.text.getString();
                    String firstSubSection = textAsString.substring(0, this.cursorPosition);
                    String secondSubSection = textAsString.substring(this.cursorPosition);
                    String newString = firstSubSection + c + secondSubSection;
                    this.setText(newString);
                    this.cursorPosition++;
                    this.cursorDrawDirty = true;
                    this.isDirty = true;
                }
            }
        }
    }

    public void setText(String newText) {
        this.text = Component.literal(newText);
        this.onTextUpdate.accept(newText);
        this.isDirty = true;
    }

    public String getText() {
        return this.text.getString();
    }

    public Component getDefaultLabel() {
        return this.defaultLabel;
    }
}
