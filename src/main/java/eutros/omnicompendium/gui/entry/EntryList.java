package eutros.omnicompendium.gui.entry;

import eutros.omnicompendium.gui.GuiCompendium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class EntryList {

    private static final double SCROLL_SENSITIVITY = 0.2;

    private int scroll;
    private final List<eutros.omnicompendium.gui.entry.CompendiumEntry> entries;

    public static final int ICON_MIN_V = 128;
    public static final int ICON_HEIGHT = 16;

    public EntryList(List<eutros.omnicompendium.gui.entry.CompendiumEntry> entries) {
        this.entries = entries;
        scroll = 0;
    }

    public void draw(CompendiumEntry currentPage, GuiCompendium gui) {
        int pointer = this.scroll;
        Iterator<eutros.omnicompendium.gui.entry.CompendiumEntry> it = entries.listIterator(pointer / ICON_HEIGHT);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getTextureManager().bindTexture(GuiCompendium.BOOK_GUI_TEXTURES);
        while(it.hasNext()) {
            eutros.omnicompendium.gui.entry.CompendiumEntry entry = it.next();
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
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
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
            Optional<eutros.omnicompendium.gui.entry.CompendiumEntry> entry = getEntryUnderMouse(mouseY);
            entry.ifPresent(gui::setEntry);

            flag |= entry.isPresent();
        }

        return flag;
    }



    @Nonnull
    private Optional<eutros.omnicompendium.gui.entry.CompendiumEntry> getEntryUnderMouse(int mouseY) {
        if(mouseY < 0)
            return Optional.empty();

        mouseY += scroll;
        int index = mouseY / ICON_HEIGHT;
        if(index >= entries.size())
            return Optional.empty();

        return Optional.of(entries.get(index));
    }

    @Nullable
    public List<String> getTooltip(int mouseX, int mouseY) {
        if(mouseX > 0 && mouseX < GuiCompendium.ENTRY_LIST_WIDTH)
            return getEntryUnderMouse(mouseY)
                    .map(CompendiumEntry::getTitle)
                    .map(Collections::singletonList)
                    .orElse(null);

        return null;
    }

}
