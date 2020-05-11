package eutros.omnicompendium.gui.component;

import eutros.omnicompendium.gui.GuiCompendium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class HeaderComponent extends TextComponentComponent {

    private final int size;

    public HeaderComponent(ITextComponent text, int x, int y, int size) {
        super(text, x, y);
        this.size = size;
    }

    private static double scaleFactorOfSize(int size) {
        return 1 + 0.5 / size;
    }

    public static List<ComponentFactory<HeaderComponent>> fromString(String s, int size) {
        StringBuilder builder = new StringBuilder(TextFormatting.BOLD.toString());
        List<ComponentFactory<HeaderComponent>> ret = new ArrayList<>();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        double sf = scaleFactorOfSize(size);

        for(String word : s.split(" ")) {
            if((fr.getStringWidth(builder.toString() + word)) * sf >= GuiCompendium.INNER_WIDTH) {
                if(fr.getStringWidth(builder.toString()) != 0)
                    addFactory(size, builder.toString(), ret);
                builder = resolveWordWrap(size, word, ret);
            } else {
                builder.append(word).append(" ");
            }
        }
        addFactory(size, builder.toString(), ret);

        return ret;
    }

    private static StringBuilder resolveWordWrap(int size, String word, List<ComponentFactory<HeaderComponent>> ret) {
        double sf = scaleFactorOfSize(size);
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        while(fr.getStringWidth(word) * sf >= GuiCompendium.INNER_WIDTH) {
            for(int i = word.length() - 1; i >= 0; i--) {
                String s = TextFormatting.BOLD + word.substring(0, i);
                if(fr.getStringWidth(s) * sf <= GuiCompendium.INNER_WIDTH) {
                    addFactory(size, s, ret);
                    word = word.substring(i);
                    break;
                }
            }
        }

        return new StringBuilder(TextFormatting.BOLD + word + " ");
    }

    private static void addFactory(int size, String string, List<ComponentFactory<HeaderComponent>> ret) {
        ret.add((x, y) -> new HeaderComponent(new TextComponentString(string.trim()), x, y, size));
    }

    @Override
    public int getHeight() {
        return (int) Math.ceil(super.getHeight() * scaleFactorOfSize(size));
    }

    @Override
    protected void drawComponent() {
        double sf = scaleFactorOfSize(size);
        GlStateManager.scale(sf, sf, 1);
        super.drawComponent();
    }

}
