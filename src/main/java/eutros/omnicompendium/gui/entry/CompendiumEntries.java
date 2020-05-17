package eutros.omnicompendium.gui.entry;

import eutros.omnicompendium.Config;
import eutros.omnicompendium.loader.GitLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CompendiumEntries {

    public static final CompendiumEntry BROKEN = new CompendiumEntry(I18n.format("omnicompendium.entry.broken"));

    private static Map<String, CompendiumEntry> entryMap = new HashMap<>();
    private static Pattern regex = Pattern.compile("[.^$*+?()\\[{\\\\|]");

    private static String serializeRegex(String s) {
        return regex.matcher(s).replaceAll("\\\\$0");
    }

    private static Pattern getLinkChecker(String url, String branch) {
        return Pattern.compile("(" +
                serializeRegex(url) +
                "/blob/" +
                serializeRegex(branch) +
                "/)?(?<relative>(\\\\[a-z_\\-\\s0-9.]+)+(\\.(txt|md))?)$");
    }

    public static Optional<CompendiumEntry> fromSource(File source) {
        return Optional.ofNullable(entryMap.computeIfAbsent(source.toString(), src -> {
            try {
                CompendiumEntry entry = new CompendiumEntry(String.join("\n", Files.readAllLines(new File(src).toPath())));
                entry.source = source;
                return entry;
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

    public static Optional<CompendiumEntry> fromLink(String link, @Nullable File source) {
        Matcher matcher = getLinkChecker(Config.url, Config.branch).matcher(link);
        if(!matcher.matches()) {
            return Optional.empty();
        }
        String relativePath = matcher.group("relative");
        String parent = null;
        if(source != null) {
            parent = source.getParent();
        }
        if(parent == null) {
            parent = GitLoader.DIR.getPath();
        }

        return fromSource(new File(parent, relativePath));
    }

}
