package eutros.omnicompendium.gui;

import eutros.omnicompendium.Omnicompendium;
import eutros.omnicompendium.gui.entry.CompendiumEntries;
import eutros.omnicompendium.gui.entry.CompendiumEntry;
import eutros.omnicompendium.gui.entry.EntryList;
import eutros.omnicompendium.helper.MouseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class GuiCompendium extends GuiScreen {

    public static final ResourceLocation BOOK_GUI_TEXTURES = new ResourceLocation(Omnicompendium.MOD_ID, "textures/gui/compendium.png");
    public static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(Omnicompendium.MOD_ID, "pages/landing.md");

    public static final int TEX_SIZE = 256;

    public static double GUI_SCALE;
    public static final int GUI_Y = 20;
    public static int GUI_X;

    public static int ENTRY_WIDTH;
    public static int ENTRY_HEIGHT;
    public static int ENTRY_LIST_WIDTH;
    public static int ENTRY_LIST_HEIGHT;

    public static int ENTRY_LIST_Y;
    public static int ENTRY_LIST_X;
    public static int ENTRY_X;
    public static int ENTRY_Y;

    private final EntryList entryList;

    @Nonnull
    private CompendiumEntry entry = CompendiumEntries.fromResourceLocation(DEFAULT_LOCATION).orElse(CompendiumEntries.Entries.BROKEN).setCompendium(this);

    public GuiCompendium() {
        super();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        this.setGuiSize(sr.getScaledWidth(), sr.getScaledHeight());
        initGui();
        entryList = new EntryList(CompendiumEntries.listEntries, this);
    }

    @Override
    public void initGui() {
        GUI_SCALE = Math.min((double) width / TEX_SIZE, height / ((double) (TEX_SIZE + 2 * GUI_Y) / 2));

        GUI_X = (int) ((width - TEX_SIZE * GUI_SCALE) / 2);
        ENTRY_WIDTH = (int) (182 * GUI_SCALE);
        ENTRY_HEIGHT = (int) (104 * GUI_SCALE);
        ENTRY_LIST_WIDTH = (int) (37 * GUI_SCALE);
        ENTRY_LIST_HEIGHT = (int) (96 * GUI_SCALE);
        ENTRY_LIST_Y = (int) (17 * GUI_SCALE);
        ENTRY_LIST_X = (int) (8 * GUI_SCALE);
        ENTRY_X = (int) (60 * GUI_SCALE);
        ENTRY_Y = (int) (12 * GUI_SCALE);

        entry.setCompendium(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int i = GUI_X;

        {
            GlStateManager.pushMatrix();

            GlStateManager.translate(i, GUI_Y, 0);
            {
                GlStateManager.pushMatrix();

                GlStateManager.scale(GUI_SCALE, GUI_SCALE, 0);
                this.mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURES);
                this.drawTexturedModalRect(0, 0, 0, 0, TEX_SIZE, TEX_SIZE / 2);

                GlStateManager.popMatrix();
            }

            {
                GlStateManager.pushMatrix();
                drawEntryList();
                GlStateManager.popMatrix();
            }

            {
                GlStateManager.pushMatrix();
                drawEntry();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
        }

        Point mouse = new Point(mouseX - i, mouseY - GUI_Y);
        Point entryListMouse = transmuteEntryListMouse(mouse);
        Point entryMouse = transmuteEntryMouse(mouse);

        List<String> tooltip = null;
        if(MouseHelper.contains(0, 0, ENTRY_WIDTH, ENTRY_HEIGHT, entryMouse.x, entryMouse.y)) {
            tooltip = entry.getTooltip(entryMouse.x, entryMouse.y);
        }

        if(tooltip == null && MouseHelper.contains(0, 0, ENTRY_LIST_WIDTH, ENTRY_LIST_HEIGHT, entryListMouse.x, entryListMouse.y)) {
            tooltip = entryList.getTooltip(entryListMouse.y);
        }

        if(tooltip != null) {
            drawHoveringText(tooltip, mouseX, mouseY);
        }
    }

    @Nonnull
    private Point transmuteEntryMouse(Point mouse) {
        return new Point(mouse.x - ENTRY_X, mouse.y - ENTRY_Y);
    }

    @Nonnull
    private Point transmuteEntryListMouse(Point mouse) {
        return new Point(mouse.x - ENTRY_LIST_X, mouse.y - ENTRY_LIST_Y);
    }

    private void drawEntryList() {
        GlStateManager.translate(ENTRY_LIST_X, ENTRY_LIST_Y, 0);
        entryList.draw(entry);
    }

    private void drawEntry() {
        GlStateManager.translate(ENTRY_X, ENTRY_Y, 0);
        entry.draw();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        mouseX -= GUI_X;
        mouseX -= ENTRY_X;

        mouseY -= GUI_Y;
        mouseY -= ENTRY_Y;

        if(!MouseHelper.contains(0,
                0,
                ENTRY_WIDTH + CompendiumEntry.SCROLL_BAR_OFFSET + CompendiumEntry.SCROLL_BAR_WIDTH,
                ENTRY_HEIGHT,
                mouseX,
                mouseY))
            return;

        entry.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void setEntry(CompendiumEntry entry) {
        this.entry = entry;
        entry.setCompendium(this);
        entry.reset();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        mouseX -= GUI_X;

        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight;
        mouseY -= GUI_Y;

        if(MouseHelper.isClicked(ENTRY_LIST_X,
                ENTRY_LIST_Y,
                ENTRY_LIST_WIDTH,
                ENTRY_LIST_HEIGHT,
                mouseX,
                mouseY)) {
            if(entryList.handleMouseInput(mouseY - ENTRY_LIST_Y)) return;
        }

        Point mouse = transmuteEntryMouse(new Point(mouseX, mouseY));
        entry.handleMouseInput(mouse.y);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
