package eutros.omnicompendium.helper;

import eutros.omnicompendium.loader.GitLoader;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileHelper {

    public static List<File> getEntries() {
        try {
            return Files.walk(new File(GitLoader.DIR, "guides").toPath(), FileVisitOption.FOLLOW_LINKS)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> f.toString().toLowerCase().endsWith(".md"))
                    .collect(Collectors.toList());
        } catch(IOException e) {
            return Collections.emptyList();
        }
    }

    public static List<Pair<File, BufferedImage>> getImages() {
        try {
            Path git = (new File(GitLoader.DIR, ".git")).toPath();
            return Files.walk(GitLoader.DIR.toPath(), FileVisitOption.FOLLOW_LINKS)
                    .filter(path -> !path.startsWith(git))
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .map(file -> {
                        try {
                            BufferedImage image = ImageIO.read(file);
                            if(image == null) return null;
                            return Pair.of(file, image);
                        } catch(IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch(IOException e) {
            return Collections.emptyList();
        }
    }

    @Nonnull
    public static File getRelative(@Nullable File source, String relativePath) {
        String parent = null;
        if(source != null) {
            parent = source.getParent();
        }
        if(parent == null) {
            parent = GitLoader.DIR.getPath();
        }
        return new File(parent, relativePath);
    }

}
