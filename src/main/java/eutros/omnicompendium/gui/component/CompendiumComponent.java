package eutros.omnicompendium.gui.component;

import net.minecraft.client.renderer.GlStateManager;

public abstract class CompendiumComponent {

    public static final int COLOR = 0xFF888888;
    public final int x;
    public final int y;

    public CompendiumComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw() {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        drawComponent();
        GlStateManager.popMatrix();
    }

    public abstract int getHeight();

    protected abstract void drawComponent();

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    @FunctionalInterface
    public interface ComponentFactory<T extends CompendiumComponent> {

        T create(int x, int y);

    }

}
