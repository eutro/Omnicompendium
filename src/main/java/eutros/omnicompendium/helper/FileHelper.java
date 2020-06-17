package eutros.omnicompendium.helper;

import eutros.omnicompendium.Omnicompendium;
import eutros.omnicompendium.loader.GitLoader;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileHelper {

    private static final Pattern CAMEL_SPLITTER = Pattern.compile("([a-z])([A-Z])");

    public static Stream<File> getEntries() {
        try {
            Path root = GitLoader.DIR.toPath();
            return Files.walk(root, FileVisitOption.FOLLOW_LINKS)
                    .filter(path -> !path.getParent().equals(root))
                    .filter(path -> !root.relativize(path).toString().startsWith("."))
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> FilenameUtils.getExtension(f.getName()).toLowerCase().equals("md"));
        } catch(IOException e) {
            return Stream.empty();
        }
    }

    public static List<Pair<Path, BufferedImage>> getImages() {
        try {
            Path root = GitLoader.DIR.toPath();
            return Files.walk(root, FileVisitOption.FOLLOW_LINKS)
                    .filter(path -> !root.relativize(path).toString().startsWith("."))
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .map(file -> {
                        try {
                            BufferedImage image = ImageIO.read(file);
                            if(image == null) return null;
                            return Pair.of(file.toPath(), image);
                        } catch(IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch(IOException e) {
            return Collections.emptyList();
        } catch(Throwable e) {
            Omnicompendium.LOGGER.error("Caught exception from image loading.", e);
            return Collections.emptyList();
        }
    }

    @Nonnull
    public static Optional<Path> getRelative(@Nullable File source, String relativePath) {
        Path parent = null;
        if(source != null) {
            parent = source.toPath().getParent();
        }
        if(parent == null) {
            parent = GitLoader.DIR.toPath();
        }

        try {
            return Optional.of(parent.resolve(URLDecoder.decode(relativePath, StandardCharsets.UTF_8.name())));
        } catch(InvalidPathException | UnsupportedEncodingException e) {
            return Optional.empty();
        }
    }

    public static String fileNameToTitle(@Nonnull File file) {
        String name = FilenameUtils.getBaseName(file.getName());
        if(name.contains("_")) { // snake_case
            name = String.join(" ", name.split("_"));
        } else if(name.contains("-")) { // hyphen-case
            name = String.join(" ", name.split("-"));
        } else if(!name.contains(" ")) { // camelCase/PascalCase
            name = CAMEL_SPLITTER.matcher(name).replaceAll("$1 $2");
        } else {
            return name;
        }
        return WordUtils.capitalizeFully(name);
    }

}
