package eutros.omnicompendium.gui;

import eutros.omnicompendium.gui.entry.CompendiumEntry;

public interface ICompendiumPage {

    void draw(int mouseX, int mouseY);

    void mouseClicked(int mouseX, int mouseY, int mouseButton);

    CompendiumEntry setCompendium(GuiCompendium compendium);

    void reset();

    void handleMouseInput();

}
