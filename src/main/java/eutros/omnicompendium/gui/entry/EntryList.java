package eutros.omnicompendium.gui.entry;

import eutros.omnicompendium.gui.GuiCompendium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class EntryList {

    private static final double SCROLL_SENSITIVITY = 0.2;

    private int scroll;
    public final List<CompendiumEntry> entries = new ArrayList<>();

    public static final int ICON_MIN_V = 128;
    public static final int ICON_HEIGHT = 16;

    public EntryList() {
        scroll = 0;
    }

    public void draw(CompendiumEntry currentPage, GuiCompendium gui) {
        int pointer = this.scroll;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getTextureManager().bindTexture(GuiCompendium.BOOK_GUI_TEXTURES);
        synchronized(entries) {
            Iterator<CompendiumEntry> it = entries.listIterator(pointer / ICON_HEIGHT);
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
            Optional<CompendiumEntry> entry = getEntryUnderMouse(mouseY);
            entry.ifPresent(gui::setEntry);

            flag |= entry.isPresent();
        }

        return flag;
    }



    @Nonnull
    private Optional<CompendiumEntry> getEntryUnderMouse(int mouseY) {
        if(mouseY < 0 || mouseY > GuiCompendium.ENTRY_LIST_HEIGHT)
            return Optional.empty();

        mouseY += scroll;
        int index = mouseY / ICON_HEIGHT;
        if(index >= entries.size())
            return Optional.empty();

        return Optional.of(entries.get(index));
    }

    @Nullable
    public List<String> getTooltip(int mouseX, int mouseY) {
        return getEntryUnderMouse(mouseY)
                .map(entry -> {
                    ArrayList<String> tooltip = new ArrayList<>();
                    tooltip.add(entry.getTitle());
                    if(entry.source != null) {
                        tooltip.add(TextFormatting.BLUE + "" +  TextFormatting.ITALIC + entry.source.toPath().getFileName().toString());
                    }
                    return tooltip;
                })
                .orElse(null);

    }

}
