package eutros.omnicompendium.gui.markdown;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class MonoRenderer {

    public static final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    private static final int CHAR_SIZE = 7;

    public static int getStringWidth(String str) {
        return str.toCharArray().length * CHAR_SIZE;
    }

    @Nonnull
    public static List<String> listFormattedStringToWidth(String text, int wrapWidth) {
        return Arrays.asList(wrapFormattedStringToWidth(text, wrapWidth).split("\n"));
    }

    /**
     * Copied from {@link FontRenderer#wrapFormattedStringToWidth(String, int)}.
     */
    @SuppressWarnings("JavadocReference")
    private static String wrapFormattedStringToWidth(String str, int wrapWidth) {
        int i = sizeStringToWidth(str, wrapWidth);

        if(str.length() <= i) {
            return str;
        } else {
            String s = str.substring(0, i);
            char c0 = str.charAt(i);
            boolean flag = c0 == ' ' || c0 == '\n';
            String s1 = str.substring(i + (flag ? 1 : 0));
            return s + "\n" + wrapFormattedStringToWidth(s1, wrapWidth);
        }
    }

    /**
     * Copied from {@link FontRenderer#sizeStringToWidth(String, int)}.
     */
    @SuppressWarnings("JavadocReference")
    public static int sizeStringToWidth(String str, int wrapWidth) {
        int i = str.length();
        int j = 0;
        int k = 0;
        int l = -1;

        for(; k < i; ++k) {
            char c0 = str.charAt(k);

            switch(c0) {
                case '\n':
                    --k;
                    break;
                case ' ':
                    l = k;
                default:
                    j += CHAR_SIZE;
                    break;
            }

            if(c0 == '\n') {
                ++k;
                l = k;
                break;
            }

            if(j > wrapWidth) {
                break;
            }
        }

        return k != i && l != -1 && l < k ? l : k;
    }

    public static void drawString(String str, int x, int y, int color) {
        for(char c : str.toCharArray()) {
            fr.drawString(String.valueOf(c), x + (CHAR_SIZE - fr.getCharWidth(c)) / 2F, y, color, false);
            x += CHAR_SIZE;
        }
    }

}
