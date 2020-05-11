package eutros.omnicompendium.helper;

import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * @link https://github.com/FTBTeam/FTB-Library/blob/1.12/src/main/java/com/feed_the_beast/ftblib/lib/util/text_components/TextComponentParser.java
 * @author LatvianModder
 */
public class TextComponentParser {

    public static final Char2ObjectOpenHashMap<TextFormatting> CODE_TO_FORMATTING = new Char2ObjectOpenHashMap<>();
    public static final char FORMATTING_CHAR = '\u00a7';

    static {
        for(TextFormatting formatting : TextFormatting.values()) {
            CODE_TO_FORMATTING.put(formatting.toString().charAt(1), formatting);
        }
    }

    public static ITextComponent parse(String text, @Nullable Function<String, ITextComponent> substitutes) {
        return new TextComponentParser(text, substitutes).parse();
    }

    private String text;
    private Function<String, ITextComponent> substitutes;

    private ITextComponent component;
    private StringBuilder builder;
    private Style style;

    private TextComponentParser(String txt, @Nullable Function<String, ITextComponent> sub) {
        text = txt;
        substitutes = sub;
    }

    private ITextComponent parse() {
        if(text.isEmpty()) {
            return new TextComponentString("");
        }

        char[] c = text.toCharArray();
        boolean hasSpecialCodes = false;

        for(char c1 : c) {
            if(c1 == '{' || c1 == '&' || c1 == FORMATTING_CHAR) {
                hasSpecialCodes = true;
                break;
            }
        }

        if(!hasSpecialCodes) {
            return new TextComponentString(text);
        }

        component = new TextComponentString("");
        style = new Style();
        builder = new StringBuilder();
        boolean sub = false;

        for(int i = 0; i < c.length; i++) {
            boolean escape = i > 0 && c[i - 1] == '\\';
            boolean end = i == c.length - 1;

            if(sub && (end || c[i] == '{' || c[i] == '}')) {
                if(c[i] == '{') {
                    throw new IllegalArgumentException("Invalid formatting! Can't nest multiple substitutes!");
                }

                finishPart();
                sub = false;
                continue;
            }

            if(!escape) {
                if(c[i] == '&') {
                    c[i] = FORMATTING_CHAR;
                }

                if(c[i] == FORMATTING_CHAR) {
                    finishPart();

                    if(end) {
                        throw new IllegalArgumentException("Invalid formatting! Can't end string with & or " + FORMATTING_CHAR + "!");
                    }

                    i++;

                    TextFormatting formatting = CODE_TO_FORMATTING.get(c[i]);

                    if(formatting == null) {
                        throw new IllegalArgumentException("Illegal formatting! Unknown color code character: " + c[i] + "!");
                    }

                    switch(formatting) {
                        case OBFUSCATED:
                            style.setObfuscated(!style.getObfuscated());
                            break;
                        case BOLD:
                            style.setBold(!style.getBold());
                            break;
                        case STRIKETHROUGH:
                            style.setStrikethrough(!style.getStrikethrough());
                            break;
                        case UNDERLINE:
                            style.setUnderlined(!style.getUnderlined());
                            break;
                        case ITALIC:
                            style.setItalic(!style.getItalic());
                            break;
                        case RESET:
                            style = new Style();
                            break;
                        default:
                            style.setColor(formatting);
                    }

                    continue;
                } else if(c[i] == '{') {
                    finishPart();

                    if(end) {
                        throw new IllegalArgumentException("Invalid formatting! Can't end string with {!");
                    }

                    sub = true;
                }
            }

            if(c[i] != '\\' || escape) {
                builder.append(c[i]);
            }
        }

        finishPart();
        return component;
    }

    private void finishPart() {
        String string = builder.toString();
        builder.setLength(0);

        if(string.isEmpty()) {
            return;
        } else if(string.length() < 2 || string.charAt(0) != '{') {
            ITextComponent component1 = new TextComponentString(string);
            component1.setStyle(style.createShallowCopy());
            component.appendSibling(component1);
            return;
        }

        ITextComponent component1 = substitutes.apply(string.substring(1));

        if(component1 != null) {
            Style style0 = component1.getStyle().createShallowCopy();
            Style style1 = style.createShallowCopy();
            style1.setHoverEvent(style0.getHoverEvent());
            style1.setClickEvent(style0.getClickEvent());
            style1.setInsertion(style0.getInsertion());
            component1.setStyle(style1);
        } else {
            throw new IllegalArgumentException("Invalid formatting! Unknown substitute " + string);
        }

        component.appendSibling(component1);
    }

}