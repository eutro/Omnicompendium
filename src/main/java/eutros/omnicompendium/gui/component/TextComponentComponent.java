package eutros.omnicompendium.gui.component;

import eutros.omnicompendium.gui.GuiCompendium;
import eutros.omnicompendium.gui.entry.CompendiumEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TextComponentComponent extends CompendiumComponent {

    protected ITextComponent text;
    protected final CompendiumEntry entry;

    public TextComponentComponent(ITextComponent text, int x, int y, CompendiumEntry entry) {
        super(x, y);
        this.text = text;
        this.entry = entry;
    }

    public static List<ComponentFactory<TextComponentComponent>> fromString(String s, CompendiumEntry entry) {
        StringBuilder builder = new StringBuilder();
        List<ComponentFactory<TextComponentComponent>> ret = new ArrayList<>();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        for(String word : s.split(" ")) {
            if((fr.getStringWidth(builder.toString() + word)) >= GuiCompendium.ENTRY_WIDTH) {
                if(fr.getStringWidth(builder.toString()) != 0)
                    addFactory(builder.toString(), ret, entry);
                builder = resolveWordWrap(word, st -> addFactory(st, ret, entry));
            } else {
                builder.append(word).append(" ");
            }
        }
        addFactory(builder.toString(), ret, entry);

        return ret;
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
