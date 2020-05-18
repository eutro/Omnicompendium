package eutros.omnicompendium.gui.entry;

import eutros.omnicompendium.gui.GuiCompendium;
import eutros.omnicompendium.gui.component.*;
import eutros.omnicompendium.helper.ClickHelper.ClickableComponent;
import eutros.omnicompendium.helper.TextComponentParser;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class CompendiumEntry {

    private String markdown;
    private List<CompendiumComponent> components;
    private int constructionY;
    protected GuiCompendium compendium;

    @Nullable
    public File source;
    private int scroll = 0;

    private List<ClickableComponent> clickableComponents = new ArrayList<>();

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
                addComponent(BlankComponent.getInstance());
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
                addComponent(BlankComponent.getInstance());
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

    public void draw(int mouseX, int mouseY) {
        GlStateManager.translate(0, -scroll, 0);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        for(CompendiumComponent component : components) {
            component.draw();
        }
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        mouseY += scroll;

        for(ClickableComponent component : clickableComponents) {
            if(component.onClick(mouseX, mouseY, mouseButton))
                return;
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

    public void handleMouseInput() {
        scroll = (int) Math.max(0, scroll - Mouse.getDWheel() * SCROLL_SENSITIVITY);
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
