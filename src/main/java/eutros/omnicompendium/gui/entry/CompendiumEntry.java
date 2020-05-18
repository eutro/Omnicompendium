package eutros.omnicompendium.gui.entry;

import eutros.omnicompendium.gui.GuiCompendium;
import eutros.omnicompendium.gui.component.*;
import eutros.omnicompendium.helper.ClickHelper;
import eutros.omnicompendium.helper.ClickHelper.ClickableComponent;
import eutros.omnicompendium.helper.TextComponentParser;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class CompendiumEntry {

    public static final int SCROLL_BAR_WIDTH = 10;
    private String markdown;
    private List<CompendiumComponent> components;
    private int constructionY;
    protected GuiCompendium compendium;

    @Nullable
    public File source;
    private int scroll = 0;

    private List<ClickableComponent> clickableComponents = new ArrayList<>();
    private boolean scrollBarClicked = false;

    public CompendiumEntry(String markdown) {
        this.markdown = Constants.COMMENT_PATTERN.matcher(markdown).replaceAll("");
        generateComponents();
    }

    private void generateComponents() {
        components = new ArrayList<>();
        constructionY = 0;

        for(String s : markdown.split("\n")) {
            s = s.trim();

            if(s.isEmpty()) {
                addComponent(new BlankComponent(0, constructionY));
                continue;
            }

            if(Constants.HR_PATTERN.matcher(s).matches()) {
                addComponent(new HRComponent(0, constructionY));
                continue;
            }

            s = s.replaceAll("&", "\\\\&");

            boolean b;

            // TODO exclude links and stuff

            b = !s.equals(s = Constants.STRIKETHROUGH_PATTERN.matcher(s).replaceAll(Constants.STRIKETHROUGH_REPLACE));
            b = !s.equals(s = Constants.BOLD_PATTERN.matcher(s).replaceAll(Constants.BOLD_REPLACE)) | b;
            b = !s.equals(s = Constants.ITALIC_PATTERN.matcher(s).replaceAll(Constants.ITALIC_REPLACE)) | b;

            if(b) {
                s = TextComponentParser.parse(s, null).getFormattedText();
            }

            Matcher matcher = Constants.HEADING_PATTERN.matcher(s);

            if(matcher.matches()) {
                List<HeaderComponent.ComponentFactory<HeaderComponent>> factories = HeaderComponent.fromString(matcher.group(2), matcher.group(1).length(), this);
                for(HeaderComponent.ComponentFactory<HeaderComponent> factory : factories) {
                    addComponent(factory.create(0, constructionY));
                }
                continue;
            }

            s = Constants.POST_PROCESSING_PATTERN.matcher(s).replaceAll(Constants.POST_PROCESSING_REPLACE);

            matcher = Constants.IMG_PATTERN.matcher(s);

            if(matcher.find()) {
                // TODO image components
                addComponent(new BlankComponent(0, constructionY));
                continue;
            }

            // TODO code blocks

            List<CompendiumComponent.ComponentFactory<TextComponentComponent>> factories = TextComponentComponent.fromString(s, this, constructionY);
            for(CompendiumComponent.ComponentFactory<TextComponentComponent> factory : factories) {
                addComponent(factory.create(0, constructionY));
            }
        }
    }

    private void addComponent(CompendiumComponent component) {
        constructionY += component.getHeight() + Constants.LINE_GAP;
        components.add(component);
    }

    public void draw() {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -scroll, 0);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        components.stream()
                .filter(component -> component.y + component.getHeight() >= scroll)
                .filter(component -> component.y <= scroll + GuiCompendium.ENTRY_HEIGHT)
                .forEach(CompendiumComponent::draw);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        CompendiumComponent lastComponent = components.get(components.size() - 1);
        float maxScroll = Math.max((float) lastComponent.y + lastComponent.getHeight(), GuiCompendium.ENTRY_HEIGHT);
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
        for(ClickableComponent component : clickableComponents) {
            if(component.onClick(mouseX, mouseY + scroll, mouseButton))
                return;
        }

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
        CompendiumComponent lastComponent = components.get(components.size() - 1);
        return Math.max(0, lastComponent.y + lastComponent.getHeight() - GuiCompendium.ENTRY_HEIGHT);
    }

    @Nullable
    public List<String> getTooltip(int mouseX, int mouseY) {
        mouseY += scroll;
        for(ClickableComponent component : clickableComponents) {
            if(component.isHovered(mouseX, mouseY)) {
                List<String> tooltip = component.getTooltip();
                if(tooltip != null) {
                    return tooltip;
                }
            }
        }

        return null;
    }

    public GuiCompendium getCompendium() {
        return compendium;
    }

    @Nonnull
    public String getTitle() {

        String[] lines = markdown.split("\n");
        for(String s : lines) {
            Matcher matcher = Constants.HEADING_PATTERN.matcher(s);
            if(matcher.matches()) {
                return matcher.group(2);
            }
        }

        if(lines.length != 0) {
            String s = lines[0];

            s = Constants.STRIKETHROUGH_PATTERN.matcher(s).replaceAll("$2");
            s = Constants.BOLD_PATTERN.matcher(s).replaceAll("$2");
            s = Constants.ITALIC_PATTERN.matcher(s).replaceAll("$2");

            return s;
        }

        return Constants.UNTITLED;
    }

    public void addClickComponent(ClickableComponent component) {
        clickableComponents.add(component);
    }

}
