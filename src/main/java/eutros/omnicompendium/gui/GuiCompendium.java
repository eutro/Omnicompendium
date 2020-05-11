package eutros.omnicompendium.gui;

import eutros.omnicompendium.Omnicompendium;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class GuiCompendium extends GuiScreen {

    public static final ResourceLocation BOOK_GUI_TEXTURES = new ResourceLocation(Omnicompendium.MOD_ID, "textures/gui/compendium.png");
    public static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(Omnicompendium.MOD_ID, "pages/landing.md");

    public static final int TEX_SIZE = 256;
    public static final int INNER_WIDTH = 170;
    public static final int INNER_HEIGHT = 234;
    public static final double SCALE_FACTOR = 0.8;
    public static final int BORDER_X = 43;
    public static final int BORDER_Y = 11;
    public static final int TOP_OFFSET = 2;

    public int scroll = 0;
    private ICompendiumPage entry = CompendiumEntry.fromResourceLocation(DEFAULT_LOCATION).orElse(CompendiumEntry.BROKEN).setCompendium(this);

    public GuiCompendium() {
        super();
        this.setGuiSize(TEX_SIZE, TEX_SIZE);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURES);
        GlStateManager.pushMatrix();
        int i = (int) ((this.width - TEX_SIZE * SCALE_FACTOR) / 2);
        GlStateManager.translate(i, TOP_OFFSET, 0);
        GlStateManager.scale(SCALE_FACTOR, SCALE_FACTOR, 0);
        this.drawTexturedModalRect(0, 0, 0, 0, TEX_SIZE, TEX_SIZE);
        GlStateManager.translate(BORDER_X, BORDER_Y, 0);
        GlStateManager.translate(0, -scroll, 0);
        entry.draw();
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        int i = (int) ((this.width - TEX_SIZE * SCALE_FACTOR) / 2);
        mouseX -= i;
        mouseY -= TOP_OFFSET;
        mouseX /= SCALE_FACTOR;
        mouseY /= SCALE_FACTOR;
        entry.mouseClicked(mouseX - BORDER_X, mouseY - BORDER_Y + scroll, mouseButton);
    }

    public void setEntry(ICompendiumPage entry) {
        this.entry = entry;
        entry.setCompendium(this);
        scroll = 0;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        scroll = Math.max(0, scroll - Mouse.getDWheel() / 10);
    }

}
