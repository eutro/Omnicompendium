package eutros.omnicompendium.gui.entry;

import eutros.omnicompendium.gui.GuiCompendium;
import eutros.omnicompendium.gui.ICompendiumPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class EntryList {

    private static final double SCROLL_SENSITIVITY = 0.2;

    private int scroll;
    private final List<CompendiumEntry> entries;

    public static final int ICON_MIN_V = 128;
    public static final int ICON_HEIGHT = 16;

    public EntryList(List<CompendiumEntry> entries) {
        this.entries = entries;
        scroll = 0;
    }

    public void draw(ICompendiumPage currentPage, GuiCompendium gui, int mouseX, int mouseY) {
        int pointer = this.scroll;
        Iterator<CompendiumEntry> it = entries.listIterator(pointer / ICON_HEIGHT);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(GuiCompendium.BOOK_GUI_TEXTURES);
        while(it.hasNext()) {
            CompendiumEntry entry = it.next();
            int topCrop = Math.floorMod(pointer, 16);
            int botCrop = Math.max(0, pointer - scroll + ICON_HEIGHT - GuiCompendium.ENTRY_LIST_HEIGHT);

            Random random = new Random(entry.hashCode());

            if(entry == currentPage) {
                gui.drawTexturedModalRect(0, 0, 0, ICON_MIN_V + ICON_HEIGHT + topCrop, GuiCompendium.ENTRY_LIST_WIDTH, ICON_HEIGHT - topCrop - botCrop);
            }

            gui.drawTexturedModalRect(0, 0, random.nextInt(GuiCompendium.TEX_SIZE), ICON_MIN_V + topCrop, GuiCompendium.ENTRY_LIST_WIDTH, ICON_HEIGHT - topCrop - botCrop);

            if(pointer - scroll + ICON_HEIGHT > GuiCompendium.ENTRY_LIST_HEIGHT) {
                break;
            }

            int shift = ICON_HEIGHT - Math.floorMod(pointer, ICON_HEIGHT);
            GlStateManager.translate(0, shift, 0);
            pointer += shift;
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        if(mouseX < GuiCompendium.ENTRY_LIST_WIDTH && mouseX > 0)
            getEntryUnderMouse(mouseY).ifPresent(entry -> gui.drawHoveringText(entry.getTitle(), mouseX, mouseY));
    }

    public boolean handleMouseInput(int mouseY, GuiCompendium gui) {
        int scroll = Mouse.getDWheel();
        boolean flag = false;

        if(scroll != 0) {
            int maxScroll = Math.max(0, 16 * entries.size() - GuiCompendium.ENTRY_LIST_HEIGHT);

            this.scroll = Math.min(Math.max(0, (int) (this.scroll - scroll * SCROLL_SENSITIVITY)), maxScroll);
            flag = true;
        }

        int button = Mouse.getEventButton();
        boolean buttonState = Mouse.getEventButtonState();

        if(button == 0 && buttonState) {
            Optional<CompendiumEntry> entry = getEntryUnderMouse(mouseY);
            entry.ifPresent(gui::setEntry);

            flag |= entry.isPresent();
        }

        return flag;
    }



    @Nonnull
    private Optional<CompendiumEntry> getEntryUnderMouse(int mouseY) {
        if(mouseY < 0)
            return Optional.empty();

        mouseY += scroll;
        int index = mouseY / ICON_HEIGHT;
        if(index >= entries.size())
            return Optional.empty();

        return Optional.of(entries.get(index));
    }

}
