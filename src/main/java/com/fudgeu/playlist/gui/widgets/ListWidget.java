package com.fudgeu.playlist.gui.widgets;

import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.gui.PlaylistScreen;
import com.fudgeu.playlist.utils.RGBA;
import com.fudgeu.playlist.utils.RenderUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import org.jetbrains.annotations.Nullable;

public abstract class ListWidget<E extends ListWidget.Entry<E>> extends AbstractContainerEventHandler implements Renderable, NarratableEntry {
    protected final Minecraft client;
    private final PlaylistScreen playlistScreen;
    protected final int itemHeight;
    private final List<E> children = new ArrayList<>();
    protected int width;
    protected int height;
    protected int top;
    protected int bottom;
    protected int right;
    protected int left;
    protected boolean centerListVertically = true;
    private double scrollAmount;
    private boolean renderSelection = true;
    private boolean renderHeader;
    private boolean scrolling;
    private boolean isScrollPermitted = true;
    private boolean renderBottomOffScreen = false;
    @Nullable
    private E selected;
    @Nullable
    private E hoveredEntry;
    private RGBA itemHoverColor = new RGBA(0.075F);
    private RGBA itemActiveColor = new RGBA(0.1F);

    public ListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
        this.client = client;
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.itemHeight = itemHeight;
        this.left = 0;
        this.right = this.left + width;
        this.playlistScreen = PlaylistClient.instance.playlistScreenInstance;
    }

    public void checkAndUpdateFocused() {
    }

    public int getRowWidth() {
        return this.width;
    }

    @Nullable
    public E getSelectedOrNull() {
        return this.selected;
    }

    public void setSelected(@Nullable E entry) {
        this.selected = entry;
    }

    @Nullable
    public E getFocused() {
        return (E)super.getFocused();
    }

    public final List<E> children() {
        return this.children;
    }

    protected final void clearEntries() {
        this.children.clear();
    }

    protected void replaceEntries(Collection<E> newEntries) {
        this.children.clear();
        this.children.addAll(newEntries);
    }

    protected E getEntry(int index) {
        return this.children().get(index);
    }

    protected int addEntry(E entry) {
        this.children.add(entry);
        return this.children.size() - 1;
    }

    protected int getEntryCount() {
        return this.children().size();
    }

    protected boolean isSelectedEntry(int index) {
        return Objects.equals(this.getSelectedOrNull(), this.children().get(index));
    }

    @Nullable
    protected final E getEntryAtPosition(double x, double y) {
        int rowLeft = this.left;
        int rowRight = this.left + this.width;
        int m = Mth.floor(y - this.top) + (int)this.getScrollAmount();
        int n = m / this.itemHeight;
        return x >= rowLeft && x <= rowRight && n >= 0 && m >= 0 && n < this.getEntryCount() ? this.children().get(n) : null;
    }

    public void setLeftPos(int left) {
        this.left = left;
        this.right = left + this.width;
    }

    protected int getMaxPosition() {
        return this.getEntryCount() * this.itemHeight;
    }

    public void renderBottomOffScreen(boolean bool) {
        this.renderBottomOffScreen = bool;
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.checkAndUpdateFocused();
        int rowLeft = this.getRowLeft();
        int listTop = this.top + 4 - (int)this.getScrollAmount();
        this.hoveredEntry = this.isMouseOver(mouseX, mouseY) && !PlaylistClient.instance.playlistScreenInstance.isContextMenuActive()
            ? this.getEntryAtPosition(mouseX, mouseY)
            : null;
        this.preRender(context, mouseX, mouseY, delta);
        this.renderList(context, rowLeft, listTop, mouseX, mouseY, delta);
        this.postRender(context, mouseX, mouseY, delta);
        int maxEntriesVisible = (int)Math.floor((float)(this.bottom - this.top) / this.itemHeight);
        if (this.getEntryCount() != 0 && maxEntriesVisible < this.getEntryCount()) {
            int scrollBarHeight = (int)((this.bottom - this.top) * ((float)maxEntriesVisible / this.getEntryCount()));
            int maxScroll = this.getEntryCount() * this.itemHeight - this.height;
            int scrollOffset = (int)((this.height - scrollBarHeight) * (this.scrollAmount / maxScroll));
            RenderUtils.drawRect(this.right - 1, this.top + scrollOffset, this.right, this.top + scrollBarHeight + scrollOffset, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    protected void renderList(GuiGraphics context, int x, int y, int mouseX, int mouseY, float delta) {
        int i = this.getEntryCount();

        for (int j = 0; j < i; j++) {
            int rowTop = this.getRowTop(j);
            if (rowTop >= this.top) {
                int rowBottom = rowTop + this.itemHeight;
                if (rowBottom >= this.top && (this.renderBottomOffScreen ? rowTop <= this.bottom : rowBottom <= this.bottom)) {
                    int rowHeight = this.itemHeight;
                    E entry = this.getEntry(j);
                    int rowLeft = this.left;
                    int rowWidth = this.width;
                    if (Objects.equals(this.hoveredEntry, entry) || entry.isSelfContextMenuOpen) {
                        this.renderHovered(entry, context, rowLeft, rowTop, rowWidth, rowHeight);
                    }

                    if (Objects.equals(this.getFocused(), entry)) {
                        this.renderSelected(context, rowLeft, rowTop, rowWidth, rowHeight);
                    }

                    entry.render(context, j, rowLeft, rowTop, rowWidth, rowHeight, mouseX, mouseY, false, delta);
                }
            }
        }
    }

    public void preRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
    }

    public void postRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
    }

    public void setHoveredColor(RGBA color) {
        this.itemHoverColor = color;
    }

    public void setSelectedColor(RGBA color) {
        this.itemActiveColor = color;
    }

    public void renderHovered(ListWidget.Entry<E> entry, GuiGraphics context, int x, int y, int width, int height) {
        if (!Objects.equals(this.getFocused(), entry)) {
            RenderUtils.drawRect(x, y, x + width, y + height, this.itemHoverColor.r, this.itemHoverColor.g, this.itemHoverColor.b, this.itemHoverColor.a);
        }
    }

    public void renderSelected(GuiGraphics context, int x, int y, int width, int height) {
        RenderUtils.drawRect(x, y, x + width, y + height, this.itemActiveColor.r, this.itemActiveColor.g, this.itemActiveColor.b, this.itemActiveColor.a);
    }

    protected void ensureVisible(E entry) {
        int i = this.getRowTop(this.children().indexOf(entry));
        int j = i - this.top - 4 - this.itemHeight;
        if (j < 0) {
            this.scroll(j);
        }

        int k;
        if ((k = this.bottom - i - this.itemHeight - this.itemHeight) < 0) {
            this.scroll(-k);
        }
    }

    private void scroll(int amount) {
        this.setScrollAmount(this.getScrollAmount() + amount);
    }

    public double getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double amount) {
        this.scrollAmount = Mth.clamp(amount, 0.0, this.getMaxScroll());
    }

    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.bottom - this.top));
    }

    protected boolean updateScrollingState(double mouseX, double mouseY, int button) {
        this.scrolling = button == 0 && mouseX >= this.right - 2.0 && mouseX < this.right + 2 && mouseY > this.top && mouseY < this.bottom;
        return this.scrolling;
    }

    protected int getScrollbarPositionX() {
        return this.right - 1;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else if (this.updateScrollingState(mouseX, mouseY, button)) {
            return true;
        } else {
            E entry = this.getEntryAtPosition(mouseX, mouseY);
            if (entry != null && entry.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(entry);
                this.setDragging(true);
                this.client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            } else {
                return this.scrolling;
            }
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(mouseX, mouseY, button);
        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        } else if (button == 0 && this.scrolling) {
            if (mouseY < this.top) {
                this.setScrollAmount(0.0);
            } else if (mouseY > this.bottom) {
                this.setScrollAmount(this.getMaxScroll());
            } else {
                double d = Math.max(1, this.getMaxScroll());
                int i = this.bottom - this.top;
                int j = Mth.clamp((int)((float)(i * i) / this.getMaxPosition()), 32, i - 8);
                double e = Math.max(1.0, d / (i - j));
                this.setScrollAmount(this.getScrollAmount() + deltaY * e);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double amount) {
        if (this.isScrollPermitted) {
            this.setScrollAmount(this.getScrollAmount() - amount * this.itemHeight / 3.0);
            return true;
        } else {
            return false;
        }
    }

    public void permitScroll(boolean permit) {
        this.isScrollPermitted = permit;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == 264) {
            this.moveSelection(ListWidget.MoveDirection.DOWN);
            return true;
        } else if (keyCode == 265) {
            this.moveSelection(ListWidget.MoveDirection.UP);
            return true;
        } else {
            return false;
        }
    }

    protected void moveSelection(ListWidget.MoveDirection direction) {
        this.moveSelectionIf(direction, entry -> true);
    }

    protected void moveSelectionIf(ListWidget.MoveDirection direction, Predicate<E> predicate) {
        int i = direction == ListWidget.MoveDirection.UP ? -1 : 1;
        if (!this.children().isEmpty()) {
            int j = this.children().indexOf(this.getSelectedOrNull());

            int k;
            while (j != (k = Mth.clamp(j + i, 0, this.getEntryCount() - 1))) {
                ListWidget.Entry entry = this.children().get(k);
                if (predicate.test((E)entry)) {
                    this.setSelected((E)entry);
                    this.ensureVisible((E)entry);
                    break;
                }

                j = k;
            }
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseY >= this.top && mouseY <= this.bottom && mouseX >= this.left && mouseX <= this.left + this.width;
    }

    public int getRowLeft() {
        return this.left;
    }

    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    protected int getRowTop(int index) {
        return this.top - (int)this.getScrollAmount() + index * this.itemHeight;
    }

    private int getRowBottom(int index) {
        return this.getRowTop(index) + this.itemHeight;
    }

    public NarrationPriority narrationPriority() {
        return this.hoveredEntry != null ? NarrationPriority.HOVERED : NarrationPriority.NONE;
    }

    protected E remove(int index) {
        ListWidget.Entry entry = this.children.get(index);
        return (E)(this.removeEntry(this.children.get(index)) ? entry : null);
    }

    protected boolean removeEntry(E entry) {
        boolean bl = this.children.remove(entry);
        if (bl && entry == this.getSelectedOrNull()) {
            this.setSelected(null);
        }

        return bl;
    }

    @Nullable
    protected E getHoveredEntry() {
        return this.hoveredEntry;
    }

    public abstract static class Entry<E extends ListWidget.Entry<E>> implements GuiEventListener {
        @Deprecated
        ListWidget parentList;
        public boolean isSelfContextMenuOpen = false;

        public abstract void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

        public boolean isMouseOver(double mouseX, double mouseY) {
            return Objects.equals(this.parentList.getEntryAtPosition(mouseX, mouseY), this);
        }
    }

    protected static enum MoveDirection {
        UP,
        DOWN;
    }
}
