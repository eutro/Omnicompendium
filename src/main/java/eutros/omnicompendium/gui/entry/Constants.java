package eutros.omnicompendium.gui.entry;

import net.minecraft.client.resources.I18n;

import java.util.regex.Pattern;

public class Constants {

    /**
     * @link https://github.com/FTBTeam/FTB-Guides/blob/6e6683bfe5466d441bd4dbfcadd17f5e5e2454a1/src/main/java/com/feed_the_beast/mods/ftbguides/gui/components/ComponentPage.java#L23-L36
     */
    public static final Pattern COMMENT_PATTERN = Pattern.compile("<!--(?:.|\\s)*?-->\\s?", Pattern.MULTILINE);
    public static final Pattern STRIKETHROUGH_PATTERN = Pattern.compile("(?<!\\\\)(~~)(.+?)\\1");
    public static final String STRIKETHROUGH_REPLACE = "&m$2&m";
    public static final Pattern BOLD_PATTERN = Pattern.compile("(?<!\\\\)(\\*\\*|__)(.+?)\\1");
    public static final String BOLD_REPLACE = "&l$2&l";
    public static final Pattern ITALIC_PATTERN = Pattern.compile("(?<!\\\\)([*_])(.+?)\\1");
    public static final String ITALIC_REPLACE = "&o$2&o";
    public static final Pattern IMG_PATTERN = Pattern.compile("^!\\[(.*)]\\((.*)\\)$");
    public static final Pattern HR_PATTERN = Pattern.compile("^-{3,}|\\*{3,}|_{3,}$");
    public static final Pattern HEADING_PATTERN = Pattern.compile("^(#+)\\s*(.*)$");
    public static final Pattern POST_PROCESSING_PATTERN = Pattern.compile("\\\\([\\\\*_~])");
    public static final String POST_PROCESSING_REPLACE = "$1";

    public static final int LINE_GAP = 2;

    public static final String UNTITLED = I18n.format("omnicompendium.entry.untitled");

}
