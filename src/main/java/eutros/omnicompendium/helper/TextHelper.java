package eutros.omnicompendium.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import static net.minecraft.client.gui.FontRenderer.getFormatFromString;

public class TextHelper {

    private static FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    public static String wrapFormattedStringToWidth(String str, int wrapWidth) {
        int i = trimStringToWidth(str, wrapWidth);

        if(str.length() <= i) {
            return str;
        } else {
            String s = str.substring(0, i);
            char c0 = str.charAt(i);
            boolean flag = c0 == ' ' || c0 == '\n';
            String s1 = getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
            return s + "\n" + wrapFormattedStringToWidth(s1, wrapWidth);
        }
    }

    public static int trimStringToWidth(String str, int wrapWidth) {
        int i1 = str.length();
        int j = 0;
        int k = 0;
        int l = -1;

        for(boolean flag1 = false; k < i1; ++k) {
            char c01 = str.charAt(k);

            switch(c01) {
                case '\n':
                    --k;
                    break;
                case ' ':
                    l = k;
                default:
                    j += fr.getCharWidth(c01);

                    if(flag1) {
                        ++j;
                    }

                    break;
                case '\u00a7':

                    if(k < i1 - 1) {
                        ++k;
                        char c1 = str.charAt(k);

                        if(c1 != 'l' && c1 != 'L') {
                            if(c1 == 'r' || c1 == 'R' || c1 >= '0' && c1 <= '9' || c1 >= 'a' && c1 <= 'f' || c1 >= 'A' && c1 <= 'F') {
                                flag1 = false;
                            }
                        } else {
                            flag1 = true;
                        }
                    }
            }

            if(c01 == '\n') {
                ++k;
                l = k;
                break;
            }

            if(j > wrapWidth) {
                break;
            }
        }

        return k != i1 && l != -1 && l < k ? l : k;
    }

}
