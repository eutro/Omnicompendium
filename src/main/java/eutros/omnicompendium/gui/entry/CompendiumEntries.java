package eutros.omnicompendium.gui.entry;

import eutros.omnicompendium.helper.FileHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CompendiumEntries {

    public static class Entries {

        public static final CompendiumEntry BROKEN = new CompendiumEntry(I18n.format("omnicompendium.entry.broken"), null);

    }

    public static final String UNTITLED = I18n.format("omnicompendium.entry.untitled");

    private static Map<String, CompendiumEntry> entryMap = new HashMap<>();
    public static final List<CompendiumEntry> listEntries = new ArrayList<>();

    private static Pattern serializer = Pattern.compile("[.^$*+?()\\[{\\\\|]");

    private static String serializeRegex(String s) {
        return serializer.matcher(s).replaceAll("\\\\$0");
    }

    public static Pattern linkChecker;

    public static void setLinkChecker(String url) {
        linkChecker = Pattern.compile("(" +
                serializeRegex(url) +
                "/blob/.+?/)?(?<relative>([a-zA-Z_\\-\\s0-9.]+)\\.md)$");
    }

    public static Optional<CompendiumEntry> fromSource(File source) {
        return Optional.ofNullable(entryMap.computeIfAbsent(source.toString(), src -> {
            try {
                return new CompendiumEntry(String.join("\n", Files.readAllLines(new File(src).toPath())), source);
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
            return new CompendiumEntry(br.lines().collect(Collectors.joining("\n")), null);
        }));
    }

    public static Optional<CompendiumEntry> fromLink(String link, @Nullable File source) {
        Matcher matcher = linkChecker.matcher(link);
        if(!matcher.matches()) {
            return Optional.empty();
        }
        String relativePath = matcher.group("relative");

        return FileHelper.getRelative(source, relativePath)
                .map(Path::toFile)
                .flatMap(CompendiumEntries::fromSource);
    }

    public static void refresh() {
        entryMap.clear();
        listEntries.clear();
        new Thread(() ->
                FileHelper.getEntries()
                        .map(CompendiumEntries::fromSource)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(entry -> {
                            synchronized(listEntries) {
                                int index = Collections.binarySearch(listEntries, entry, Comparator.comparing(CompendiumEntry::getTitle));
                                if(index < 0) index = -index - 1;

                                listEntries.add(index, entry);
                            }
                        })
        ).start();
    }

}
