package eutros.omnicompendium.gui.component;

import eutros.omnicompendium.gui.GuiCompendium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class HRComponent extends CompendiumComponent {

    public HRComponent(int x, int y) {
        super(x, y);
    }

    @Override
    protected void drawComponent() {
        GuiScreen.drawRect(2, 4, GuiCompendium.INNER_WIDTH - 2, 5, COLOR);
    }

    @Override
    public int getHeight() {
        return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

}
