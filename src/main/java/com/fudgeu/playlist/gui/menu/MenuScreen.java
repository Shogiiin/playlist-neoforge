package com.fudgeu.playlist.gui.menu;

import com.fudgeu.playlist.StateManager;
import com.fudgeu.playlist.ToastManager;
import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.screens.songinfo.ConditionList;
import com.fudgeu.playlist.gui.widgets.Box;
import com.fudgeu.playlist.gui.widgets.RadioGroup;
import com.fudgeu.playlist.gui.widgets.SelectionList;
import com.fudgeu.playlist.gui.widgets.TextLabel;
import com.fudgeu.playlist.gui.widgets.Texture;
import com.fudgeu.playlist.gui.widgets.TransparentButton;
import com.fudgeu.playlist.gui.widgets.TransparentTextField;
import com.fudgeu.playlist.objects.CategorySet;
import com.fudgeu.playlist.utils.GeneralUtils;
import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.RenderUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button.OnPress;

public class MenuScreen extends Screen {
    private final Minecraft mc;
    private final StateManager stateManager;
    private final ToastManager toastManager;
    private final Component title;
    private final Screen previousScreen;
    private List<Renderable> drawables = new ArrayList<>();
    private TransparentButton backButton;
    private int totalHeight;
    private int top;
    private int paddedTop;
    private int paddedTopMainContent;
    private int bottom;
    private int paddedBottom;
    private int left;
    private int paddedLeft;
    private int right;
    private int paddedRight;
    public int menuWidth;
    public int menuHeight = 150;

    public MenuScreen(Component title, Screen previousScreen) {
        super(title);
        this.title = title;
        this.previousScreen = previousScreen;
        this.mc = Minecraft.getInstance();
        this.stateManager = PlaylistClient.instance.stateManager;
        this.toastManager = PlaylistClient.instance.toastManager;
    }

    public void init() {
        this.clearWidgets();
        this.totalHeight = 38 + this.menuHeight + 50;
        this.top = this.height / 2 - this.totalHeight / 2;
        this.paddedTop = this.top + 10;
        this.paddedTopMainContent = this.paddedTop + 28;
        this.bottom = this.height / 2 + this.totalHeight / 2;
        this.paddedBottom = this.bottom - 10;
        this.left = this.width / 2 - 125;
        this.paddedLeft = this.width / 2 - 100;
        this.right = this.width / 2 + 125;
        this.paddedRight = this.width / 2 + 100;
        this.menuWidth = 200;
        this.backButton = (TransparentButton)this.addRenderableWidget(
            new TransparentButton(this.left + 5, this.paddedTop - 3, 15, 15, Component.literal("<"), onPress -> this.closeScreen())
        );
        this.backButton.setStyle(TransparentButton.ButtonStyle.TRANSPARENT);
        super.init();
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        // Background is drawn explicitly at the start of render(); Screen.render() would otherwise draw it (and blur) twice.
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        if (this.stateManager.getStateBool("darkenBackground")) {
            RenderUtils.drawRect(0, 0, this.width, this.height, 0.0F, 0.0F, 0.0F, 0.65F);
        }

        RenderUtils.drawRect(this.left, this.top, this.right, this.paddedTop + 18, 0.1F, 0.1F, 0.1F, 1.0F);
        RenderUtils.drawRect(this.left, this.paddedTop + 18, this.right, this.bottom, 0.05F, 0.05F, 0.05F, 1.0F);
        context.drawString(this.mc.font, this.title, this.paddedLeft, this.paddedTop, 16777215, false);
        super.render(context, mouseX, mouseY, delta);
        this.toastManager.render(context, this.width, this.height);
        this.postRender(context, this.paddedLeft, this.paddedTopMainContent, mouseX, mouseY, delta);
    }

    public void postRender(GuiGraphics context, int left, int top, int mouseX, int mouseY, float delta) {
    }

    public void tick() {
        super.tick();
    }

