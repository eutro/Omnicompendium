package eutros.omnicompendium.gui.component;

import eutros.omnicompendium.gui.CompendiumEntry;
import eutros.omnicompendium.gui.GuiCompendium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextComponentComponent extends CompendiumComponent {

    protected ITextComponent text;
    protected final CompendiumEntry entry;

    public TextComponentComponent(ITextComponent text, int x, int y, CompendiumEntry entry) {
        super(x, y);
        this.text = text;
        this.entry = entry;
    }

    private static Pattern TOKEN_MATCHER = Pattern.compile("((\\[(?<text>.+?)]\\((?<link>.+?)\\))|(?<word>[^ ]+))");

    public static List<ComponentFactory<TextComponentComponent>> fromString(String s, CompendiumEntry entry) {
        StringBuilder builder = new StringBuilder();
        List<ComponentFactory<TextComponentComponent>> ret = new ArrayList<>();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        Matcher matcher = TOKEN_MATCHER.matcher(s);

        while(matcher.find()) {
            String word = matcher.group("text");
            String link = matcher.group("link");
            if(word == null) {
                word = matcher.group();
            }
            if((fr.getStringWidth(builder.toString() + word)) >= GuiCompendium.INNER_WIDTH) {
                if(fr.getStringWidth(builder.toString()) != 0)
                    addFactory(builder.toString(), ret, entry);
                if(link == null) {
                    builder = resolveWordWrap(word, st -> addFactory(st, ret, entry));
                } else {
                    Consumer<String> linkFactoryFactory = st ->
                            ret.add((x, y) ->
                                    new LinkComponent(new TextComponentString(st),
                                            link,
                                            x,
                                            y,
                                            entry));
                    builder = resolveWordWrap(word, linkFactoryFactory);
                    linkFactoryFactory.accept(builder.toString());
                    int spaceWidth = fr.getStringWidth(TextFormatting.BOLD + " ");
                    int linkWidth = fr.getStringWidth(builder.toString());
                    int spaces = linkWidth / spaceWidth;
                    builder = new StringBuilder(new String(new char[spaces]).replace('\0', ' '));
                }
            } else {
                if(link != null) {
                    String finalWord = word;
                    int prevLength = fr.getStringWidth(builder.toString());
                    ret.add((x, y) ->
                            new LinkComponent(new TextComponentString(finalWord),
                                    link,
                                    x + prevLength,
                                    y,
                                    entry));
                    int spaceWidth = fr.getStringWidth(TextFormatting.BOLD + " ");
                    int linkWidth = fr.getStringWidth(word);
                    int spaces = linkWidth / spaceWidth;
                    word = new String(new char[spaces + 1]).replace('\0', ' ');
                }
                builder.append(word).append(" ");
            }
        }
        addFactory(builder.toString(), ret, entry);

        return ret;
    }

    private static StringBuilder resolveWordWrap(String word, Consumer<String> factoryAdder) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        while(fr.getStringWidth(word) >= GuiCompendium.INNER_WIDTH) {
            for(int i = word.length() - 1; i >= 0; i--) {
                String s = word.substring(0, i);
                if(fr.getStringWidth(s) <= GuiCompendium.INNER_WIDTH) {
                    factoryAdder.accept(s);
                    word = word.substring(i);
                    break;
                }
            }
        }

        return new StringBuilder(word + " ");
    }

    private static void addFactory(String s, List<ComponentFactory<TextComponentComponent>> ret, CompendiumEntry entry) {
        ret.add((x, y) -> new TextComponentComponent(new TextComponentString(s.trim()), x, y, entry));
    }

    @Override
    public int getHeight() {
        return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

    @Override
    protected void drawComponent() {
        Minecraft.getMinecraft().fontRenderer.drawString(text.getFormattedText(), 0, 0, getColor());
    }

    protected int getColor() {
        return COLOR;
    }

    private static class LinkComponent extends TextComponentComponent {

        private final String link;
        private static final int COLOR = new Color(16, 16, 255).getRGB();

        public LinkComponent(ITextComponent text, String link, int x, int y, CompendiumEntry entry) {
            super(text, x, y, entry);
            this.link = link;
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            if(mouseButton != 0) return;

            FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
            if(mouseX < x + fr.getStringWidth(text.getFormattedText())) {
                clickLink();
            }
        }

        private void clickLink() {
            Optional<CompendiumEntry> possibleEntry = CompendiumEntry.fromLink(link, entry.source);
            possibleEntry.ifPresent(compendiumEntry -> entry.getCompendium().setEntry(compendiumEntry));
        }

        @Override
        protected int getColor() {
            return COLOR;
        }

    }

}
