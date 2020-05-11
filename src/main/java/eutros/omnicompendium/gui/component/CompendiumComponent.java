package eutros.omnicompendium.gui.component;

import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public abstract class CompendiumComponent {

    static final int COLOR = new Color(0, 191, 191).getRGB();
    private final int x;
    private final int y;

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

    @FunctionalInterface
    public interface ComponentFactory<T extends CompendiumComponent> {

        T create(int x, int y);

    }

}