    public void addTextLabel(int x, int y, Component label, int color) {
        int charactersPerLine = (int)Math.floor(this.menuWidth / 6.0F);
        String wrappedText = GeneralUtils.wrap(label.getString(), charactersPerLine, null, true, null, null);
        int i = 0;

        for (String cutString : Arrays.asList(wrappedText.split("\n"))) {
            this.addRenderableWidget(
                new TextLabel(
                    this.paddedLeft + x,
                    this.paddedTopMainContent + y + i * 12,
                    Component.literal(cutString.replaceAll("\r", "")).setStyle(label.getStyle()),
                    color
                )
            );
            i++;
        }
    }

    public TextLabel addSingleLineTextLabel(int x, int y, Component label, int color) {
        return (TextLabel)this.addRenderableWidget(new TextLabel(this.paddedLeft + x, this.paddedTopMainContent + y, label, color));
    }

    public Texture addTexture(int x, int y, int w, int h, ResourceLocation texture) {
        return (Texture)this.addRenderableWidget(new Texture(this.paddedLeft + x, this.paddedTopMainContent + y, w, h, texture));
    }

    public Texture addTexture(int x, int y, int w, int h, ResourceLocation texture, RGBA color) {
        return (Texture)this.addRenderableWidget(new Texture(this.paddedLeft + x, this.paddedTopMainContent + y, w, h, texture, color));
    }

    public TransparentTextField addTextBox(int x, int y, int fieldWidth, int fieldHeight, Component defaultText) {
        return (TransparentTextField)this.addRenderableWidget(
            new TransparentTextField(this.mc.font, this.paddedLeft + x, this.paddedTopMainContent + y, fieldWidth, fieldHeight, defaultText)
        );
    }

    public TransparentButton addButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        return (TransparentButton)this.addRenderableWidget(new TransparentButton(x + this.paddedLeft, y + this.paddedTopMainContent, width, height, message, onPress));
    }

    public TransparentButton setPrimaryMenuButton(Component message, OnPress onPress) {
        return (TransparentButton)this.addRenderableWidget(new TransparentButton(this.paddedRight - 60, this.paddedBottom - 20, 60, 20, message, onPress));
    }

    public TransparentButton setSecondaryMenuButton(Component message, OnPress onPress) {
        return (TransparentButton)this.addRenderableWidget(new TransparentButton(this.paddedRight - 125, this.paddedBottom - 20, 60, 20, message, onPress));
    }

    public RadioGroup addRadioGroup(int x, int y, int width, int height) {
        return (RadioGroup)this.addRenderableWidget(new RadioGroup(this.paddedLeft + x, this.paddedTopMainContent + y));
    }

    public RadioGroup addRadioGroup(int x, int y, int width, int height, RadioGroup.PressAction pressAction) {
        return (RadioGroup)this.addRenderableWidget(new RadioGroup(this.paddedLeft + x, this.paddedTopMainContent + y, pressAction));
    }

    public Box addDivider(int y) {
        return (Box)this.addRenderableWidget(
            new Box(this.paddedLeft, this.paddedTopMainContent + y, this.paddedRight, this.paddedTopMainContent + y + 1, new RGBA(0.1F))
        );
    }

    public ConditionList addConditionList(int x, int y, int width, int endY, int itemHeight, boolean editable, ArrayList<CategorySet> categorySets) {
        return (ConditionList)this.addRenderableWidget(
            new ConditionList(this.paddedLeft + x, this.paddedTopMainContent + y, width, this.paddedTopMainContent + endY, itemHeight, editable, categorySets)
        );
    }

    public SelectionList addSelectionList(int x, int y, int width, int endY, int itemHeight, HashMap<String, Component> selections) {
        return (SelectionList)this.addRenderableWidget(
            new SelectionList(this.paddedLeft + x, this.paddedTopMainContent + y, width, this.paddedTopMainContent + endY, itemHeight, selections)
        );
    }

    public int getWidth() {
        return this.width;
    }

    public void setMainContentHeight(int height) {
        this.menuHeight = height;
    }

    public Screen getPreviousScreen() {
        return this.previousScreen;
    }

    public void closeScreen() {
        this.mc.setScreen(this.previousScreen);
    }
}
