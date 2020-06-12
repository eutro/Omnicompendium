package eutros.omnicompendium.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class RenderHelper {

    public static void setupCamera(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();

        double scaledWidth = resolution.getScaledWidth();
        double scaledHeight = resolution.getScaledHeight();
        int windowWidth = (int) (width / scaledWidth * mc.displayWidth);
        int windowHeight = (int) (height / scaledHeight * mc.displayHeight);

        // These are the bottom left coordinates, so y is inverted.
        int windowX = (int) (x / scaledWidth * mc.displayWidth);
        int windowY = mc.displayHeight - (int) (y / scaledHeight * mc.displayHeight) - windowHeight;

        //GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(windowX, windowY, windowWidth, windowHeight);
    }

    public static void resetCamera() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
    }

}
