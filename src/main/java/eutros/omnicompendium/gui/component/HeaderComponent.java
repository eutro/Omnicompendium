package eutros.omnicompendium.gui.component;

import eutros.omnicompendium.gui.GuiCompendium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

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
        StringBuilder builder = new StringBuilder();
        List<ComponentFactory<HeaderComponent>> ret = new ArrayList<>();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        double sf = scaleFactorOfSize(size);

        for(String word : s.split(" ")) {
            if((fr.getStringWidth(builder.toString() + word)) * sf >= GuiCompendium.INNER_WIDTH) {
                addFactory(size, builder, ret);
                builder = new StringBuilder(word + " ");
            } else {
                builder.append(word).append(" ");
            }
        }
        addFactory(size, builder, ret);

        return ret;
    }

    private static void addFactory(int size, StringBuilder builder, List<ComponentFactory<HeaderComponent>> ret) {
        ret.add((x, y) -> {
            TextComponentString c = new TextComponentString(builder.toString().trim());
            c.getStyle().setBold(true);
            return new HeaderComponent(c, x, y, size);
        });
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
