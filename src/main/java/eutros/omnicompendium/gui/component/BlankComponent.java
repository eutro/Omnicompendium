package eutros.omnicompendium.gui.component;

import net.minecraft.client.Minecraft;

public class BlankComponent extends CompendiumComponent {

    private static BlankComponent INSTANCE = new BlankComponent();

    private BlankComponent() {
        super(0, 0);
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

    public static BlankComponent getInstance() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return "";
    }

}
