package eutros.omnicompendium.gui.entry;

import eutros.omnicompendium.gui.GuiCompendium;
import eutros.omnicompendium.gui.markdown.RenderingVisitor;
import eutros.omnicompendium.helper.ClickHelper;
import eutros.omnicompendium.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CompendiumEntry {

    public static final int SCROLL_BAR_WIDTH = 10;
    private final Node node;
    protected GuiCompendium compendium;

    @Nullable
    public File source;
    private int scroll = 0;

    private boolean scrollBarClicked = false;

    public CompendiumEntry(String markdown) {
        List<Extension> extensions = Arrays.asList(
                TablesExtension.create(),
                StrikethroughExtension.create());
        Parser parser = Parser.builder()
                .extensions(extensions)
                .build();

        node = parser.parse(markdown);
    }

    public void draw() {
        RenderHelper.setupCamera(
                compendium.getGuiX() + GuiCompendium.ENTRY_X,
                GuiCompendium.GUI_Y + GuiCompendium.ENTRY_Y,
                GuiCompendium.ENTRY_WIDTH,
                GuiCompendium.ENTRY_HEIGHT
        );

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -scroll, 0);
        node.accept(RenderingVisitor.INSTANCE);
        GlStateManager.popMatrix();

        RenderHelper.resetCamera();

        float maxScroll = Math.max(RenderingVisitor.INSTANCE.y + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT, GuiCompendium.ENTRY_HEIGHT);
        float scrollPct = scroll / maxScroll;

        int barHeight = (int) (GuiCompendium.ENTRY_HEIGHT * GuiCompendium.ENTRY_HEIGHT / maxScroll);
        int scrollHeight = (int) ((GuiCompendium.ENTRY_HEIGHT - 2) * scrollPct);

        GuiUtils.drawGradientRect(0,
                GuiCompendium.ENTRY_WIDTH + 2,
                scrollHeight,
                GuiCompendium.ENTRY_WIDTH + SCROLL_BAR_WIDTH,
                scrollHeight + barHeight,
                0xAAAAAA0,
                0x888888);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // FIXME: component clicks

        if(mouseButton == 0) {
            scrollBarClicked = ClickHelper.isClicked(GuiCompendium.ENTRY_WIDTH,
                    0,
                    SCROLL_BAR_WIDTH,
                    GuiCompendium.ENTRY_HEIGHT,
                    mouseX,
                    mouseY);
        }
    }

    public CompendiumEntry setCompendium(GuiCompendium compendium) {
        this.compendium = compendium;
        return this;
    }

    public void reset() {
        scroll = 0;
    }

    public static final double SCROLL_SENSITIVITY = 0.1;

    public void handleMouseInput(int mouseY) {
        int maxScroll = getMaxScroll();
        int scroll = this.scroll - (int) (Mouse.getDWheel() * SCROLL_SENSITIVITY);

        if(scrollBarClicked) {
            if(Mouse.isButtonDown(0)) {
                float scrollPct = mouseY / (float) GuiCompendium.ENTRY_HEIGHT;
                scroll = (int) (maxScroll * scrollPct);
            } else {
                scrollBarClicked = false;
            }
        }

        this.scroll = MathHelper.clamp(
                scroll,
                0,
                maxScroll
        );
    }

    private int getMaxScroll() {
        return Math.max(0, RenderingVisitor.INSTANCE.y - GuiCompendium.ENTRY_HEIGHT + 10);
    }

    @Nullable
    public List<String> getTooltip(int mouseX, int mouseY) {
        // FIXME re-implement tooltips
        return null;
    }

    public GuiCompendium getCompendium() {
        return compendium;
    }

    @Nonnull
    public String getTitle() {
        // FIXME: re-implement titles
        return Constants.UNTITLED;
    }

}
