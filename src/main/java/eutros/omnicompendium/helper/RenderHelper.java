package eutros.omnicompendium.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class RenderHelper {

    public static void setupCamera(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);

        GlStateManager.enableBlend();

        double scaledWidth = resolution.getScaledWidth();
        double scaledHeight = resolution.getScaledHeight();
        int windowWidth = (int) (width / scaledWidth * mc.displayWidth);
        int windowHeight = (int) (height / scaledHeight * mc.displayHeight);

        // These are the bottom left coordinates, so y is inverted.
        int windowX = (int) (x / scaledWidth * mc.displayWidth);
        int windowY = mc.displayHeight - (int) (y / scaledHeight * mc.displayHeight) - windowHeight;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(windowX, windowY, windowWidth, windowHeight);
    }

    public static void resetCamera() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.disableBlend();
    }

    public static void drawScrollBar(int[] bar, float scrollBarClicked) {
        int x = bar[0];
        int y = bar[1];
        int width = bar[2];
        int height = bar[3];

        int border = 2;
        Gui.drawRect( // corners
                x,
                y,
                x + width,
                y + height,
                0xFF7E7E7E
        );
        Gui.drawRect( // top left
                x,
                y,
                x + width - border,
                y + height - border,
                0xFFFFFFFF
        );
        Gui.drawRect( // bottom right
                x + border,
                y + border,
                x + width,
                y + height,
                0xFF373737
        );
        Gui.drawRect( // inner section
                x + border,
                y + border,
                x + width - border,
                y + height - border,
                0xFF8B8B8B
        );
        if(scrollBarClicked < 0) return;

        GlStateManager.enableBlend();
        Gui.drawRect( // overlay
                x,
                y,
                x + width,
                y + height,
                0x22FFFFFF
        );
        GlStateManager.disableBlend();
    }

}
