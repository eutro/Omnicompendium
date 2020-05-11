package eutros.omnicompendium.gui.component;

import eutros.omnicompendium.gui.GuiCompendium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.List;

public class TextComponentComponent extends CompendiumComponent {

    private ITextComponent text;

    public TextComponentComponent(ITextComponent text, int x, int y) {
        super(x, y);
        this.text = text;
    }

    public static List<ComponentFactory<TextComponentComponent>> fromString(String s) {
        StringBuilder builder = new StringBuilder();
        List<ComponentFactory<TextComponentComponent>> ret = new ArrayList<>();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        for(String word : s.split(" ")) {
            if((fr.getStringWidth(builder.toString() + word)) >= GuiCompendium.INNER_WIDTH) {
                if(fr.getStringWidth(builder.toString()) != 0)
                    addFactory(builder.toString(), ret);
                builder = resolveWordWrap(word, ret);
            } else {
                builder.append(word).append(" ");
            }
        }
        addFactory(builder.toString(), ret);

        return ret;
    }

    private static StringBuilder resolveWordWrap(String word, List<ComponentFactory<TextComponentComponent>> ret) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        while(fr.getStringWidth(word) >= GuiCompendium.INNER_WIDTH) {
            for(int i = word.length() - 1; i >= 0; i--) {
                String s = word.substring(0, i);
                if(fr.getStringWidth(s) <= GuiCompendium.INNER_WIDTH) {
                    addFactory(s, ret);
                    word = word.substring(i);
                    break;
                }
            }
        }

        return new StringBuilder(word + " ");
    }

    private static void addFactory(String s, List<ComponentFactory<TextComponentComponent>> ret) {
        ret.add((x, y) -> new TextComponentComponent(new TextComponentString(s.trim()), x, y));
    }

    @Override
    public int getHeight() {
        return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

    @Override
    protected void drawComponent() {
        Minecraft.getMinecraft().fontRenderer.drawString(text.getFormattedText(), 0, 0, COLOR);
    }

}
