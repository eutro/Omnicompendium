package eutros.omnicompendium.gui.component;

import com.google.common.collect.ImmutableList;
import eutros.omnicompendium.gui.GuiCompendium;
import eutros.omnicompendium.gui.entry.CompendiumEntries;
import eutros.omnicompendium.gui.entry.CompendiumEntry;
import eutros.omnicompendium.gui.entry.Constants;
import eutros.omnicompendium.helper.ClickHelper;
import eutros.omnicompendium.helper.ClickHelper.ClickableComponent;
import eutros.omnicompendium.helper.FileHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

    public static List<ComponentFactory<TextComponentComponent>> fromString(String s, CompendiumEntry entry, int startHeight) {
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
            if((fr.getStringWidth(builder.toString() + word)) >= GuiCompendium.ENTRY_WIDTH) {
                if(fr.getStringWidth(builder.toString()) != 0)
                    addFactory(builder.toString(), ret, entry);
                builder = resolveWordWrap(word, st -> ret.add((x, y) -> {
                            TextComponentString component = new TextComponentString(s.trim());
                            if(link != null) {
                                entry.addClickComponent(ClickableComponent.bySize(
                                        x,
                                        y,
                                        GuiCompendium.ENTRY_WIDTH,
                                        fr.FONT_HEIGHT
                                )
                                        .withCallback(openLink(link, entry))
                                        .withTooltip(linkTooltip(link)));
                                component.getStyle().setColor(TextFormatting.BLUE);
                            }
                            return new TextComponentComponent(component, x, y, entry);
                        })
                );
                if(link != null) {
                    entry.addClickComponent(ClickableComponent.bySize(
                            0,
                            startHeight + ret.size() * (fr.FONT_HEIGHT + Constants.LINE_GAP),
                            fr.getStringWidth(builder.toString()),
                            fr.FONT_HEIGHT)
                            .withCallback(openLink(link, entry))
                            .withTooltip(linkTooltip(link)));
                    builder = new StringBuilder(TextFormatting.BLUE.toString())
                            .append(builder.toString())
                            .append(TextFormatting.RESET);
                }
            } else {
                if(link != null) {
                    entry.addClickComponent(ClickableComponent.bySize(
                            fr.getStringWidth(builder.toString()),
                            startHeight + ret.size() * (fr.FONT_HEIGHT + Constants.LINE_GAP),
                            fr.getStringWidth(word),
                            fr.FONT_HEIGHT)
                            .withCallback(openLink(link, entry))
                            .withTooltip(linkTooltip(link))
                    );
                    word = TextFormatting.BLUE + word + TextFormatting.RESET;
                }
                builder.append(word).append(" ");
            }
        }
        addFactory(builder.toString(), ret, entry);

        return ret;
    }

    private static ImmutableList<String> linkTooltip(String link) {
        return ImmutableList.of(
                TextFormatting.BLUE + link,
                TextFormatting.GRAY + "" + TextFormatting.ITALIC + I18n.format("omnicompendium.component.link_click"));
    }

    private static ClickHelper.ClickFunction openLink(String link, CompendiumEntry entry) {
        return (mouseX, mouseY, mouseButton) -> {
            Optional<CompendiumEntry> linkedEntry = CompendiumEntries.fromLink(link, entry.source);
            GuiCompendium gui = entry.getCompendium();
            if(linkedEntry.isPresent()) {
                gui.setEntry(linkedEntry.get());
                return true;
            } else {
                try {
                    if(Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        if(link.startsWith("http")) {
                            URI uri = new URI(link);
                            if(desktop.isSupported(Desktop.Action.BROWSE)) {
                                desktop.browse(uri);
                                return true;
                            }
                        } else {
                            File file = FileHelper.getRelative(entry.source, link);
                            File parent = file.getParentFile();
                            if(parent != null
                                    && desktop.isSupported(Desktop.Action.OPEN)) {
                                desktop.open(parent);
                            }
                        }
                    }
                } catch(NoClassDefFoundError | IOException | URISyntaxException ignored) {
                    // *shrug*
                }
            }
            return false;
        };
    }

    private static StringBuilder resolveWordWrap(String word, Consumer<String> factoryAdder) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        while(fr.getStringWidth(word) >= GuiCompendium.ENTRY_WIDTH) {
            for(int i = word.length() - 1; i >= 0; i--) {
                String s = word.substring(0, i);
                if(fr.getStringWidth(s) <= GuiCompendium.ENTRY_WIDTH) {
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

    @Override
    public String toString() {
        return text.getUnformattedText();
    }

}
