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
                addFactory(builder, ret);
                builder = new StringBuilder(word + " ");
            } else {
                builder.append(word).append(" ");
            }
        }
        addFactory(builder, ret);

        return ret;
    }

    private static void addFactory(StringBuilder builder, List<ComponentFactory<TextComponentComponent>> ret) {
        ret.add((x, y) -> new TextComponentComponent(new TextComponentString(builder.toString().trim()), x, y));
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
