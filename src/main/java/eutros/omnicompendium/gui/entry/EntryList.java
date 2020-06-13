package eutros.omnicompendium.gui.entry;

import eutros.omnicompendium.gui.GuiCompendium;
import eutros.omnicompendium.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntryList {

    private static final double SCROLL_SENSITIVITY = 0.2;

    private int scroll;
    public final List<CompendiumEntry> entries;
    private final GuiCompendium compendium;

    public static final int ICON_MIN_V = 128;
    public static final int ICON_HEIGHT = 16;

    public EntryList(List<CompendiumEntry> entries, GuiCompendium compendium) {
        this.entries = entries;
        this.compendium = compendium;
        scroll = 0;
    }

    public void draw(CompendiumEntry currentPage) {
        RenderHelper.setupCamera(
                compendium.getGuiX(),
                GuiCompendium.GUI_Y + GuiCompendium.ENTRY_LIST_Y,
                GuiCompendium.ENTRY_LIST_WIDTH,
                GuiCompendium.ENTRY_LIST_HEIGHT
        );
        GlStateManager.pushMatrix();
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.translate(0, -scroll, 0);
        synchronized(entries) {
            for(CompendiumEntry entry : entries) {
                GlStateManager.pushMatrix();

                int titleX = 5;

                String title = TextFormatting.BOLD.toString() + entry.getTitle();

                if(entry == currentPage) {
                    int stringWidth = mc.fontRenderer.getStringWidth(title);
                    if(stringWidth > GuiCompendium.ENTRY_LIST_WIDTH) {
                        titleX -= MathHelper.clamp(
                                (int) (mc.world.getWorldTime() % stringWidth) - GuiCompendium.ENTRY_LIST_WIDTH / 2,
                                0,
                                stringWidth - GuiCompendium.ENTRY_LIST_WIDTH + titleX
                        );
                    }
                }

                mc.fontRenderer.drawString(title, titleX, (ICON_HEIGHT - mc.fontRenderer.FONT_HEIGHT) / 2F + 1, 0xFF000000, false);

                mc.getTextureManager().bindTexture(GuiCompendium.BOOK_GUI_TEXTURES);
                GlStateManager.color(0, 0, 0, 1);

                if(entry == currentPage) {
                    compendium.drawTexturedModalRect(0, 0, 0, ICON_MIN_V + ICON_HEIGHT, GuiCompendium.ENTRY_LIST_WIDTH, ICON_HEIGHT);
                }

                GlStateManager.popMatrix();
                compendium.drawTexturedModalRect(0, 0, 0, ICON_MIN_V, GuiCompendium.ENTRY_LIST_WIDTH, ICON_HEIGHT);

                GlStateManager.translate(0, ICON_HEIGHT, 0);
            }
        }
        RenderHelper.resetCamera();
        GlStateManager.popMatrix();
    }

    public boolean handleMouseInput(int mouseY) {
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
            entry.ifPresent(compendium::setEntry);

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
    public List<String> getTooltip(int mouseY) {
        return getEntryUnderMouse(mouseY)
                .map(entry -> {
                    ArrayList<String> tooltip = new ArrayList<>();
                    tooltip.add(entry.getTitle());
                    if(entry.source != null) {
                        tooltip.add(TextFormatting.BLUE + "" + TextFormatting.ITALIC + entry.source.toPath().getFileName().toString());
                    }
                    return tooltip;
                })
                .orElse(null);

    }

}
