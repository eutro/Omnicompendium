package eutros.omnicompendium.gui;

public interface ICompendiumPage {

    void draw();

    void mouseClicked(int mouseX, int mouseY, int mouseButton);

    CompendiumEntry setCompendium(GuiCompendium compendium);

}
