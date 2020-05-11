package eutros.omnicompendium.gui;

import eutros.omnicompendium.Config;
import eutros.omnicompendium.gui.component.*;
import eutros.omnicompendium.helper.TextComponentParser;
import eutros.omnicompendium.loader.GitLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CompendiumEntry {

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
    public static final Pattern LINK_PATTERN = Pattern.compile("^(!)?\\[(.*)]\\((.*)\\)$");
    public static final Pattern HR_PATTERN = Pattern.compile("^-{3,}|\\*{3,}|_{3,}$");
    public static final Pattern HEADING_PATTERN = Pattern.compile("^(#+)\\s*(.*)$");
    public static final Pattern POST_PROCESSING_PATTERN = Pattern.compile("\\\\([\\\\*_~])");
    public static final String POST_PROCESSING_REPLACE = "$1";

    public static final CompendiumEntry BROKEN = new CompendiumEntry(I18n.format("omnicompendium.page.broken"));
    public static final int LINE_GAP = 2;
    private static Map<String, CompendiumEntry> entryMap = new HashMap<>();

    private String markdown;
    private List<CompendiumComponent> components;
    private int constructionY;

    public CompendiumEntry(String markdown) {
        this.markdown = COMMENT_PATTERN.matcher(markdown).replaceAll("");
        generateComponents();
    }

    private void generateComponents() {
        components = new ArrayList<>();
        constructionY = 0;

        for(String s : markdown.split("\n")) {
            s = s.trim();

            if(s.isEmpty()) {
                addComponent(BlankComponent.getInstance());
                continue;
            }

            if(HR_PATTERN.matcher(s).matches()) {
                addComponent(new HRComponent(0, constructionY));
                continue;
            }

            boolean b;

            b = !s.equals(s = STRIKETHROUGH_PATTERN.matcher(s).replaceAll(STRIKETHROUGH_REPLACE));
            b = !s.equals(s = BOLD_PATTERN.matcher(s).replaceAll(BOLD_REPLACE)) | b;
            b = !s.equals(s = ITALIC_PATTERN.matcher(s).replaceAll(ITALIC_REPLACE)) | b;

            if(b) {
                s = TextComponentParser.parse(s, null).getFormattedText();
            }

            Matcher matcher = HEADING_PATTERN.matcher(s);

            if(matcher.matches()) {
                List<HeaderComponent.ComponentFactory<HeaderComponent>> factories = HeaderComponent.fromString(matcher.group(2), matcher.group(1).length());
                for(HeaderComponent.ComponentFactory<HeaderComponent> factory : factories) {
                    addComponent(factory.create(0, constructionY));
                }
                continue;
            }

            s = POST_PROCESSING_PATTERN.matcher(s).replaceAll(POST_PROCESSING_REPLACE);

            matcher = LINK_PATTERN.matcher(s);

            if(matcher.find()) {
                //noinspection IfStatementWithIdenticalBranches
                if(matcher.group(1) != null && matcher.group(1).equals("!")) {
                    // TODO image components
                    addComponent(BlankComponent.getInstance());
                    continue;
                } else {
                    // TODO links
                    addComponent(BlankComponent.getInstance());
                    continue;
                }
            }

            // TODO code blocks

            List<CompendiumComponent.ComponentFactory<TextComponentComponent>> factories = TextComponentComponent.fromString(s);
            for(CompendiumComponent.ComponentFactory<TextComponentComponent> factory : factories) {
                addComponent(factory.create(0, constructionY));
            }
        }
    }

    private void addComponent(CompendiumComponent component) {
        constructionY += component.getHeight() + LINE_GAP;
        components.add(component);
    }

    public static Optional<CompendiumEntry> fromSource(File source) {
        return Optional.ofNullable(entryMap.computeIfAbsent(source.toString(), src -> {
            try {
                return new CompendiumEntry(String.join("\n", Files.readAllLines(new File(src).toPath())));
            } catch(IOException e) {
                return null;
            }
        }));
    }

    public static Optional<CompendiumEntry> fromResourceLocation(ResourceLocation location) {
        return Optional.ofNullable(entryMap.computeIfAbsent(location.toString(), loc -> {
            InputStream stream;
            try {
                stream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(loc)).getInputStream();
            } catch(IOException e) {
                return null;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            return new CompendiumEntry(br.lines().collect(Collectors.joining("\n")));
        }));
    }

    private static Function<String, String> regexSerializer = new Function<String, String>() {
        private Pattern regex = Pattern.compile("[.^$*+?()\\[{\\\\|]");

        @Override
        public String apply(String s) {
            return regex.matcher(s).replaceAll("\\\\$0");
        }
    };

    private static BiFunction<String, String, Pattern> linkChecker = (url, branch) ->
            Pattern.compile("(" +
                    regexSerializer.apply(Config.url) +
                    "/blob/" +
                    regexSerializer.apply(Config.branch) +
                    "/)?(?<relative>.+)"
            );

    public static Optional<CompendiumEntry> fromLink(String link) {
        Matcher matcher = linkChecker.apply(Config.url, Config.branch).matcher(link);
        if(!matcher.matches()) {
            return Optional.empty();
        }
        return fromSource(new File(GitLoader.DIR, matcher.group("relative")));
    }

    public void draw() {
        for(CompendiumComponent component : components) {
            component.draw();
        }
    }

}
