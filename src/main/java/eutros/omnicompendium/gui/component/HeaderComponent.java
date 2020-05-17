package eutros.omnicompendium.gui.component;

import eutros.omnicompendium.gui.entry.CompendiumEntry;
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

    public HeaderComponent(ITextComponent text, int x, int y, int size, CompendiumEntry entry) {
        super(text, x, y, entry);
        this.size = size;
    }

    private static double scaleFactorOfSize(int size) {
        return 1 + 0.5 / size;
    }

    public static List<ComponentFactory<HeaderComponent>> fromString(String s, int size, CompendiumEntry entry) {
        StringBuilder builder = new StringBuilder(TextFormatting.BOLD.toString());
        List<ComponentFactory<HeaderComponent>> ret = new ArrayList<>();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        double sf = scaleFactorOfSize(size);

        for(String word : s.split(" ")) {
            if((fr.getStringWidth(builder.toString() + word)) * sf >= GuiCompendium.ENTRY_WIDTH) {
                if(fr.getStringWidth(builder.toString()) != 0)
                    addFactory(size, builder.toString(), ret, entry);
                builder = resolveWordWrap(size, word, ret, entry);
            } else {
                builder.append(word).append(" ");
            }
        }
        addFactory(size, builder.toString(), ret, entry);

        return ret;
    }

    private static StringBuilder resolveWordWrap(int size, String word, List<ComponentFactory<HeaderComponent>> ret, CompendiumEntry entry) {
        double sf = scaleFactorOfSize(size);
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        while(fr.getStringWidth(word) * sf >= GuiCompendium.ENTRY_WIDTH) {
            for(int i = word.length() - 1; i >= 0; i--) {
                String s = TextFormatting.BOLD + word.substring(0, i);
                if(fr.getStringWidth(s) * sf <= GuiCompendium.ENTRY_WIDTH) {
                    addFactory(size, s, ret, entry);
                    word = word.substring(i);
                    break;
                }
            }
        }

        return new StringBuilder(TextFormatting.BOLD + word + " ");
    }

    private static void addFactory(int size, String string, List<ComponentFactory<HeaderComponent>> ret, CompendiumEntry entry) {
        ret.add((x, y) -> new HeaderComponent(new TextComponentString(string.trim()), x, y, size, entry));
    }

    @Override
    public int getHeight() {
        return (int) Math.ceil(super.getHeight() * scaleFactorOfSize(size));
    }

    @Override
    protected void drawComponent() {
        double sf = scaleFactorOfSize(size);
        GlStateManager.scale(sf, sf, 1);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text.getFormattedText(), 0, 0, getColor());
    }

}
