package eutros.omnicompendium.gui.component;

import net.minecraft.client.Minecraft;

public class BlankComponent extends CompendiumComponent {

    public BlankComponent(int x, int y) {
        super(x, y);
    }

    @Override
    public void draw() {
    }

    @Override
    protected void drawComponent() {
    }

    @Override
    public int getHeight() {
        return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

    @Override
    public String toString() {
        return "";
    }

}
